package br.com.unopay.api.fileuploader.config;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.transfer.TransferManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfig {

    @Value("${amazon.s3.accessKey}")
    private String accessKey;
    @Value("${amazon.s3.secretKey}")
    private String secretKey;

    @Bean
    public TransferManager transferManager() {
        return new TransferManager(new BasicAWSCredentials(accessKey, secretKey));
    }
}
