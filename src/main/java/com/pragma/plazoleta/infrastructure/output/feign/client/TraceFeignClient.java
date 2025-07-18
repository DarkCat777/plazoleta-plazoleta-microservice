package com.pragma.plazoleta.infrastructure.output.feign.client;

import com.pragma.plazoleta.infrastructure.output.feign.model.request.CreateOrderTraceCommand;
import com.pragma.plazoleta.infrastructure.output.feign.model.response.OrderTraceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "trace-microservice", url = "${feign.trace.client.url}/order-traces")
public interface TraceFeignClient {

    @PostMapping("/order")
    OrderTraceResponse registerOrderTrace(@RequestBody CreateOrderTraceCommand command);

}
