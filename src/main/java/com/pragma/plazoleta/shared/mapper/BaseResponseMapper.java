package com.pragma.plazoleta.shared.mapper;

public interface BaseResponseMapper<D, R> {
    R toResponse(D domain);

    D toDomain(R response);
}
