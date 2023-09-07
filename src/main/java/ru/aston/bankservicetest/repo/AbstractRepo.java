package ru.aston.bankservicetest.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractRepo<T> extends CrudRepository<T, Long> {
}
