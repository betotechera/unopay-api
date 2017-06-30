package br.com.unopay.api.notification.config;

import br.com.unopay.api.notification.filter.CnpjFormat;
import br.com.unopay.api.notification.filter.DateStringFormat;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemplateConfig {
    @Bean
    public  Jinjava jinjava() {
        Jinjava jinjava = new Jinjava(jinjavaConfig());
        jinjava.getGlobalContext().registerFilter(new DateStringFormat());
        jinjava.getGlobalContext().registerFilter(new CnpjFormat());
        return jinjava;
    }

    private JinjavaConfig jinjavaConfig() {
        return new JinjavaConfig(StandardCharsets.UTF_8, new Locale("pt", "BR"), ZoneOffset.UTC, 10);
    }
}