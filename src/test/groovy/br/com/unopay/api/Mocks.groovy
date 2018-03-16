package br.com.unopay.api

import br.com.unopay.api.billing.creditcard.model.CreditCard
import br.com.unopay.api.billing.creditcard.model.Gateway
import br.com.unopay.api.uaa.model.UserDetail
import org.mockito.Mockito
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate
import org.springframework.cache.Cache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
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

    @Bean
    @Primary
    Gateway mockGateway(){
        Gateway mock = Mockito.mock(Gateway)
        CreditCard creditCard = Mockito.mock(CreditCard)
        Mockito.when(mock.storeCard(Mockito.any(UserDetail.class), Mockito.any(CreditCard.class)))
                .thenReturn(creditCard)
        mock
    }
}
