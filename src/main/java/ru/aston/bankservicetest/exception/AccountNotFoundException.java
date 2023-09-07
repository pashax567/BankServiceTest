package ru.aston.bankservicetest.exception;

public class AccountNotFoundException extends NotFoundException{
    public AccountNotFoundException(Long accNumber) {
        super(String.format("Account with number %s not found", accNumber));
    }
}
