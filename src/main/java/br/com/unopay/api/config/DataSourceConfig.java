package br.com.unopay.api.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Profile({"qa", "prod"})
    @Primary
    @Bean(name = "datasource")
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource datasource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Profile({"dev", "test"})
    @Bean(name = "spring.datasource", destroyMethod = "shutdown")
    public DataSource dataSourceDevTest() {
        EmbeddedDatabase embeddedDatabase = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .continueOnError(true)
                .setName("uaa")
                .build();
        return embeddedDatabase;
    }

}
