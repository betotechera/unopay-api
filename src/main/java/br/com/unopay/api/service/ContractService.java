package br.com.unopay.api.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.infra.Notifier;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractEstablishment;
import br.com.unopay.api.model.ContractInstallment;
import br.com.unopay.api.model.ContractSituation;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.filter.ContractFilter;
import br.com.unopay.api.network.model.AccreditedNetwork;
import br.com.unopay.api.network.model.ServiceType;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.repository.ContractEstablishmentRepository;
import br.com.unopay.api.repository.ContractRepository;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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

import static br.com.unopay.api.uaa.exception.Errors.CONTRACTOR_CONTRACT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_ALREADY_EXISTS;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_ESTABLISHMENT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_HIRER_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_NOT_FOUND;

@Timed
@Slf4j
@Service
public class ContractService {

    private ContractRepository repository;
    private HirerService hirerService;
    private ContractorService contractorService;
    private ProductService productService;
    private ContractEstablishmentRepository contractEstablishmentRepository;
    private UserDetailService userDetailService;
    private ContractInstallmentService installmentService;
    private PaymentInstrumentService paymentInstrumentService;
    private Notifier notifier;


    @Autowired
    public ContractService(ContractRepository repository, HirerService hirerService,
                           ContractorService contractorService, ProductService productService,
                           ContractEstablishmentRepository contractEstablishmentRepository,
                           UserDetailService userDetailService,
                           ContractInstallmentService installmentService,
                           PaymentInstrumentService paymentInstrumentService, Notifier notifier) {
        this.repository = repository;
        this.hirerService = hirerService;
        this.contractorService = contractorService;
        this.productService = productService;
        this.contractEstablishmentRepository = contractEstablishmentRepository;
        this.userDetailService = userDetailService;
        this.installmentService = installmentService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.notifier = notifier;
    }

    public void createInstallmentOrders(){
        Set<ContractInstallment> installments = installmentService.findAllNotPaidInstallments();
        installments.stream().map(ContractInstallment::toOrder)
                .forEach(order -> notifier.notify(Queues.ORDER_CREATE, order));
    }

    public Contract create(Contract contract) {
        return create(contract, false);
    }

    public Contract create(Contract contract, Boolean forHirer) {
        try {
            checkContract(contract.getContractor().getDocumentNumber(), contract.getProduct().getCode());
            validateReferences(contract);
            contract.validate();
            contract.checkFields();
            contract.setupMeUp();
            Contract created = save(contract);
            createInstallment(forHirer, created);
            return created;
        }catch (DataIntegrityViolationException e){
            log.info("Contract with code={} already exists",  contract.getCode(), e);
            throw UnovationExceptions.conflict()
                    .withErrors(CONTRACT_ALREADY_EXISTS.withOnlyArgument(contract.getCode()));
        }
    }

    public Contract save(Contract contract) {
        return repository.save(contract);
    }

    private void createInstallment(Boolean forHirer, Contract created) {
        if(forHirer){
            installmentService.createForHirer(created);
            return;
        }
        installmentService.create(created);
    }

    public void checkContract(String contractorDocument, String productCode) {
        Optional<Contract> contractOptional = findByContractorAndProduct(contractorDocument, productCode);
        contractOptional.ifPresent(it -> {
            throw UnovationExceptions.conflict()
                    .withErrors(CONTRACT_ALREADY_EXISTS.withOnlyArgument(it.getCode()));
        });
    }

    public void update(String id, Contract contract) {
        Contract current = findById(id);
        update(current, contract);
    }

    private void update(Contract current, Contract contract) {
        validateReferences(contract);
        current.updateMe(contract);
        current.validate();
        contract.checkFields();
        try {
            save(current);
        }catch (DataIntegrityViolationException e){
            log.info("Product code={} already exists", contract.getCode(), e);
            throw UnovationExceptions.conflict().withErrors(CONTRACT_ALREADY_EXISTS);
        }
    }

    public Contract findById(String id) {
        Optional<Contract> contract = repository.findById(id);
        return contract.orElseThrow(()->UnovationExceptions.notFound().withErrors(CONTRACT_NOT_FOUND));
    }

    public Contract findByCode(Long code) {
        Optional<Contract> contract = repository.findByCode(code);
        return contract.orElseThrow(()->UnovationExceptions.notFound().withErrors(CONTRACT_NOT_FOUND));
    }

    public Contract findByIdForHirer(String id, Hirer hirer) {
        Optional<Contract> contract = repository.findByIdAndHirerId(id, hirer.getId());
        return contract.orElseThrow(()->UnovationExceptions.notFound().withErrors(CONTRACT_NOT_FOUND));
    }

