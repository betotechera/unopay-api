package br.com.unopay.api.service;

import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractEstablishment;
import br.com.unopay.api.model.ContractSituation;
import br.com.unopay.api.model.filter.ContractFilter;
import br.com.unopay.api.repository.ContractEstablishmentRepository;
import br.com.unopay.api.repository.ContractRepository;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.CONTRACTOR_CONTRACT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_ALREADY_EXISTS;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_ESTABLISHMENT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_NOT_FOUND;

@Slf4j
@Service
public class ContractService {

    private ContractRepository repository;
    private HirerService hirerService;
    private ContractorService contractorService;
    private ProductService productService;
    private ContractEstablishmentRepository contractEstablishmentRepository;
    @Setter private ContractInstallmentService installmentService;

    @Autowired
    public ContractService(ContractRepository repository, HirerService hirerService,
                           ContractorService contractorService, ProductService productService,
                           ContractEstablishmentRepository contractEstablishmentRepository,
                           ContractInstallmentService installmentService) {
        this.repository = repository;
        this.hirerService = hirerService;
        this.contractorService = contractorService;
        this.productService = productService;
        this.contractEstablishmentRepository = contractEstablishmentRepository;
        this.installmentService = installmentService;
    }

    public Contract save(Contract contract) {
        try {
            validateReferences(contract);
            contract.validate();
            contract.checkFields();
            Contract created = repository.save(contract);
            installmentService.create(created);
            return created;
        }catch (DataIntegrityViolationException e){
            log.info("Contract with code={} already exists",  contract.getCode(), e);
            throw UnovationExceptions.conflict().withErrors(CONTRACT_ALREADY_EXISTS);
        }
    }

    public void update(String id, Contract contract) {
        Contract current = findById(id);
        validateReferences(contract);
        current.updateMe(contract);
        current.validate();
        contract.checkFields();

        try {
            repository.save(current);
        }catch (DataIntegrityViolationException e){
            log.info("Product code={} already exists", contract.getCode(), e);
            throw UnovationExceptions.conflict().withErrors(CONTRACT_ALREADY_EXISTS);
        }
    }

    public Contract findById(String id) {
        Optional<Contract> contract = repository.findById(id);
        return contract.orElseThrow(()->UnovationExceptions.notFound().withErrors(CONTRACT_NOT_FOUND));
    }

    public Contract getByIdAndContractorId(String contractId, String contractorId) {
        List<Contract> contracts = repository.findByContractorId(contractorId);
        Optional<Contract> contract = contracts.stream()
                                    .filter(c -> Objects.equals(c.getId(), contractId)).findFirst();
        return contract.orElseThrow(()->  UnovationExceptions.notFound().withErrors(CONTRACTOR_CONTRACT_NOT_FOUND));
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

    public ContractEstablishment addEstablishments(String id, ContractEstablishment contractEstablishment) {
        Contract contract = findById(id);
        if(contract.containsEstablishment(contractEstablishment)){
            throw UnovationExceptions.conflict().withErrors(Errors.ESTABLISHMENT_ALREADY_IN_CONTRACT);
        }
        contractEstablishment.setMeUpBy(contract);
        contractEstablishment = contractEstablishmentRepository.save(contractEstablishment);
        contract.addContractEstablishment(contractEstablishment);
        repository.save(contract);
        return contractEstablishment;
    }

    public void removeEstablishment(String id, String contractEstablishmentId) {
        Contract contract = findById(id);
        contract.removeContractEstablishmentBy(contractEstablishmentId);
        repository.save(contract);
        findContractEstablishmentById(contractEstablishmentId);
        contractEstablishmentRepository.delete(contractEstablishmentId);
    }

    private ContractEstablishment findContractEstablishmentById(String id) {
        ContractEstablishment contract = contractEstablishmentRepository.findOne(id);
        if(contract == null){
            throw UnovationExceptions.notFound().withErrors(CONTRACT_ESTABLISHMENT_NOT_FOUND);
        }
        return contract;
    }

    public List<Contract> findByEstablishmentId(String establishmentId) {
        List<Contract> contracts = repository.findByEstablishmentsId(establishmentId);
        if(contracts.isEmpty()){
            throw UnovationExceptions.notFound().withErrors(CONTRACT_ESTABLISHMENT_NOT_FOUND);
        }
        return contracts;
    }
    public List<Contract> getContractorValidContracts(String contractorId, String establishmentId,
                                                      Set<ServiceType> serviceType) {
        contractorService.getById(contractorId);
        Page<Contract> contractPage = getActiveContracts(contractorId,serviceType);
        List<Contract> contracts = contractPage.getContent();
        if(establishmentId !=null) {
            return contracts.stream()
                    .filter(contract -> contract.validToEstablishment(establishmentId))
                    .collect(Collectors.toList());
        }
        return contracts;
    }

    private Page<Contract> getActiveContracts(String contractorId, Set<ServiceType> serviceType) {
        ContractFilter contractFilter = createContractActiveFilter(contractorId,serviceType);
        return findByFilter(contractFilter, new UnovationPageRequest());
    }

    private ContractFilter createContractActiveFilter(String contractorId, Set<ServiceType> serviceType) {
        ContractFilter contractFilter = new ContractFilter();
        contractFilter.setSituation(ContractSituation.ACTIVE);
        contractFilter.setContractor(contractorId);
        contractFilter.setServiceType(serviceType);
        return contractFilter;
    }

}
