package com.honey.tracing.example.controller;

import com.honey.tracing.example.client.SendClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeignClientController {

    @Autowired
    private SendClient sendClient;

    @GetMapping("/feign/send")
    public void send() {
        sendClient.send();
    }

}