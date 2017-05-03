package br.com.unopay.api.uaa.infra;

import br.com.unopay.api.infra.CacheService;
import static br.com.unopay.api.uaa.exception.Errors.TOKEN_NOT_FOUND;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasswordTokenService {

    private CacheService cacheService;

    private static final String EVENT_RESET = "newUserPassword";

    @Autowired
    public PasswordTokenService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

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
