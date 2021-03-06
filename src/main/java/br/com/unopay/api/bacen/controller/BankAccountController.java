package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.BankAccount;
import br.com.unopay.api.bacen.service.BankAccountService;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Timed(prefix = "api")
public class BankAccountController {

    @Value("${unopay.api}")
    private String api;

    private BankAccountService service;

    @Autowired
    public BankAccountController(BankAccountService service) {
        this.service = service;
    }

    @JsonView(Views.BankAccount.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_BANK_ACCOUNT')")
    @RequestMapping(value = "/bankAccounts", method = RequestMethod.POST)
    public ResponseEntity<BankAccount> create(@Validated(Create.class) @RequestBody BankAccount bankAccount) {
        log.info("creating bank account {}", bankAccount);
        BankAccount created = service.create(bankAccount);
        return ResponseEntity
                .created(URI.create("/bankAccounts/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.BankAccount.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_BANK_ACCOUNT')")
    @RequestMapping(value = "/bankAccounts/{id}", method = RequestMethod.GET)
    public BankAccount get(@PathVariable  String id) {
        log.info("get bank account={}", id);
        return service.findById(id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_BANK_ACCOUNT')")
    @RequestMapping(value = "/bankAccounts/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody BankAccount bankAccount) {
        bankAccount.setId(id);
        log.info("updating bank account {}", bankAccount);
        service.update(id,bankAccount);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_BANK_ACCOUNT')")
    @RequestMapping(value = "/bankAccounts/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing bank account id={}", id);
        service.delete(id);
    }

}
