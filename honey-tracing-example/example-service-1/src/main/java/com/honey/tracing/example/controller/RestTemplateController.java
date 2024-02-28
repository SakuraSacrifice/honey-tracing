package com.honey.tracing.example.controller;

import com.alibaba.ttl.TtlRunnable;
import com.honey.tracing.example.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;

@RestController
public class RestTemplateController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private ExecutorService wrappedThreadPool;

    @Autowired
    private ScheduledExecutorService wrappedScheduledThreadPool;

    @Autowired
    private AsyncService asyncService;

    @GetMapping("/send")
    public void send(String url) {
        restTemplate.getForEntity(url, Void.class);
    }

    @GetMapping("/async/thread/send")
    public void syncSendByThread(String url) throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                restTemplate.getForEntity(url, Void.class);
                countDownLatch.countDown();
            }
        }).start();
        countDownLatch.await();
    }

    @GetMapping("/async/thread-pool/send")
    public void asyncSendByThreadPool(String url) throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        threadPoolExecutor.execute(TtlRunnable.get(new Runnable() {
            @Override
            public void run() {
                restTemplate.getForEntity(url, Void.class);
                countDownLatch.countDown();
            }
        }));
        countDownLatch.await();
    }

    @GetMapping("/async/wrapped-thread-pool/send")
    public void asyncSendByWrappedThreadPool(String url) throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        wrappedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                restTemplate.getForEntity(url, Void.class);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    @GetMapping("/async/wrapped-scheduled-thread-pool/send")
    public void asyncSendByWrappedScheduledThreadPool(String url) throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        wrappedScheduledThreadPool.schedule(new Runnable() {
            @Override
            public void run() {
                restTemplate.getForEntity(url, Void.class);
                countDownLatch.countDown();
            }
        }, 1, TimeUnit.SECONDS);
        countDownLatch.await();
    }

    @GetMapping("/async/annotation/send")
    public void asyncSendByAsyncAnnotation(String url) throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        asyncService.send(restTemplate, countDownLatch, url);
        countDownLatch.await();
    }

}