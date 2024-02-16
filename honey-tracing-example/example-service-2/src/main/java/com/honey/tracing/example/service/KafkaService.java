package com.honey.tracing.example.service;

import com.honey.tracing.kafka.consumer.interceptor.HoneyKafkaTracing;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    private static final String TEST_TOPIC = "testTopic";

    @HoneyKafkaTracing
    @KafkaListener(topics = TEST_TOPIC)
    public void onMessage(ConsumerRecord<String, String> consumerRecord) {
        System.out.println(consumerRecord.value());
    }

}