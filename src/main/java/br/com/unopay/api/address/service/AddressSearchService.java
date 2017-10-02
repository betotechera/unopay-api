package br.com.unopay.api.address.service;

import br.com.unopay.api.address.model.AddressSearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AddressSearchService {

    private RestTemplate restTemplate;

    private String addressSearchUrl;

    @Autowired
    public AddressSearchService(RestTemplate restTemplate, @Value("${addressSearch.url:}")String addressSearchUrl) {
        this.restTemplate = restTemplate;
        this.addressSearchUrl = addressSearchUrl;
    }

    public AddressSearch search(String cep){
        return restTemplate.getForEntity(addressSearchUrl + cep,AddressSearch.class).getBody();
    }
}
