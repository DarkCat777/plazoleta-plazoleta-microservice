package com.pragma.plazoleta.infrastructure.adapter.output.client;

import com.pragma.plazoleta.infrastructure.adapter.output.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users-microservice", url = "${users.client.url}")
public interface UserClient {

    @GetMapping("/api/v1/users/{id}")
    UserResponse getUserById(@PathVariable Long id);
}
