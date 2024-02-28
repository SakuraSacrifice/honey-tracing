package com.honey.tracing.example.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolExecutorConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(
                1,
                1,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable runnable) {
                        return new Thread(runnable, "Pool-Thread-1");
                    }
                });
    }

    @Bean
    public ExecutorService wrappedThreadPool() {
        return TtlExecutors.getTtlExecutorService(new ThreadPoolExecutor(
                1,
                1,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable runnable) {
                        return new Thread(runnable, "Pool-Thread-1");
                    }
                }));
    }

    @Bean
    public ScheduledExecutorService wrappedScheduledThreadPool() {
        return TtlExecutors.getTtlScheduledExecutorService(new ScheduledThreadPoolExecutor(
                1,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable runnable) {
                        return new Thread(runnable, "Pool-Thread-1");
                    }
                }));
    }

}