package ru.aston.bankservicetest.service;

import ru.aston.bankservicetest.exception.BankServiceException;
import ru.aston.bankservicetest.model.dto.AccountDto;

public interface AccountService {
    AccountDto createAccount(String beneficiaryName, String pinCode);

    void depositAndSaveHistory(Long accNumber, Long amount) throws BankServiceException;

    void withdrawAndSaveHistory(Long accNumber, Long amount, String pinCode) throws BankServiceException;

    void transferAndSaveHistory(Long accNumberFrom, Long accNumberTo, Long amount, String pinCode) throws BankServiceException;
}
