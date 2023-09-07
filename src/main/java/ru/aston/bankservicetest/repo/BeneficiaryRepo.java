package ru.aston.bankservicetest.repo;

import ru.aston.bankservicetest.model.entity.Beneficiary;

import java.util.List;
import java.util.Optional;

public interface BeneficiaryRepo extends AbstractRepo<Beneficiary> {

    Optional<Beneficiary> findByUsername(String username);

    @Override
    List<Beneficiary> findAll();
}
