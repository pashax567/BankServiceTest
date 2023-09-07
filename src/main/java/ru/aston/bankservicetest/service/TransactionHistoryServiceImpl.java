package ru.aston.bankservicetest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.bankservicetest.component.mapper.TransactionHistoryMapper;
import ru.aston.bankservicetest.model.dto.TransactionHistoryDto;
import ru.aston.bankservicetest.model.entity.Account;
import ru.aston.bankservicetest.model.entity.TransactionHistory;
import ru.aston.bankservicetest.model.type.EOperationType;
import ru.aston.bankservicetest.repo.TransactionHistoryRepo;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionHistoryServiceImpl implements TransactionHistoryService {

    private final TransactionHistoryRepo transactionHistoryRepo;
    private final TransactionHistoryMapper transactionHistoryMapper;

    /**
     * Сохраняет каждую транзакцию в таблицу истории
     *
     * @param accountFrom   Аккаунт с которого были списаны средства
     * @param accountTo     Аккаунт на который были переведены средства
     * @param operationType Тип операции
     * @param amount        Сумма операции (>0)
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void addTransactionHistory(Account accountFrom, Account accountTo, EOperationType operationType, Long amount) {

        if (amount == null || amount <= 0)
            throw new IllegalArgumentException("Amount is null or less than 0");

        switch (operationType) {
            case DEPOSIT -> {
                if (accountTo == null)
                    throw new IllegalArgumentException("Account number can't be null");


                final TransactionHistory transactionHistory = new TransactionHistory(null, accountTo, operationType, amount);
                transactionHistoryRepo.save(transactionHistory);
            }
            case WITHDRAW -> {
                if (accountFrom == null)
                    throw new IllegalArgumentException("Account number can't be null");

                final TransactionHistory transactionHistory = new TransactionHistory(accountFrom, null, operationType, amount);
                transactionHistoryRepo.save(transactionHistory);
            }
            case TRANSFER -> {
                if (accountFrom == null || accountTo == null)
                    throw new IllegalArgumentException("Account number can't be null");

                final TransactionHistory transactionHistory = new TransactionHistory(accountFrom, accountTo, operationType, amount);
                transactionHistoryRepo.save(transactionHistory);
            }
        }
    }

    /**
     * Получение всей истории транзакций для конкретного счета.
     *
     * @param accNumber Номер счета
     * @return История операций
     */
    @Override
    public List<TransactionHistoryDto> getAllTransactionHistoryForAccount(Long accNumber) {
        return transactionHistoryRepo
                .findByAccountFrom_NumberOrAccountTo_Number(accNumber, accNumber)
                .stream()
                .map(transactionHistoryMapper::map)
                .collect(Collectors.toList());
    }
}
