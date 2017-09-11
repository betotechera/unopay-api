package br.com.unopay.api.order.controller;


import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.service.OrderService;
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

    private OrderService service;

    @Autowired
    public CreditOrderController(OrderService service) {
        this.service = service;
    }

    @JsonView(Views.Order.Detail.class)
    @ResponseStatus(CREATED)
    @RequestMapping(value = "/credit-orders", method = POST)
    public ResponseEntity<Order> create(@Validated(Create.class) @RequestBody Order order) {
        log.info("creating order {}", order);
        Order created = service.create(order);
        return
                created(URI.create("/credit-orders/"+created.getId()))
                        .body(created);

    }

    @ResponseStatus(OK)
    @JsonView(Views.Order.Detail.class)
    @RequestMapping(value = "/credit-orders/{id}", method = GET)
    public Order get(@PathVariable String id) {
        log.info("get creditOrder={}", id);
        return service.findById(id);
    }

}
