package ru.aston.bankservicetest.exception;

public class NotFoundException extends BankServiceException {
    public NotFoundException(String message) {
        super(message);
    }
}
