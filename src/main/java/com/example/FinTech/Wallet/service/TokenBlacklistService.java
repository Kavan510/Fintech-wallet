package com.example.FinTech.Wallet.service;


import com.example.FinTech.Wallet.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    public void blacklistToken(String token) {

        Date expiry = jwtUtil.extractExpiration(token);
        long ttl = expiry.getTime() - System.currentTimeMillis();

        redisTemplate.opsForValue()
                .set(token, "blacklisted", ttl, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }
}