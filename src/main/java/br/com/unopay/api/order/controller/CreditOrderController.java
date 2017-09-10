package br.com.unopay.api.order.controller;


import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.order.model.CreditOrder;
import br.com.unopay.api.order.service.CreditOrderService;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
@Timed(prefix = "api")
public class CreditOrderController {

    @Value("${unopay.api}")
    private String api;

    private CreditOrderService service;

    @Autowired
    public CreditOrderController(CreditOrderService service) {
        this.service = service;
    }

    @JsonView(Views.Order.Detail.class)
    @ResponseStatus(CREATED)
    @RequestMapping(value = "/credit-orders", method = POST)
    public ResponseEntity<CreditOrder> create(@Validated(Create.class) @RequestBody CreditOrder creditOrder) {
        log.info("creating creditOrder {}", creditOrder);
        CreditOrder created = service.create(creditOrder);
        return
                created(URI.create("/credit-orders/"+created.getId()))
                        .body(created);

    }

    @ResponseStatus(OK)
    @JsonView(Views.Order.Detail.class)
    @RequestMapping(value = "/credit-orders/{id}", method = GET)
    public CreditOrder get(@PathVariable String id) {
        log.info("get creditOrder={}", id);
        return service.findById(id);
    }

}
