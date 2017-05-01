package br.com.unopay.api.service;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.repository.EstablishmentRepository;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.filter.ContractFilter;
import br.com.unopay.api.repository.ContractRepository;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Set;

import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_ALREADY_EXISTS;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_NOT_FOUND;

@Slf4j
@Service
public class ContractService {

    private ContractRepository repository;
    private HirerService hirerService;
    private ContractorService contractorService;
    private ProductService productService;
    private EstablishmentRepository establishmentRepository;

    @Autowired
    public ContractService(ContractRepository repository, HirerService hirerService, ContractorService contractorService, ProductService productService, EstablishmentRepository establishmentRepository) {
        this.repository = repository;
        this.hirerService = hirerService;
        this.contractorService = contractorService;
        this.productService = productService;
        this.establishmentRepository = establishmentRepository;
    }

    public Contract save(Contract contract) {
        try {
            validateReferences(contract);
            contract.validate();
            contract.checkDocumentNumberInvoice();
            return repository.save(contract);
        }catch (DataIntegrityViolationException e){
            log.info("Contract with code={} already exists",  contract.getCode());
            throw UnovationExceptions.conflict().withErrors(CONTRACT_ALREADY_EXISTS);
        }
    }

    public void update(String id, Contract contract) {
        Contract current = findById(id);
        validateReferences(contract);
        current.updateMe(contract);
        current.validate();
        contract.checkDocumentNumberInvoice();

        try {
            repository.save(current);
        }catch (DataIntegrityViolationException e){
            log.info("Product code={} already exists", contract.getCode());
            throw UnovationExceptions.conflict().withErrors(CONTRACT_ALREADY_EXISTS);
        }
    }

    public Contract findById(String id) {
        Contract contract = repository.findOne(id);
        if(contract == null){
            throw UnovationExceptions.notFound().withErrors(CONTRACT_NOT_FOUND);
        }
        return contract;
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

    public Page<Contract> findByFilter(ContractFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private void validateReferences(Contract contract) {
        if(contract.getContractor().getId() != null) {
            contract.setContractor(contractorService.getById(contract.getContractor().getId()));
        }
        if(contract.getHirer().getId() != null) {
            contract.setHirer(hirerService.getById(contract.getHirer().getId()));
        }
        if(contract.getProduct().getId() != null) {
            contract.setProduct(productService.findById(contract.getProduct().getId()));
        }
    }

    public int addEstablishments(String id, Set<String> establishmentsDocumentNumber) {
        Set<Establishment> establishments = establishmentRepository.findByPersonDocumentNumberIn(establishmentsDocumentNumber);
        if(establishments == null || establishments.size() < 0)
            throw UnovationExceptions.conflict().withErrors(Errors.ESTABLISHMENTS_NOT_FOUND);
        Contract contract = findById(id);
        contract.setEstablishments(establishments);
        repository.save(contract);
        return establishments.size();
    }
}