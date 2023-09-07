package ru.aston.bankservicetest.service;

import ru.aston.bankservicetest.model.dto.TransactionHistoryDto;
import ru.aston.bankservicetest.model.entity.Account;
import ru.aston.bankservicetest.model.type.EOperationType;

import java.util.List;

public interface TransactionHistoryService {
    void addTransactionHistory(Account accountFrom, Account accountTo, EOperationType operationType, Long amount);

    List<TransactionHistoryDto> getAllTransactionHistoryForAccount(Long accNumber);
}
