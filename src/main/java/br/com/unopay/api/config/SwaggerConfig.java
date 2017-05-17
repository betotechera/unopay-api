package br.com.unopay.api.config;

import com.google.common.base.Predicate;
import static com.google.common.base.Predicates.or;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import static springfox.documentation.builders.PathSelectors.regex;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(OAuth2Authentication.class, HttpServletRequest.class)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(paths())
                .build();
    }

    private Predicate<String> paths() {
        return or(
                regex("/users.*"),
                regex("/groups.*"),
                regex("/contracts.*"),
                regex("/payment-instruments.*"),
                regex("/hirers.*"),
                regex("/hirers.*"),
                regex("/products.*"),
                regex("/accredited-networks.*"),
                regex("/banks.*"),
                regex("/brand-flags.*"),
                regex("/contractors.*"),
                regex("/establishments.*"),
                regex("/events.*"),
                regex("/hirer-branches.*"),
                regex("/institutions.*"),
                regex("/issuers.*"),
                regex("/partners.*"),
                regex("/payment-rule-groups.*"),
                regex("/services.*"),
                regex("/user-types.*"));
    }
}