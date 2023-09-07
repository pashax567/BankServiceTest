package ru.aston.bankservicetest.exception;

public class BeneficiaryNotFoundException extends NotFoundException{
    public BeneficiaryNotFoundException(String username) {
        super(String.format("Beneficiary with username %s not found", username));
    }
}
