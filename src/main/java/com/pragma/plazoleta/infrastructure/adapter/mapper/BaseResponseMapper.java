package com.pragma.plazoleta.infrastructure.adapter.mapper;

public interface BaseResponseMapper<D, R> {
    R toResponse(D domain);

    D toDomain(R response);
}
