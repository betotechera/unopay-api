package br.com.unopay.api.config;

import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.Institution;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.Partner;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.ForbiddenException;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserTypeArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private UserDetailService userDetailService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Arrays.asList(Institution.class, Issuer.class,
                Establishment.class, AccreditedNetwork.class,
                Partner.class, Contractor.class, Hirer.class).contains(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                           ModelAndViewContainer modelAndViewContainer,
                           NativeWebRequest nativeWebRequest,
                           WebDataBinderFactory webDataBinderFactory) throws Exception {


        OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isClientOnly()) {
            throw new ForbiddenException("only authenticated user can access this resource");
        }

        UserDetail currentUser = userDetailService.getByEmail(authentication.getName());
        return currentUser.my(methodParameter.getParameterType());
    }
}
