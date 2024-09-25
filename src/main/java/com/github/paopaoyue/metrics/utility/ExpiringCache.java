package com.github.paopaoyue.metrics.utility;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExpiringCache<K, V> {
    private final ConcurrentHashMap<K, CacheItem<V>> cache = new ConcurrentHashMap<>();
    private final long expirationTime;
    private final ScheduledExecutorService cleaner;

    private static class CacheItem<V> {
        V value;
        long expiryTime;

        CacheItem(V value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }
    }

    public ExpiringCache(long expirationTime, TimeUnit timeUnit) {
        this.expirationTime = timeUnit.toMillis(expirationTime);
        this.cleaner = Executors.newSingleThreadScheduledExecutor();
        startCleaner();
    }

    private void startCleaner() {
        cleaner.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            cache.forEach((key, item) -> {
                if (item.expiryTime < currentTime) {
                    cache.remove(key);
                }
            });
        }, expirationTime, expirationTime, TimeUnit.MILLISECONDS);
    }

    public void put(K key, V value) {
        long expiryTime = System.currentTimeMillis() + expirationTime;
        cache.put(key, new CacheItem<>(value, expiryTime));
    }

    public V get(K key) {
        CacheItem<V> item = cache.get(key);
        if (item != null && item.expiryTime >= System.currentTimeMillis()) {
            return item.value;
        }
        return null;
    }

    public void clear() {
        cache.clear();
    }

    public void shutdown() {
        cleaner.shutdown();
    }
}