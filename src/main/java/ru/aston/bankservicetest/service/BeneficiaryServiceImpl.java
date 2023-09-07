package ru.aston.bankservicetest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.aston.bankservicetest.exception.BeneficiaryNotFoundException;
import ru.aston.bankservicetest.model.dto.BeneficiaryDto;
import ru.aston.bankservicetest.component.mapper.BeneficiaryMapper;
import ru.aston.bankservicetest.repo.BeneficiaryRepo;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final BeneficiaryRepo beneficiaryRepo;
    private final BeneficiaryMapper beneficiaryMapper;

    @Override
    public List<BeneficiaryDto> getAll() {
        return beneficiaryRepo.findAll().stream().map(beneficiaryMapper::map).collect(Collectors.toList());
    }

    @Override
    public BeneficiaryDto findByUsername(String username) throws BeneficiaryNotFoundException {
        return beneficiaryRepo.findByUsername(username)
                .map(beneficiaryMapper::map)
                .orElseThrow(() -> new BeneficiaryNotFoundException(username));
    }
}
