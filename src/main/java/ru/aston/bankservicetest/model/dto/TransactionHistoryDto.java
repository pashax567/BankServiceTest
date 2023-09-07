package ru.aston.bankservicetest.model.dto;

import ru.aston.bankservicetest.model.type.EOperationType;

import java.util.Date;

public record TransactionHistoryDto(Long accountFromNumber, Long accountToNumber, EOperationType operationType, Long amount, Date transactionDate) {
}
