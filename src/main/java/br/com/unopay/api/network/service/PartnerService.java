package br.com.unopay.api.network.service;

import br.com.unopay.api.bacen.service.BankAccountService;
import br.com.unopay.api.network.model.Partner;
import br.com.unopay.api.network.model.filter.PartnerFilter;
import br.com.unopay.api.network.repository.PartnerRepository;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.service.ProductService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PartnerService {
    private PartnerRepository repository;
    private PersonService personService;
    private UserDetailService userDetailService;
    private BankAccountService bankAccountService;
    private ProductService productService;

    @Autowired
    public PartnerService(PartnerRepository repository, PersonService personService,
                          UserDetailService userDetailService,
                          BankAccountService bankAccountService, ProductService productService) {
        this.repository = repository;
        this.personService = personService;
        this.userDetailService = userDetailService;
        this.bankAccountService = bankAccountService;
        this.productService = productService;
    }

    public Partner create(Partner partner) {
        try {
            if(partner.getBankAccount() != null) {
                bankAccountService.create(partner.getBankAccount());
            }
            validateReferences(partner);
            personService.create(partner.getPerson());
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
        personService.create(partner.getPerson());
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
        if(userDetailService.hasPartner(id)){
            throw UnovationExceptions.conflict().withErrors(Errors.PARTNER_WITH_USERS);
        }
        repository.delete(id);
    }

    public Page<Partner> findByFilter(PartnerFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

}
