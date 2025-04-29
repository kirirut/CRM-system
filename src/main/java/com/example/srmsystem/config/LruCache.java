package com.example.srmsystem.config;

import java.util.*;

public class LruCache<K, V> {
    private final Map<K, V> cache;

    public LruCache(int maxSize) {
        this.cache = new LinkedHashMap<K, V>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxSize;
            }
        };
    }

    public synchronized V get(K key) {
        return cache.get(key);
    }

    public synchronized void put(K key, V value) {
        cache.put(key, value);
    }

    public synchronized void remove(K key) {
        cache.remove(key);
    }

    public synchronized Collection<V> getAll() {
        return new ArrayList<>(cache.values());
    }
}
