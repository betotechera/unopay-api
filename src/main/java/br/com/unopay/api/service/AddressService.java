package br.com.unopay.api.service;

import br.com.unopay.api.model.Address;
import br.com.unopay.api.viacep.model.CEP;
import br.com.unopay.api.viacep.service.ViaCEPService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AddressService {

    private ViaCEPService viaCEPService;

    @Autowired
    public AddressService(ViaCEPService viaCEPService) {
        this.viaCEPService = viaCEPService;
    }

    public Address search(String zipCode) {

        try {
            CEP cep = viaCEPService.search(zipCode);
            if(!cep.error())
              return new Address(cep);
        }catch (Exception e){
            log.warn("Error on getting address",e);
        }
        return new Address();
    }
}
