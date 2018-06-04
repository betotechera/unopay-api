package br.com.unopay.api.market.controller;

import br.com.unopay.api.market.model.ContractorBonus;
import br.com.unopay.api.market.model.filter.ContractorBonusFilter;
import br.com.unopay.api.market.service.ContractorBonusService;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import java.net.URI;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Slf4j
@RestController
@Timed(prefix = "api")
public class ContractorBonusController {

    @Value("${unopay.api}")
    private String api;

    private ContractorBonusService contractorBonusService;

    @Autowired
    public ContractorBonusController(ContractorBonusService contractorBonusService) {
        this.contractorBonusService = contractorBonusService;
    }

    @JsonView(Views.ContractorBonus.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_CONTRACTOR_BONUS')")
    @RequestMapping(value = "/contractor-bonuses", method = RequestMethod.GET)
    public Results<ContractorBonus> getByParams(ContractorBonusFilter contractorBonusFilter,
                                                      @Validated UnovationPageRequest pageable) {
        log.info("search contractor bonus with filter={}", contractorBonusFilter);
        Page<ContractorBonus> page = contractorBonusService.findByFilter(contractorBonusFilter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/contractor-bonuses", api));
    }

    @JsonView(Views.ContractorBonus.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_CONTRACTOR_BONUS')")
    @RequestMapping(value = "/contractor-bonuses/{id}", method = RequestMethod.GET)
    public ContractorBonus get(@PathVariable String id) {
        log.info("get contractor bonus={}", id);
        return contractorBonusService.findById(id);
    }

    @JsonView(Views.ContractorBonus.Detail.class)
    @ResponseStatus(CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_CONTRACTOR_BONUS')")
    @RequestMapping(value = "/contractor-bonuses", method = POST)
    public ResponseEntity<ContractorBonus> create(@Validated(Create.class) @RequestBody ContractorBonus bonus){
        log.info("creating contractor bonus={}", bonus);
        ContractorBonus created = contractorBonusService.create(bonus);
        log.info("created contractor bonus={}", created);
        return created(URI.create(String
                .format("/contractor-bonuses/%s",created.getId()))).body(created);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_CONTRACTOR_BONUS')")
    @RequestMapping(value = "/contractor-bonuses/{id}", method = PUT)
    public void update(@PathVariable String id, @Validated(Update.class) @RequestBody ContractorBonus contractorBonus) {
        contractorBonus.setId(id);
        log.info("updating bonus={}", contractorBonus);
        contractorBonusService.update(id, contractorBonus);
    }

}
