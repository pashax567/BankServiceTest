package ru.aston.bankservicetest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class AccountServiceTest {

    @MockBean
    private AccountMapper accountMapper;

    @MockBean
    private AccountRepo accountRepo;

    @MockBean
    private BeneficiaryRepo beneficiaryRepo;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private TransactionHistoryService transactionHistoryService;

    @Autowired
    private AccountService accountService;

    private final String SIMPLE_PASS = "1234";
    private final String SIMPLE_PASS_HASH = SIMPLE_PASS + "_hash";
    private final Long AMOUNT = 10L;
    private final Long FIRST_ACC_NUMBER = 1L;
    private final Long SECOND_ACC_NUMBER = 2L;
    private final String FIRST_BENEFICIARY_NAME = "first_ben";
    private final String SECOND_BENEFICIARY_NAME = "second_ben";
    private final Beneficiary FIRST_BENEFICIARY = new Beneficiary(FIRST_BENEFICIARY_NAME, null);
    private final Beneficiary SECOND_BENEFICIARY = new Beneficiary(SECOND_BENEFICIARY_NAME, null);
    private final Account FIRST_ACCOUNT = new Account(FIRST_ACC_NUMBER, FIRST_BENEFICIARY, SIMPLE_PASS_HASH, 30L);
    private final Account SECOND_ACCOUNT = new Account(SECOND_ACC_NUMBER, SECOND_BENEFICIARY, SIMPLE_PASS_HASH, 30L);

    @BeforeEach
    void setUp() {
        Mockito.when(passwordEncoder.encode(SIMPLE_PASS)).thenReturn(SIMPLE_PASS_HASH);
        Mockito.when(passwordEncoder.matches(SIMPLE_PASS, SIMPLE_PASS_HASH)).thenReturn(true);
        Mockito.when(accountRepo.findByNumber(FIRST_ACC_NUMBER)).thenReturn(Optional.of(FIRST_ACCOUNT));
        Mockito.when(accountRepo.findByNumber(SECOND_ACC_NUMBER)).thenReturn(Optional.of(SECOND_ACCOUNT));

        Mockito.doNothing().when(transactionHistoryService)
                .addTransactionHistory(null, FIRST_ACCOUNT, EOperationType.DEPOSIT, AMOUNT);
        Mockito.doNothing().when(transactionHistoryService)
                .addTransactionHistory(FIRST_ACCOUNT, null, EOperationType.WITHDRAW, AMOUNT);
        Mockito.doNothing().when(transactionHistoryService)
                .addTransactionHistory(FIRST_ACCOUNT, SECOND_ACCOUNT, EOperationType.TRANSFER, AMOUNT);

    }

    @Test
    void createAccount() {
        final AccountDto expectedAccountDto = new AccountDto(1L, FIRST_ACC_NUMBER, FIRST_BENEFICIARY_NAME, 0L);

        Mockito.when(beneficiaryRepo.findByUsername(FIRST_BENEFICIARY_NAME)).thenReturn(Optional.of(FIRST_BENEFICIARY));
        Mockito.when(accountMapper.map(FIRST_ACCOUNT)).thenReturn(expectedAccountDto);

        final AccountDto actualAccountDto = accountService.createAccount(FIRST_BENEFICIARY_NAME, SIMPLE_PASS);

        assertEquals(expectedAccountDto.id(), actualAccountDto.id());
        assertEquals(expectedAccountDto.number(), actualAccountDto.number());
        assertEquals(expectedAccountDto.beneficiary(), actualAccountDto.beneficiary());
        assertEquals(expectedAccountDto.balance(), actualAccountDto.balance());

        assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(null, null));
        assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(FIRST_BENEFICIARY_NAME, null));
        assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(null, SIMPLE_PASS));
        assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(FIRST_BENEFICIARY_NAME, "12345"));
    }

    @Test
    void depositAndSaveHistory() throws BankServiceException {
        final Long oldBalance = FIRST_ACCOUNT.getBalance();
        accountService.depositAndSaveHistory(FIRST_ACC_NUMBER, AMOUNT);

        assertEquals(oldBalance + AMOUNT, FIRST_ACCOUNT.getBalance());
        assertThrows(AccountNotFoundException.class, () -> accountService.depositAndSaveHistory(3L, AMOUNT));
        assertThrows(BankServiceException.class, () -> accountService.depositAndSaveHistory(null, AMOUNT));
        assertThrows(BankServiceException.class, () -> accountService.depositAndSaveHistory(FIRST_ACC_NUMBER, -5L));
        assertThrows(BankServiceException.class, () -> accountService.depositAndSaveHistory(FIRST_ACC_NUMBER, null));
    }

    @Test
    void withDrawAndSaveHistory() throws BankServiceException {
        final Long oldBalance = FIRST_ACCOUNT.getBalance();
        accountService.withdrawAndSaveHistory(FIRST_ACC_NUMBER, AMOUNT, SIMPLE_PASS);

        assertEquals(oldBalance - AMOUNT, FIRST_ACCOUNT.getBalance());
        assertThrows(AccountNotFoundException.class, () -> accountService.withdrawAndSaveHistory(3L, AMOUNT, SIMPLE_PASS));
        assertThrows(BankServiceException.class, () -> accountService.withdrawAndSaveHistory(null, AMOUNT, SIMPLE_PASS));
        assertThrows(InsufficientFundsException.class, () -> accountService.withdrawAndSaveHistory(FIRST_ACC_NUMBER, AMOUNT * 10, SIMPLE_PASS));
        assertThrows(BankServiceException.class, () -> accountService.withdrawAndSaveHistory(FIRST_ACC_NUMBER, AMOUNT , "1111"));
        assertThrows(BankServiceException.class, () -> accountService.withdrawAndSaveHistory(FIRST_ACC_NUMBER, -5L, SIMPLE_PASS));
        assertThrows(BankServiceException.class, () -> accountService.withdrawAndSaveHistory(FIRST_ACC_NUMBER, null, SIMPLE_PASS));

    }

    @Test
    void transferAndSaveHistory() throws BankServiceException {
        final Long firstAccOldBalance = FIRST_ACCOUNT.getBalance();
        final Long secondAccOldBalance = SECOND_ACCOUNT.getBalance();
        accountService.transferAndSaveHistory(FIRST_ACC_NUMBER, SECOND_ACC_NUMBER, AMOUNT, SIMPLE_PASS);

        assertEquals(firstAccOldBalance - AMOUNT, FIRST_ACCOUNT.getBalance());
        assertEquals(secondAccOldBalance + AMOUNT, SECOND_ACCOUNT.getBalance());
        assertThrows(AccountNotFoundException.class, () -> accountService.transferAndSaveHistory(3L, SECOND_ACC_NUMBER, AMOUNT, SIMPLE_PASS));
        assertThrows(AccountNotFoundException.class, () -> accountService.transferAndSaveHistory(FIRST_ACC_NUMBER, 3L, AMOUNT, SIMPLE_PASS));
        assertThrows(InsufficientFundsException.class, () -> accountService.transferAndSaveHistory(FIRST_ACC_NUMBER, 3L, AMOUNT * 10, SIMPLE_PASS));
        assertThrows(BankServiceException.class, () -> accountService.transferAndSaveHistory(FIRST_ACC_NUMBER, SECOND_ACC_NUMBER, AMOUNT, "1111"));
        assertThrows(BankServiceException.class, () -> accountService.transferAndSaveHistory(FIRST_ACC_NUMBER, SECOND_ACC_NUMBER, -5L, SIMPLE_PASS));
        assertThrows(BankServiceException.class, () -> accountService.transferAndSaveHistory(FIRST_ACC_NUMBER, null, AMOUNT, SIMPLE_PASS));
        assertThrows(BankServiceException.class, () -> accountService.transferAndSaveHistory(null, SECOND_ACC_NUMBER, AMOUNT, SIMPLE_PASS));
    }
}