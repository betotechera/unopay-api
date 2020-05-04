package br.com.unopay.api.config;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Configuration
public class DataSourceConfig {

    /*@Profile({"qa", "dev", "prod"})
    @Primary
    @Bean(name = "datasource")
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource datasource() {
        return DataSourceBuilder.create().build();
    }*/

    @Bean
    @Profile({"qa", "dev", "prod"})
    @Primary
    @Bean(name = "datasource")
    public BasicDataSource dataSource() throws URISyntaxException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(dbUrl);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);

        return basicDataSource;
    }

    @Primary
    @Profile({"test"})
    @Bean(name = "spring.datasource", destroyMethod = "shutdown")
    public DataSource dataSourceDevTest() {
        EmbeddedDatabase embeddedDatabase = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .continueOnError(true)
                .setName("unopay")
                .build();
        return embeddedDatabase;
    }


}
