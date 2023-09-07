package ru.aston.bankservicetest.exception;

public class BankServiceException extends Exception{

    public BankServiceException(String message) {
        super(message);
    }

    public BankServiceException(Throwable cause) {
        super(cause);
    }
}
