package com.saarisht.eventhub.bookingservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;

@Service
public class SeatLockService {

    private static final String LOCK_KEY_PREFIX = "lock:eventSeat:";

    /**
     * Compare-and-delete, run atomically inside Redis. A plain GET-then-DEL from
     * the app is racy: the TTL could expire and another user could acquire the
     * lock between our GET and our DEL, and we would delete their lock instead
     * of our own expired one.
     */
    private static final DefaultRedisScript<Long> RELEASE_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('del', KEYS[1]) " +
                    "else return 0 end",
            Long.class
    );

    private final StringRedisTemplate redisTemplate;
    private final Duration lockTtl;

    public SeatLockService(StringRedisTemplate redisTemplate,
                            @Value("${booking.seat-lock.ttl-seconds:300}") long ttlSeconds) {
        this.redisTemplate = redisTemplate;
        this.lockTtl = Duration.ofSeconds(ttlSeconds);
    }

    /**
     * Atomic SET NX EX: fails immediately if someone else already holds the
     * lock, no read-then-write race. The value stored is the requesting
     * user's id, so releaseLock can later verify the caller actually owns
     * the lock before deleting it.
     */
    public boolean tryAcquireLock(Long eventSeatId, Long userId) {
        String key = lockKey(eventSeatId);
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, userId.toString(), lockTtl);
        return Boolean.TRUE.equals(acquired);
    }

    public boolean releaseLock(Long eventSeatId, Long userId) {
        String key = lockKey(eventSeatId);
        Long result = redisTemplate.execute(RELEASE_SCRIPT, Collections.singletonList(key), userId.toString());
        return result != null && result == 1L;
    }

    private String lockKey(Long eventSeatId) {
        return LOCK_KEY_PREFIX + eventSeatId;
    }
}
