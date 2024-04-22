package ru.korobtsov.server.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorsConfiguration {

    @Bean
    public ExecutorService executorService(@Value("${game.blockingPoolSize:8}") int blockingPoolSize) {
        return Executors.newFixedThreadPool(blockingPoolSize);
    }
}
