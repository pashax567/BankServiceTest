package ru.aston.bankservicetest.repo;

import ru.aston.bankservicetest.model.entity.TransactionHistory;

import java.util.List;

public interface TransactionHistoryRepo extends AbstractRepo<TransactionHistory> {

    List<TransactionHistory> findByAccountFrom_NumberOrAccountTo_Number(Long accFromNumber, Long accTONumber);
}
