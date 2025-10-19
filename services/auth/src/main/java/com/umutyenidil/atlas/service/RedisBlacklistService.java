package com.umutyenidil.atlas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisBlacklistService {

    private final StringRedisTemplate redis;

    public void add(String token, Instant expiresAt) {
        log.info("test 1");
        if (token == null || expiresAt == null) return;
        Duration ttl = Duration.between(Instant.now(), expiresAt);
        log.info("test 2");
        if (ttl.isNegative() || ttl.isZero()) return;
        redis.opsForValue().set(token, "revoked", ttl);
        log.info("test 3");
    }

    public boolean contains(String token) {
        return token != null && Boolean.TRUE.equals(redis.hasKey(token));
    }
}