package br.com.unopay.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Autowired
    private RedisTemplate redisTemplate;

    @Primary
    @Bean
    public RedisCacheManager cacheManager(ObjectMapper objectMapper) {
        RedisCacheManager cache = new RedisCacheManager(redisTemplate);
        cache.setDefaultExpiration(0);
        cache.setUsePrefix(true);
        return cache;
    }

}
