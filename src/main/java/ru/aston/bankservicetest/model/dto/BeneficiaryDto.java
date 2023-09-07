package ru.aston.bankservicetest.model.dto;

import java.util.List;

public record BeneficiaryDto(Long id, String userName, List<AccountDto> accounts) {
}
