package com.pragma.plazoleta.infrastructure.output.feign.client;

import com.pragma.plazoleta.infrastructure.output.feign.model.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users-microservice", url = "${users.client.url}/users")
public interface UserFeignClient {

    @GetMapping("/{id}")
    UserResponse getUserById(@PathVariable Long id);
}
