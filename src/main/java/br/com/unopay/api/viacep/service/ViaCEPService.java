package br.com.unopay.api.viacep.service;

import br.com.unopay.api.viacep.model.CEP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ViaCEPService {

    private RestTemplate restTemplate;

    private String viaCepUrl;

    @Autowired
    public ViaCEPService( RestTemplate restTemplate,@Value("${via-cep.url:}")String viaCepUrl) {
        this.restTemplate = restTemplate;
        this.viaCepUrl = viaCepUrl;
    }

    public CEP search(String cep){
        return restTemplate.getForEntity(viaCepUrl + cep+ "/json",CEP.class).getBody();
    }
}
