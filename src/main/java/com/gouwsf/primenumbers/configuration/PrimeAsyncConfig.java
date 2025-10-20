package com.gouwsf.primenumbers.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class PrimeAsyncConfig {
    @Bean(destroyMethod = "shutdown")
    public ExecutorService primeExecutor() {
        int cores = Math.max(1, Runtime.getRuntime().availableProcessors());
        return Executors.newFixedThreadPool(cores);
    }
}
