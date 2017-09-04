package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Partner;
import br.com.unopay.api.bacen.model.filter.PartnerFilter;
import br.com.unopay.api.bacen.repository.PartnerRepository;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.service.ProductService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.repository.UserDetailRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
public class PartnerService {
    private PartnerRepository repository;
    private PersonService personService;
    private UserDetailRepository userDetailRepository;
    private BankAccountService bankAccountService;
    private ProductService productService;

    @Autowired
    public PartnerService(PartnerRepository repository, PersonService personService,
                          UserDetailRepository userDetailRepository, BankAccountService bankAccountService, ProductService productService) {
        this.repository = repository;
        this.personService = personService;
        this.userDetailRepository = userDetailRepository;
        this.bankAccountService = bankAccountService;
        this.productService = productService;
    }

    public Partner create(Partner partner) {
        try {
            if(partner.getBankAccount() != null) {
                bankAccountService.create(partner.getBankAccount());
            }
            validateReferences(partner);
            personService.save(partner.getPerson());
            return repository.save(partner);
        } catch (DataIntegrityViolationException e){
            log.warn(String.format("Person partner already exists %s", partner.getPerson()), e);
            throw UnovationExceptions.conflict().withErrors(Errors.PERSON_PARTNER_ALREADY_EXISTS);

        }
    }

    public Partner getById(String id) {
        Optional<Partner> partner = repository.findById(id);
        return partner.orElseThrow(()->UnovationExceptions.notFound().withErrors(Errors.PARTNER_NOT_FOUND));
    }

    public void update(String id, Partner partner) {
        Partner current = getById(id);
        validateReferences(partner);
        current.updateModel(partner);
        personService.save(partner.getPerson());
        repository.save(current);
    }

    @Transactional
    private void validateReferences(Partner partner) {
        if(partner.hasProducts()){
            Set<Product> products = partner.getProducts().stream()
                    .map(product -> productService.findById(product.getId())).collect(Collectors.toSet());
            partner.setProducts(products);
        }
    }

    public void delete(String id) {
        getById(id);
        if(hasUser(id)){
            throw UnovationExceptions.conflict().withErrors(Errors.PARTNER_WITH_USERS);
        }
        repository.delete(id);
    }

    private Boolean hasUser(String id) {
        return userDetailRepository.countByPartnerId(id) > 0;
    }

    public Page<Partner> findByFilter(PartnerFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

}
