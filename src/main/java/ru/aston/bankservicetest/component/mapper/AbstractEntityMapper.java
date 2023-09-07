package ru.aston.bankservicetest.component.mapper;

import ru.aston.bankservicetest.model.entity.AbstractEntity;

public interface AbstractEntityMapper<E extends AbstractEntity, D> {
    D map(E entity);
}
