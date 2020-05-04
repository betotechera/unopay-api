package br.com.unopay.api.config;

import java.net.URI;
import java.net.URISyntaxException;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
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

    @Profile({"qa", "dev", "prod"})
    @Primary
    @Bean(name = "datasource")
    public DataSource dataSource() throws URISyntaxException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";
        return DataSourceBuilder.create().username(username).password(password).url(dbUrl).build();
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
