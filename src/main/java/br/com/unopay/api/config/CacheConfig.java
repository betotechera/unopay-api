package br.com.unopay.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@Profile({"!test"})
public class CacheConfig {

    public final static String BANKS =  "banks";
    public final static String BRAND_FLAGS =  "brand-flags";
    public final static String CONTRACTOR_INSTRUMENTS =  "contractor-instruments";

    @Autowired
    private RedisTemplate redisTemplate;

    @Primary
    @Bean
    public RedisCacheManager cacheManager() {
        RedisCacheManager cache = new RedisCacheManager(redisTemplate);
        cache.setDefaultExpiration(0);
        cache.setUsePrefix(true);
        return cache;
    }

}
