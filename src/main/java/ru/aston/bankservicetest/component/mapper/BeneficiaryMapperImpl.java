package ru.aston.bankservicetest.component.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.aston.bankservicetest.model.dto.AccountDto;
import ru.aston.bankservicetest.model.dto.BeneficiaryDto;
import ru.aston.bankservicetest.model.entity.Beneficiary;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BeneficiaryMapperImpl implements BeneficiaryMapper {

    private final AccountMapper accountMapper;
    @Override
    public BeneficiaryDto map(Beneficiary entity) {
        final List<AccountDto> accountDtoList = entity.getAccounts().stream().map(accountMapper::map).collect(Collectors.toList());
        return new BeneficiaryDto(entity.getId(), entity.getUsername(), accountDtoList);
    }
}
