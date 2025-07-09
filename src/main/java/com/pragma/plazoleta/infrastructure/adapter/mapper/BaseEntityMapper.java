package com.pragma.plazoleta.infrastructure.adapter.mapper;

public interface BaseEntityMapper<D, E> {

    D toDomain(E entity);

    E toEntity(D domain);

}
