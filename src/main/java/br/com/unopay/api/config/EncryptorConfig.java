package br.com.unopay.api.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;

@Configuration
public class EncryptorConfig {


    @Value("${spring.encryptor.password}")
    private String password;

    @Bean
    public BytesEncryptor encryptor(){
        String salt = "ec22b31f42882266";
        return Encryptors.standard(password, salt);
    }
}
