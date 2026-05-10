package org.artmotika.tradingengineservice.service;

import lombok.RequiredArgsConstructor;
import org.artmotika.common.dto.AssetDto;
import org.artmotika.common.dto.InvestorLimitDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatePublishService {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LIMITS_KEY = "investor:limits";
    private static final String ASSETS_KEY = "assets:state";

    public void updateLimit(InvestorLimitDto limit) {
        redisTemplate.opsForHash().put(LIMITS_KEY, limit.getUserId(), limit);
    }

    public void updateAsset(AssetDto asset) {
        redisTemplate.opsForHash().put(ASSETS_KEY, asset.getId(), asset);
    }
}
