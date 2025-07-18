package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.OrderTrace;

public interface TraceClientPort {

    void registerOrderTrace(OrderTrace orderTrace);

}
