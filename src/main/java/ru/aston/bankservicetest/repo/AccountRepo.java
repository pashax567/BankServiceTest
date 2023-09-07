package ru.aston.bankservicetest.repo;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import ru.aston.bankservicetest.model.entity.Account;

import java.util.Optional;

public interface AccountRepo extends AbstractRepo<Account> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findByNumber(Long number);

}
