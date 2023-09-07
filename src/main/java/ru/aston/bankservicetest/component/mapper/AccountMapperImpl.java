package ru.aston.bankservicetest.component.mapper;

import org.springframework.stereotype.Component;
import ru.aston.bankservicetest.model.dto.AccountDto;
import ru.aston.bankservicetest.model.entity.Account;

@Component
public class AccountMapperImpl implements AccountMapper{
    @Override
    public AccountDto map(Account entity) {
        return new AccountDto(entity.getId(), entity.getNumber(), entity.getBeneficiary().getUsername(), entity.getBalance());
    }
}
