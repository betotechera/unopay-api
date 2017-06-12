package br.com.unopay.api.controller;

import br.com.unopay.api.model.CargoContract;
import br.com.unopay.api.model.FreightReceipt;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.service.FreightReceiptService;
import br.com.unopay.api.service.ServiceAuthorizeService;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
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

import java.net.URI;

@Slf4j
@RestController
@Timed(prefix = "api")
public class FreightReceiptController {

    @Value("${unopay.api}")
    private String api;

    private FreightReceiptService service;

    @Autowired
    public FreightReceiptController(FreightReceiptService service) {
        this.service = service;
    }

    @JsonView({Views.Public.class})
    @ResponseStatus(CREATED)
    @PreAuthorize("#oauth2.isUser() && hasRole('ROLE_MANAGE_SERVICE_AUTHORIZE')")
    @RequestMapping(value = "/freight-receipts", method = POST)
    public ResponseEntity<CargoContract> receipt(OAuth2Authentication authentication, @Validated(Create.class)
                                                             @RequestBody FreightReceipt freightReceipt) {
        log.info("user={}, authorizing receipt={}", authentication.getName(), freightReceipt);
         service.receipt(authentication.getName(),freightReceipt);
        log.info("authorized receipt={}", freightReceipt);
        return created(URI.create(
                String.format("/cargo-contracts/%s", freightReceipt.cargoContractId()))).body(freightReceipt.getCargoContract());

    }

}
