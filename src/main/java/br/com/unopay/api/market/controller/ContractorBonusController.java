package br.com.unopay.api.market.controller;

import br.com.unopay.api.market.model.ContractorBonus;
import br.com.unopay.api.market.model.filter.ContractorBonusFilter;
import br.com.unopay.api.market.service.ContractorBonusService;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
}
