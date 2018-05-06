package br.com.unopay.api.market.service;

import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.market.model.ContractorBonus;
import br.com.unopay.api.market.model.filter.ContractorBonusFilter;
import br.com.unopay.api.market.repository.ContractorBonusRepository;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.service.ProductService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

import static br.com.unopay.api.uaa.exception.Errors.CONTRACTOR_BONUS_NOT_FOUND;

@Service
public class ContractorBonusService {

    private ContractorBonusRepository contractorBonusRepository;
    private ProductService productService;
    private ContractorService contractorService;
    private PersonService personService;

    @Autowired
    public ContractorBonusService(ContractorBonusRepository contractorBonusRepository,
                                  ProductService productService,
                                  ContractorService contractorService,
                                  PersonService personService) {
        this.contractorBonusRepository = contractorBonusRepository;
        this.productService = productService;
        this.contractorService = contractorService;
        this.personService = personService;
    }

    public ContractorBonus save(ContractorBonus contractorBonus) {
        return contractorBonusRepository.save(contractorBonus);
    }

    public ContractorBonus create(ContractorBonus contractorBonus) {
        defineValidReferences(contractorBonus);
        contractorBonus.setupMyCreate();
        return save(contractorBonus);
    }

    public ContractorBonus update(String id, ContractorBonus contractorBonus){
        defineValidReferences(contractorBonus);
        ContractorBonus current = findById(id);
        current.updateMe(contractorBonus);
        return save(current);
    }

    public void delete(String id) {
        findById(id);
        contractorBonusRepository.delete(id);
    }

    private ContractorBonus getContractorBonus(String id, Supplier<Optional<ContractorBonus>> contractorBonus){
        Optional<ContractorBonus> bonus = contractorBonus.get();
        return bonus.orElseThrow(() ->
                UnovationExceptions.notFound().withErrors(CONTRACTOR_BONUS_NOT_FOUND.withOnlyArgument(id)));
    }

    public ContractorBonus findById(String id) {
        return getContractorBonus(id, () -> contractorBonusRepository.findById(id));
    }

    public Page<ContractorBonus> findByFilter(ContractorBonusFilter filter, UnovationPageRequest pageable){
        return contractorBonusRepository
                .findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private void defineValidProduct(ContractorBonus contractorBonus) {
        contractorBonus.setProduct(productService.findById(contractorBonus.productId()));
    }

    private void defineValidContractor(ContractorBonus contractorBonus) {
       contractorBonus.setContractor(contractorService.getById(contractorBonus.contractorId()));
    }

    private void defineValidPayer(ContractorBonus contractorBonus) {
        contractorBonus.setPayer(personService.findById(contractorBonus.payerId()));
    }

    private void defineValidReferences(ContractorBonus contractorBonus) {
        defineValidProduct(contractorBonus);
        defineValidContractor(contractorBonus);
        defineValidPayer(contractorBonus);
    }

}
