package com.pragma.plazoleta.infrastructure.output.feign.mapper;

import com.pragma.plazoleta.domain.model.OrderTrace;
import com.pragma.plazoleta.infrastructure.output.feign.model.request.CreateOrderTraceCommand;
import com.pragma.plazoleta.infrastructure.output.feign.model.response.OrderTraceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderTraceMapper {

    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeEmail", source = "employee.email")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerEmail", source = "customer.email")
    CreateOrderTraceCommand toRequest(OrderTrace orderTrace);

    @Mapping(target = "employee.id", source = "employeeId")
    @Mapping(target = "employee.email", source = "employeeEmail")
    @Mapping(target = "customer.id", source = "customerId")
    @Mapping(target = "customer.email", source = "customerEmail")
    OrderTrace toDomain(OrderTraceResponse orderTraceResponse);

}
