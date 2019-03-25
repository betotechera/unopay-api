package br.com.unopay.api.wingoo.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.wingoo.model.Password;
import br.com.unopay.api.wingoo.model.WingooUserMapping;
import br.com.wingoo.userclient.client.UserClient;
import br.com.wingoo.userclient.model.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Data
@Slf4j
@Service
public class WingooService {

    @Autowired
    @Qualifier("wingooUserClient")
    private UserClient wingooUserClient;

    public User create(Contractor contractor, String instrumentNumber, String issuerDocument){
        try {
            return wingooUserClient.create(WingooUserMapping.fromContractor(contractor, instrumentNumber, issuerDocument));
        }catch (HttpClientErrorException e){
            log.warn(e.getResponseBodyAsString());
            return null;
        }
    }

    public void update(Password password){

    }
}
