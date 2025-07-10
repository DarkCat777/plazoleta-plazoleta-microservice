package com.pragma.plazoleta.shared.mapper;

public interface BaseEntityMapper<D, E> {

    D toDomain(E entity);

    E toEntity(D domain);

}
