package com.aroundvan.backend.common;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

//cache for weather provider limit
public class TtlCache<K, V> {

    private final Duration timeToLive;
    private final Map<K, Entry<V>> entries = new ConcurrentHashMap<>();

    public TtlCache(Duration timeToLive) {
        this.timeToLive = timeToLive;
    }

    public Optional<V> get(K key) {
        Entry<V> entry = entries.get(key);

        if (entry == null) {
            return Optional.empty();
        }

        if (entry.storedAt().plus(timeToLive).isBefore(Instant.now())) {
            entries.remove(key, entry);
            return Optional.empty();
        }

        return Optional.of(entry.value());
    }

    public void put(K key, V value) {
        entries.put(key, new Entry<>(value, Instant.now()));
    }

    private record Entry<V>(V value, Instant storedAt) {
    }
}
