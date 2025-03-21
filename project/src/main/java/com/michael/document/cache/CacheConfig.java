package com.michael.document.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    //Метод создаёт экземпляр класса CacheStore с параметрами для хранения времени жизни записей в 900 секунд.
    @Bean
    public CacheStore<String, Integer> userCache() {
        return new CacheStore<>(900, TimeUnit.SECONDS);
    }
}
