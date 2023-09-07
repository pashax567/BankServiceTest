package ru.aston.bankservicetest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.bankservicetest.component.mapper.AccountMapper;
import ru.aston.bankservicetest.exception.AccountNotFoundException;
import ru.aston.bankservicetest.exception.BankServiceException;
import ru.aston.bankservicetest.exception.InsufficientFundsException;
import ru.aston.bankservicetest.model.dto.AccountDto;
import ru.aston.bankservicetest.model.entity.Account;
import ru.aston.bankservicetest.model.entity.Beneficiary;
import ru.aston.bankservicetest.model.type.EOperationType;
import ru.aston.bankservicetest.repo.AccountRepo;
import ru.aston.bankservicetest.repo.BeneficiaryRepo;

import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepo accountRepo;
    private final BeneficiaryRepo beneficiaryRepo;
    private final TransactionHistoryService transactionHistoryService;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AccountDto createAccount(String beneficiaryName, String pinCode) {
        if (StringUtils.isEmpty(beneficiaryName) || StringUtils.isEmpty(pinCode)) {
            final String errorMessage = "Arguments (beneficiaryName or PIN code) are null or empty.";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        if (!Pattern.matches("\\d{4}", pinCode)) {
            final String errorMessage = "The PIN code must contains 4 digits";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        final Beneficiary beneficiary = beneficiaryRepo.findByUsername(beneficiaryName)
                .orElse(
                        Beneficiary.builder()
                                .username(beneficiaryName)
                                .build()
                );

        final Long accountNumber = new Random().nextLong(0, Long.MAX_VALUE);

        final Account account = new Account(accountNumber, beneficiary, passwordEncoder.encode(pinCode), 0L);
        accountRepo.save(account);
        log.info("Account with number {} created", accountNumber);
        return accountMapper.map(account);
    }

    /**
     * Увеличение депозита и сохранение транзакции в историю
     *
     * @param accNumber Номер счета
     * @param amount    Сумма пополнения (>0)
     * @throws BankServiceException Ошибка пополнения
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 15)
    public void depositAndSaveHistory(Long accNumber, Long amount) throws BankServiceException {
        try {
            final Account account = depositAndGet(accNumber, amount);
            saveTransactionHistory(account, EOperationType.DEPOSIT, amount);

            log.info("The account with the number {} was replenished with the amount {}", accNumber, amount);
        } catch (AccountNotFoundException e) {
            log.error(e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw new BankServiceException(e);
        }
    }

    private Account depositAndGet(Long accNumber, Long amount) throws AccountNotFoundException {
        if (accNumber == null)
            throw new IllegalArgumentException("Account number can't be null");

        if (amount == null || amount <= 0)
            throw new IllegalArgumentException("Amount can't be null or less than 0");

        final Account account = accountRepo
                .findByNumber(accNumber)
                .orElseThrow(() -> new AccountNotFoundException(accNumber));

        final Long currentBalance = account.getBalance();
        account.setBalance(currentBalance + amount);
        return account;
    }

    /**
     * Списание средств со счета и сохранение в историю
     *
     * @param accNumber Номер счета
     * @param amount    Сумма списания (>0)
     * @param pinCode   PIN код счета
     * @throws BankServiceException Ошибка списания
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 15)
    public void withdrawAndSaveHistory(Long accNumber, Long amount, String pinCode) throws BankServiceException {
        try {
            final Account account = withdrawAndGet(accNumber, amount, pinCode);
            saveTransactionHistory(account, EOperationType.WITHDRAW, amount);

            log.info("The account with the number {} was debited {}", accNumber, amount);
        } catch (BankServiceException e) {
            log.error(e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw new BankServiceException(e);
        }
    }

    private Account withdrawAndGet(Long accNumber, Long amount, String pinCode) throws BankServiceException {
        if (accNumber == null)
            throw new IllegalArgumentException("Account number can't be null");

        if (amount == null || amount <= 0)
            throw new IllegalArgumentException("Amount can't be null or less than 0");

        final Account account = accountRepo
                .findByNumber(accNumber)
                .orElseThrow(() -> new AccountNotFoundException(accNumber));

        if (!passwordEncoder.matches(pinCode, account.getPinCode()))
            throw new BankServiceException(String.format("Incorrect PIN code for account with number %s", accNumber));

        final Long currentBalance = account.getBalance();
        if (account.getBalance() < amount)
            throw new InsufficientFundsException(accNumber);

        account.setBalance(currentBalance - amount);
        return account;
    }

    /**
     * Перевод с одного счета на другой и сохранение транзакции в историю
     *
     * @param accNumberFrom Номер счета списания
     * @param accNumberTo   Номер счета пополнения
     * @param amount        Сумма перевода
     * @param pinCode       PIN код счета списания
     * @throws BankServiceException Ошибка перевода
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 15)
    public void transferAndSaveHistory(Long accNumberFrom, Long accNumberTo, Long amount, String pinCode) throws BankServiceException {
        try {
            if (Objects.equals(accNumberFrom, accNumberTo))
                throw new IllegalArgumentException("Same account number");
            final Account accountFrom = withdrawAndGet(accNumberFrom, amount, pinCode);
            final Account accountTo = depositAndGet(accNumberTo, amount);

            saveTransactionHistory(accountFrom, accountTo, EOperationType.TRANSFER, amount);

            log.info("An Amount ({}) was sent from the account with number {} to the account with number {}",
                    amount, accountFrom, accountTo);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw new BankServiceException(e);
        } catch (BankServiceException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private void saveTransactionHistory(Account account, EOperationType operationType, Long amount) {
        switch (operationType) {
            case DEPOSIT -> saveTransactionHistory(null, account, operationType, amount);
            case WITHDRAW -> saveTransactionHistory(account, null, operationType, amount);
            default -> throw new IllegalStateException("You must specify 2 accounts");
        }
    }

    private void saveTransactionHistory(Account accountFrom, Account accountTo, EOperationType operationType, Long amount) {
        transactionHistoryService.addTransactionHistory(accountFrom, accountTo, operationType, amount);
    }

}
