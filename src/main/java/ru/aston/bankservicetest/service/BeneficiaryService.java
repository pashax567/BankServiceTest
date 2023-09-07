package ru.aston.bankservicetest.service;

import ru.aston.bankservicetest.exception.BeneficiaryNotFoundException;
import ru.aston.bankservicetest.model.dto.BeneficiaryDto;

import java.util.List;

public interface BeneficiaryService {

    List<BeneficiaryDto> getAll();

    BeneficiaryDto findByUsername(String username) throws BeneficiaryNotFoundException;
}
