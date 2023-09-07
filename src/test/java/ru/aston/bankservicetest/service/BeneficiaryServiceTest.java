package ru.aston.bankservicetest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.aston.bankservicetest.component.mapper.BeneficiaryMapper;
import ru.aston.bankservicetest.exception.BeneficiaryNotFoundException;
import ru.aston.bankservicetest.model.dto.BeneficiaryDto;
import ru.aston.bankservicetest.model.entity.Beneficiary;
import ru.aston.bankservicetest.repo.BeneficiaryRepo;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeneficiaryServiceTest {

    @MockBean
    private BeneficiaryRepo beneficiaryRepo;

    @MockBean
    private BeneficiaryMapper beneficiaryMapper;

    @Autowired
    private BeneficiaryService beneficiaryService;

    private final String BENEFICIARY_NAME = "test_username";
    private final String NON_EXISTENT_BENEFICIARY_NAME = "not beneficiary";
    private final Beneficiary BENEFICIARY = new Beneficiary(BENEFICIARY_NAME, null);
    private final BeneficiaryDto BENEFICIARY_DTO = new BeneficiaryDto(1L, BENEFICIARY_NAME, new ArrayList<>());

    @BeforeEach
    void setUp() {
        Mockito.when(beneficiaryMapper.map(BENEFICIARY)).thenReturn(BENEFICIARY_DTO);
        Mockito.when(beneficiaryRepo.findByUsername(BENEFICIARY_NAME)).thenReturn(Optional.of(BENEFICIARY));
        Mockito.when(beneficiaryRepo.findByUsername(NON_EXISTENT_BENEFICIARY_NAME)).thenReturn(Optional.empty());
    }

    @Test
    void findByUsername() throws BeneficiaryNotFoundException {
        final BeneficiaryDto beneficiaryDto = beneficiaryService.findByUsername(BENEFICIARY_NAME);
        assertEquals(BENEFICIARY_NAME, beneficiaryDto.userName());
        assertThrows(BeneficiaryNotFoundException.class, () -> beneficiaryService.findByUsername(NON_EXISTENT_BENEFICIARY_NAME));
    }
}