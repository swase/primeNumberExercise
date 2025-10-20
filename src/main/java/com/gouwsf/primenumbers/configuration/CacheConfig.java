package com.gouwsf.primenumbers.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineSpec() {
        return Caffeine.newBuilder()
                .maximumWeight(100L * 1024 * 1024) // ~100 MB
                .weigher((Object key, Object value) -> {
                    if (value instanceof int[] arr) return arr.length * 4;
                    return 64;
                })
                .expireAfterAccess(2, TimeUnit.HOURS)
                .recordStats();
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager mgr = new CaffeineCacheManager("primesByAlgoAndLimit");
        mgr.setCaffeine(caffeine);
        mgr.setAllowNullValues(false);
        return mgr;
    }
}
