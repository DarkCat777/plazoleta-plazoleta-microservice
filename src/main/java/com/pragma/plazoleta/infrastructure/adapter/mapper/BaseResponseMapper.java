package com.pragma.plazoleta.infrastructure.adapter.mapper;

public interface BaseResponseMapper<D, R> {
    R toResponse(D domain);
}
