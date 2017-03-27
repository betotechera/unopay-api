package br.com.unopay.api

import org.mockito.Mockito
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate
import org.springframework.cache.Cache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory

@Configuration
class Mocks {


    @Bean
    RabbitMessagingTemplate mockRabbitMessagingTemplate() {
        Mockito.mock(RabbitMessagingTemplate)
    }

    @Bean
    RedisConnectionFactory mockRedisTemplate(){
        Mockito.mock(RedisConnectionFactory)
    }

    @Bean
    RedisCacheManager mockCacheService(){
        RedisCacheManager mock = Mockito.mock(RedisCacheManager)
        Cache mockCache = Mockito.mock(Cache)
        Mockito.when(mock.getCache(Mockito.anyString())).thenReturn(mockCache)
        mock
    }
}
