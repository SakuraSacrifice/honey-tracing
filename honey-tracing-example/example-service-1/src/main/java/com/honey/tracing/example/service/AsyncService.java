package com.honey.tracing.example.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;

@Service
public class AsyncService {

    @Async("wrappedThreadPool")
    public void send(RestTemplate restTemplate, CountDownLatch countDownLatch, String url) {
        restTemplate.getForEntity(url, Void.class);
        countDownLatch.countDown();
    }

}