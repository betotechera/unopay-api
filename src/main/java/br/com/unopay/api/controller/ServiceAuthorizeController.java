package br.com.unopay.api.controller;

import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.service.ServiceAuthorizeService;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import static org.springframework.http.HttpStatus.CREATED;
import org.springframework.http.ResponseEntity;
import static org.springframework.http.ResponseEntity.created;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Timed(prefix = "api")
public class ServiceAuthorizeController {

    @Value("${unopay.api}")
    private String api;

    private ServiceAuthorizeService service;

    @Autowired
    public ServiceAuthorizeController(ServiceAuthorizeService service) {
        this.service = service;
    }

    @ResponseStatus(CREATED)
    @PreAuthorize("#oauth2.isUser() && hasRole('ROLE_MANAGE_SERVICE_AUTHORIZE')")
    @RequestMapping(value = "/service-authorizations", method = POST)
    public ResponseEntity<ServiceAuthorize> create(OAuth2Authentication authentication, @Validated(Create.class)
                                                             @RequestBody ServiceAuthorize serviceAuthorize) {
        log.info("user={}, authorizing service={}", authentication.getName(), serviceAuthorize);
        ServiceAuthorize created = service.create(authentication.getName(),serviceAuthorize);
        log.info("authorized service={}", created);
        return created(URI.create(
                String.format("/service-authorizations/%s", created.getId()))).body(created);

    }

}
