package br.com.unopay.api.fileuploader.config;

import br.com.unopay.api.fileuploader.model.UploadConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties("unopay.file-uploader")
public class FileUploadConfig {

    private Map<String, UploadConfiguration> configs = new HashMap<>();

    @Bean
    public Map<String, UploadConfiguration> configs(){
        return configs;
    }

    public Map<String, UploadConfiguration> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, UploadConfiguration> configs) {
        this.configs = configs;
    }
}
