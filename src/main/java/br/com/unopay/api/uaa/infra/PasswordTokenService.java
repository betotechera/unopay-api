package br.com.unopay.api.uaa.infra;

import br.com.unopay.api.infra.CacheService;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.TOKEN_NOT_FOUND;

@Service
public class PasswordTokenService {

    @Autowired
    private CacheService cacheService;

    public static final String EVENT_RESET = "newUserPassword";

    public String createToken(UserDetail user){
        String token = RandomStringUtils.randomAlphanumeric(12);
        cacheService.put(EVENT_RESET, token, user.getId());
        return token;
    }

    public String getUserIdByToken(String token){
        String userId = cacheService.get(EVENT_RESET, token);
        if(userId== null) {
            throw UnovationExceptions.notFound().withErrors(TOKEN_NOT_FOUND);
        }
        return userId;
    }

    public void remove(String token){
        cacheService.evict(EVENT_RESET, token);
    }

}
