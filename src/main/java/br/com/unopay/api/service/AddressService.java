package br.com.unopay.api.service;

import br.com.unopay.api.address.model.AddressSearch;
import br.com.unopay.api.address.service.AddressSearchService;
import br.com.unopay.api.model.Address;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
public class AddressService {

    private AddressSearchService addressSearchService;

    @Autowired
    public AddressService(AddressSearchService addressSearchService) {
        this.addressSearchService = addressSearchService;
    }

    public Address search(String zipCode) {
        try {
            AddressSearch addressSearch = addressSearchService.search(zipCode);
            return  new Address(addressSearch);
        }catch (HttpClientErrorException e){
            log.warn("Getting address failed {}",e.getMessage());
            return new Address(zipCode);
        }
    }
}
