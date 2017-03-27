package br.com.unopay.api.uaa.infra;

import br.com.unopay.api.infra.CacheService;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasswordTokenService {

    @Autowired
    private CacheService cacheService;

    public static final String PASSWORD_RESET = "newUserPassword";

    public String createToken(UserDetail user){
        String token = RandomStringUtils.randomAlphanumeric(8);
        cacheService.put(PASSWORD_RESET, token, user.getId());
        return  token;
    }

    public String getUserIdByToken(String token){
        String userId = cacheService.get(PASSWORD_RESET, token);
        if(userId== null) {
            throw UnovationExceptions.notFound();
        }
        return userId;
    }

    public void remove(String token){
        cacheService.evict(PASSWORD_RESET, token);
    }

}
