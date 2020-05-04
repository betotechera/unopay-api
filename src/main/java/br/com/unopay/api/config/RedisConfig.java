package br.com.unopay.api.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
class RedisConfig {
    @Autowired
    RedisTemplate redisTemplate;


    @Bean
    @Primary
    public RedisTemplate redisTemplate(ObjectMapper objectMapper) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(serializerObjectMapper(objectMapper)));
        return redisTemplate;
    }

    private ObjectMapper serializerObjectMapper(ObjectMapper objectMapper) {
        ObjectMapper serializerObjectMapper = objectMapper.copy();
        serializerObjectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return serializerObjectMapper;
    }


}