    public Contract getByIdAndContractorId(String contractId, Contractor contractor) {
        List<Contract> contracts = repository.findByContractorId(contractor.getId());
        Optional<Contract> contract = contracts.stream()
                                    .filter(c -> Objects.equals(c.getId(), contractId)).findFirst();
        return contract.orElseThrow(()->  UnovationExceptions.notFound().withErrors(CONTRACTOR_CONTRACT_NOT_FOUND));
    }

    @Transactional
    public void cancel(String id) {
        Contract current = findById(id);
        cancel(current);
    }

    private void cancel(Contract current) {
        current.setSituation(ContractSituation.CANCELLED);
        paymentInstrumentService.cancel(current.getContractor().getDocumentNumber(), current.getProduct());
        repository.save(current);
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
        save(contract);
        return contractEstablishment;
    }

    public void removeEstablishment(String id, String contractEstablishmentId) {
        Contract contract = findById(id);
        contract.removeContractEstablishmentBy(contractEstablishmentId);
        save(contract);
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

    public List<Contract> findByHirerDocument(String hirerDocument) {
        List<Contract> contracts = repository.findByHirerPersonDocumentNumber(hirerDocument);
        if(contracts.isEmpty()){
            throw UnovationExceptions.notFound().withErrors(CONTRACT_HIRER_NOT_FOUND);
        }
        return contracts;
    }

    public Set<Contract> findActivesByHirerId(String hirerId) {
        return repository.findByHirerIdAndSituation(hirerId, ContractSituation.ACTIVE);
    }

    public Optional<Contract> findByContractorAndProduct(String document, String productCode) {
        return repository.findByContractorPersonDocumentNumberAndProductCode(document, productCode);
    }

    public List<Contract> getMeValidContracts(String userEmail, String productCode) {
        UserDetail currentUser = userDetailService.getByEmail(userEmail);
        return getContractorValidContracts(currentUser.contractorId(), productCode);
    }

    public List<ServiceType> getMeValidContractServiceType(String userEmail, String productCode) {
        List<Contract> validContracts = getMeValidContracts(userEmail, productCode);
        return validContracts.stream().map(Contract::getServiceTypes).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<AccreditedNetwork> getMeValidContractNetworks(String userEmail, String productCode) {
        List<Contract> validContracts = getMeValidContracts(userEmail, productCode);
        return validContracts.stream().map(Contract::getProduct).map(Product::getAccreditedNetwork).collect(Collectors.toList());
    }

    public List<Contract> getContractorValidContracts(String contractorId, String productCode) {
        contractorService.getById(contractorId);
        Page<Contract> contractPage = getActiveContracts(contractorId,productCode);
        List<Contract> contracts = contractPage.getContent();
        return contracts.stream()
                    .filter(Contract::valid)
                    .collect(Collectors.toList());
    }

    private Page<Contract> getActiveContracts(String contractorId, String productCode) {
        ContractFilter contractFilter = createContractActiveFilter(contractorId,productCode);
        UnovationPageRequest pageRequest = new UnovationPageRequest();
        pageRequest.setSize(50);
        return findByFilter(contractFilter, pageRequest);
    }

    private ContractFilter createContractActiveFilter(String contractorId, String productCode) {
        ContractFilter contractFilter = new ContractFilter();
        contractFilter.setSituation(ContractSituation.ACTIVE);
        contractFilter.setContractor(contractorId);
        contractFilter.setProduct(productCode);
        return contractFilter;
    }

    public void markInstallmentAsPaidFrom(Order order) {
        Contract contract = getContract(order);
        installmentService.markAsPaid(contract.getId(),order.getValue());
    }

    private Contract getContract(Order order) {
        Optional<Contract> contract = findByContractorAndProduct(order.getDocumentNumber(), order.getProductId());
        return contract.orElseThrow(() -> UnovationExceptions.notFound().withErrors(CONTRACT_NOT_FOUND));
    }

    public void cancelByIdForIssuer(String id, Issuer issuer) {
        Contract current = getByIdForIssuer(id, issuer);
        cancel(current);
    }

    public Contract getByIdForIssuer(String id, Issuer issuer) {
        Optional<Contract> contract = repository.findByIdAndProductIssuerId(id, issuer.getId());
        return contract.orElseThrow(() -> UnovationExceptions.notFound().withErrors(CONTRACT_NOT_FOUND));
    }

    public void updateForIssuer(String id, Issuer issuer, Contract contract) {
        Contract current = getByIdForIssuer(id, issuer);
        update(current, contract);
    }
}
