package com.michael.document.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class CacheStore<K, V> {

    private final Cache<K, V> cache;   // Кэш для хранения значений

    public CacheStore(int expiryDuration, TimeUnit timeUnit) {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(expiryDuration, timeUnit)// продолжительность жизни записи в кэше
                .concurrencyLevel(Runtime.getRuntime().availableProcessors()) //Устанавливаем уровень конкуренции с учетом доступных процессоров
                .build();
    }

    // Метод для получения значения из кэша по ключу.
    public V get(@NotNull K key) {
        log.info("Retrieving from Cache with key: {}", key.toString());
        return cache.getIfPresent(key);
    }

    //Метод для добавления записи в кэш.
    public void put(@NotNull K key, @NotNull V value) {
        log.info("Storing record in cache for key {}", key.toString());
        cache.put(key, value);
    }

    // Метод для удаления записи из кэша по ключу.
    public void evict(@NotNull K key) {
        log.info("Removing from Cache with key {}", key.toString());
        cache.invalidate(key);
    }

}
