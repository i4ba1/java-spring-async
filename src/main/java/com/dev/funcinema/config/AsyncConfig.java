package com.dev.funcinema.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Bean(name = "virtualThreadTaskExecutor")
    public Executor virtualThreadTaskExecutor() {
        // Creating an executor that uses virtual threads
        // Each task submitted to this executor will run in its own virtual thread
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        // This is a fallback executor using platform threads
        // We define this for compatibility and demonstration purposes
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("MovieApi-Thread-");
        executor.initialize();
        return executor;
    }
}
