package com.honey.tracing.example.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "sned", url = "127.0.0.1:8081")
public interface SendClient {

    @GetMapping("/receive")
    void send();

}