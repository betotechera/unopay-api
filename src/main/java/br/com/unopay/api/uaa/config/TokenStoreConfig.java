package br.com.unopay.api.uaa.config;


import br.com.unopay.api.uaa.oauth2.UnopayRedisTokenStore;
import br.com.unopay.api.uaa.oauth2.UnovationTokenStore;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;


@Configuration
public class TokenStoreConfig {


    @Bean
    @Profile({"qa", "hm", "prod"})
    public TokenStore redisTokenStore(RedisConnectionFactory connectionFactory, DataSource dataSource) {
        UnopayRedisTokenStore redisTokenStore = new UnopayRedisTokenStore(connectionFactory);
        redisTokenStore.setPrefix("api:");
        JdbcTokenStore jdbcTokenStore = new JdbcTokenStore(dataSource);
        return new UnovationTokenStore(redisTokenStore, jdbcTokenStore);
    }

    @Bean
    @Profile({"test","dev"})
    public TokenStore jdbcTokenStore(DataSource dataSource) {
        return new JdbcTokenStore(dataSource);
    }

}
