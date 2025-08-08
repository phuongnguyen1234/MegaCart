package com.megacart.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.megacart.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    @Qualifier("tokenBlacklistCache")
    private final Cache<String, Boolean> tokenBlacklistCache;

    @Override
    public void blacklistToken(String token) {
        tokenBlacklistCache.put(token, true);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistCache.getIfPresent(token) != null;
    }
}