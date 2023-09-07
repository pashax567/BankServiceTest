package ru.aston.bankservicetest.component.mapper;

import org.springframework.stereotype.Component;
import ru.aston.bankservicetest.model.dto.TransactionHistoryDto;
import ru.aston.bankservicetest.model.entity.TransactionHistory;

@Component
public class TransactionHistoryMapperImpl implements TransactionHistoryMapper{
    @Override
    public TransactionHistoryDto map(TransactionHistory entity) {
        return new TransactionHistoryDto(
                entity.getAccountFrom() != null ? entity.getAccountFrom().getNumber() : null,
                entity.getAccountTo() != null ? entity.getAccountTo().getNumber() : null,
                entity.getOperationType(),
                entity.getAmount(),
                entity.getTransactionDate()
        );
    }
}
