package com.honey.tracing.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestTemplateController {

    @GetMapping("/receive")
    public void receive() {
        System.out.println();
    }

}