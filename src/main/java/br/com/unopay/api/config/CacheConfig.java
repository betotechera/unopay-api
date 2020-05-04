package br.com.unopay.api.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@Profile({"!test"})
public class CacheConfig {

    public final static String BANKS =  "banks";
    public final static String CONTRACTOR_ORDERS =  "contractor-orders";
    public final static String SERVICE_AUTHORIZES =  "service-authorizes";
    public final static String CONTRACTS =  "contracts";
    public final static String INSTRUMENTS =  "instruments";
    public static final long DEFAULT_EXPIRATION_SECONDS = TimeUnit.MINUTES.toSeconds(30);
    public static final Map<String, Long> EXPIRATION_MAP = new HashMap<>();


    public RedisTemplate redisTemplate() throws URISyntaxException {
        JedisConnectionFactory jedisConnectionFactory = getJedisConnectionFactory();
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        return redisTemplate;
    }

    private JedisConnectionFactory getJedisConnectionFactory() throws URISyntaxException {
        JedisPoolConfig poolConfig = getJedisPoolConfig();
        URI redisURI = new URI(System.getenv("REDIS_URL"));
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(poolConfig);
        jedisConnectionFactory.setHostName(redisURI.getHost());
        jedisConnectionFactory.setPort(redisURI.getPort());
        jedisConnectionFactory.setPassword(redisURI.getUserInfo().split(":")[1]);
        return jedisConnectionFactory;
    }

    private JedisPoolConfig getJedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        return poolConfig;
    }

    @Bean
    @Primary
    public RedisCacheManager cacheManager(ObjectMapper objectMapper) throws URISyntaxException {
        RedisTemplate redisTemplate = redisTemplate();
        RedisCacheManager cache = new RedisCacheManager(redisTemplate);
        cache.setDefaultExpiration(0);
        cache.setUsePrefix(true);
        ObjectMapper serializerObjectMapper = serializerObjectMapper(objectMapper);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(serializerObjectMapper));

        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        cacheManager.setDefaultExpiration(DEFAULT_EXPIRATION_SECONDS);
        cacheManager.setExpires(EXPIRATION_MAP);
        cacheManager.setUsePrefix(true);
        return cacheManager;
    }

    static {
        putKey(BANKS);
        putKey(INSTRUMENTS);
        putKey(CONTRACTOR_ORDERS);
        putKey(SERVICE_AUTHORIZES);
        putKey(CONTRACTS);
    }

    static void putKey(String key) {
        EXPIRATION_MAP.put(key, DEFAULT_EXPIRATION_SECONDS);
    }

    private ObjectMapper serializerObjectMapper(ObjectMapper objectMapper) {
        ObjectMapper serializerObjectMapper = objectMapper.copy();
        serializerObjectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return serializerObjectMapper;
    }

}
