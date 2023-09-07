package ru.aston.bankservicetest.model.dto;

public record BankOperationRequest(Long accNumberFrom, Long accNumberTo, Long amount, String pinCode) {
}
