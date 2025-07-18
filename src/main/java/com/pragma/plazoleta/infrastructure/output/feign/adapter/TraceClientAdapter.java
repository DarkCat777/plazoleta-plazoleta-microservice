package com.pragma.plazoleta.infrastructure.output.feign.adapter;

import com.pragma.plazoleta.domain.model.OrderTrace;
import com.pragma.plazoleta.domain.spi.TraceClientPort;
import com.pragma.plazoleta.infrastructure.output.feign.client.TraceFeignClient;
import com.pragma.plazoleta.infrastructure.output.feign.mapper.OrderTraceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TraceClientAdapter implements TraceClientPort {

    private final TraceFeignClient traceFeignClient;
    private final OrderTraceMapper orderTraceMapper;

    @Override
    public void registerOrderTrace(OrderTrace orderTrace) {
        traceFeignClient.registerOrderTrace(orderTraceMapper.toRequest(orderTrace));
    }

}
