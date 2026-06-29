package com.reptithcm.edu.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService { //nó sẽ chạy ngay sau authentication kiểm tra token thuộc blacklist không

    private final StringRedisTemplate redisTemplate;
    
    // In-memory fallback map to store blacklisted tokens when Redis is unavailable
    private final ConcurrentHashMap<String, Instant> localBlacklist = new ConcurrentHashMap<>();

    public void set(String key, String value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            System.err.println("Error setting key in Redis: " + e.getMessage() + ". Falling back to in-memory store.");
            long millis = unit.toMillis(timeout);
            Instant expiry = Instant.now().plusMillis(millis);
            localBlacklist.put(key, expiry);
        }
    }

    public String get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            System.err.println("Error getting key from Redis: " + e.getMessage() + ". Checking in-memory store.");
            Instant expiry = localBlacklist.get(key);
            if (expiry != null) {
                if (Instant.now().isBefore(expiry)) {
                    return "blacklisted";
                } else {
                    localBlacklist.remove(key);
                }
            }
            return null;
        }
    }

    public Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            System.err.println("Error checking key in Redis: " + e.getMessage() + ". Checking in-memory store.");
            
            // Clean up expired entries to prevent memory leak
            localBlacklist.entrySet().removeIf(entry -> Instant.now().isAfter(entry.getValue()));
            
            Instant expiry = localBlacklist.get(key);
            if (expiry != null) {
                return Instant.now().isBefore(expiry);
            }
            return false;
        }
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            System.err.println("Error deleting key from Redis: " + e.getMessage() + ". Deleting from in-memory store.");
            localBlacklist.remove(key);
        }
    }
}
