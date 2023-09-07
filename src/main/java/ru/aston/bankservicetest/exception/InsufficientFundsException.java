package ru.aston.bankservicetest.exception;

public class InsufficientFundsException extends BankServiceException {

    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException(Long accNum) {
        this(String.format("Insufficient funds in the account with number %s", accNum));
    }

}
