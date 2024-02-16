package com.honey.tracing.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaController {

    private static final String TEST_TOPIC = "testTopic";
    private static final String TEST_MESSAGE = "testMessage";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/kafka/send")
    public void send(String url) {
        kafkaTemplate.send(TEST_TOPIC, TEST_MESSAGE);
    }

}