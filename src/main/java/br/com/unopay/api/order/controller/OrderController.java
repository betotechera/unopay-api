package br.com.unopay.api.order.controller;


import br.com.unopay.api.billing.creditcard.model.PaymentRequest;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.model.OrderSummary;
import br.com.unopay.api.order.model.OrderType;
import br.com.unopay.api.order.model.filter.OrderFilter;
import br.com.unopay.api.order.service.OrderService;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import java.net.URI;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Slf4j
@RestController
@Timed(prefix = "api")
public class OrderController {

    @Value("${unopay.api}")
    private String api;

    private OrderService service;

    @Autowired
    public OrderController(OrderService service) {
        this.service = service;
    }

    @JsonView(Views.Order.Detail.class)
    @ResponseStatus(CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_ORDERS')")
    @RequestMapping(value = "/orders", method = POST)
    public ResponseEntity<Order> create(@Validated(Create.Order.Adhesion.class) @RequestBody Order order) {
        log.info("creating order {}", order);
        Order created = service.create(order);
        return created(URI.create("/credit-orders/"+created.getId())).body(created);
    }

    @JsonView(Views.Order.Detail.class)
    @ResponseStatus(CREATED)
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/orders", method = POST, params = "type=ADHESION")
    public ResponseEntity<Order> createAdhesion(@Validated(Create.Order.Adhesion.class) @RequestBody Order order) {
        log.info("creating adhesion order {}", order);
        order.setType(OrderType.ADHESION);
        Order created = service.create(order);
        return created(URI.create("/credit-orders/"+created.getId())).body(created);
    }

    @ResponseStatus(OK)
    @JsonView(Views.Order.Detail.class)
    @PreAuthorize("hasRole('ROLE_LIST_ORDERS')")
    @RequestMapping(value = "/orders/{id}", method = GET)
    public Order get(@PathVariable String id) {
        log.info("get order={}", id);
        return service.findById(id);
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_ORDERS')")
    @RequestMapping(value = "/orders/{id}", method = PUT)
    public void update(@PathVariable String id, @RequestBody Order order) {
        log.info("update order={}", id);
        service.update(id, order);
    }

    @JsonView(Views.Order.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_ORDERS')")
    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public Results<Order> getByParams(OrderFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search order with filter={}", filter);
        Page<Order> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/orders", api));
    }

    @JsonView(Views.Order.Detail.class)
    @ResponseStatus(ACCEPTED)
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/orders/{id}", method = PUT, params = "request-payment")
    public Order create(@PathVariable  String id,
                                        @Validated(Create.Order.class) @RequestBody PaymentRequest paymentRequest) {
        log.info("new payment for order={}", id);
        return service.requestPayment(id, paymentRequest);
    }

    @ResponseStatus(OK)
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/orders/summaries", method = GET, params = "document")
    public Set<OrderSummary> summarize(@RequestParam String document) {
        log.info("new payment for order={}", document);
        return service.findSummaryByPersonDocument(document);
    }

}
