package br.com.unopay.api.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.csv.ContractorCsv;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractEstablishment;
import br.com.unopay.api.model.ContractSituation;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.filter.ContractFilter;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.repository.ContractEstablishmentRepository;
import br.com.unopay.api.repository.ContractRepository;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import javax.validation.Validator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static br.com.unopay.api.config.CacheConfig.CONTRACTS;
import static br.com.unopay.api.config.CacheConfig.SERVICE_AUTHORIZES;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACTOR_CONTRACT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_ALREADY_EXISTS;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_ESTABLISHMENT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_HIRER_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.EXISTING_CONTRACTOR;
import static br.com.unopay.api.uaa.exception.Errors.FILE_WIHOUT_LINES_OR_HEADER;

@Timed
@Slf4j
@Service
public class ContractService {

    private ContractRepository repository;
    private HirerService hirerService;
    private ContractorService contractorService;
    private ProductService productService;
    private ContractEstablishmentRepository contractEstablishmentRepository;
    private PaymentInstrumentService paymentInstrumentService;
    private UserDetailService userDetailService;
    private ContractInstallmentService installmentService;
    private Validator validator;

    @Autowired
    public ContractService(ContractRepository repository, HirerService hirerService,
                           ContractorService contractorService, ProductService productService,
                           ContractEstablishmentRepository contractEstablishmentRepository,
                           PaymentInstrumentService paymentInstrumentService,
                           UserDetailService userDetailService,
                           ContractInstallmentService installmentService,
                           Validator validator) {
        this.repository = repository;
        this.hirerService = hirerService;
        this.contractorService = contractorService;
        this.productService = productService;
        this.contractEstablishmentRepository = contractEstablishmentRepository;
        this.paymentInstrumentService = paymentInstrumentService;
        this.userDetailService = userDetailService;
        this.installmentService = installmentService;
        this.validator = validator;
    }

    public Contract create(Contract contract) {
        return create(contract, false);
    }

    public Contract create(Contract contract, Boolean forHirer) {
        try {
            checkContract(contract);
            validateReferences(contract);
            contract.validate();
            contract.checkFields();
            contract.setupMeUp();
            Contract created = repository.save(contract);
            createInstallment(forHirer, created);
            return created;
        }catch (DataIntegrityViolationException e){
            log.info("Contract with code={} already exists",  contract.getCode(), e);
            throw UnovationExceptions.conflict()
                    .withErrors(CONTRACT_ALREADY_EXISTS.withOnlyArgument(contract.getCode()));
        }
    }

    private void createInstallment(Boolean forHirer, Contract created) {
        if(forHirer){
            installmentService.createForHirer(created);
            return;
        }
        installmentService.create(created);
    }

    private void checkContract(Contract contract) {
        Optional<Contract> contractOptional = findByContractorAndProduct(contract.getContractor().getDocumentNumber(),
                contract.getProduct().getCode());
        contractOptional.ifPresent((ThrowingConsumer)-> {
            throw UnovationExceptions.conflict()
                    .withErrors(CONTRACT_ALREADY_EXISTS.withOnlyArgument(contract.getCode()));
        });
    }

    @Transactional
    public Contract dealCloseWithIssuerAsHirer(Person person, String productCode){
        return dealClose(person, productCode, null);
    }

    @Transactional
    public Contract dealClose(Person person, String productCode, String hirerDocument){
        checkContractor(person.documentNumber());
        Product product = productService.findByCode(productCode);
        Contractor contractor = contractorService.create(new Contractor(person));
        Hirer hirer = getHirer(hirerDocument, product);
        Contract contract = new Contract(product);
        contract.setHirer(hirer);
        contract.setContractor(contractor);
        paymentInstrumentService.save(new PaymentInstrument(contractor, product));
        userDetailService.create(new UserDetail(contractor));
        Contract created = create(contract, hirerDocument != null);
        if(!contract.withMembershipFee()) {
            installmentService.markAsPaid(created.getId(), product.getInstallmentValue());
        }
        return contract;
    }



    private Hirer getHirer(String hirerDocument, Product product) {
        if(hirerDocument != null) {
            return hirerService.findByDocumentNumber(hirerDocument);
        }
        return hirerService.findByDocumentNumber(product.getIssuer().documentNumber());
    }

    private void checkContractor(String documentNumber) {
        Optional<Contractor> contractor = contractorService.getOptionalByDocument(documentNumber);
        contractor.ifPresent(c -> {
            throw UnovationExceptions.conflict().withErrors(EXISTING_CONTRACTOR.withOnlyArgument(documentNumber));
        });
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

    @Cacheable(value = CONTRACTS, key = "#id")
    public Contract findById(String id) {
        Optional<Contract> contract = repository.findById(id);
        return contract.orElseThrow(()->UnovationExceptions.notFound().withErrors(CONTRACT_NOT_FOUND));
    }

    @Cacheable(value = CONTRACTS, key = "#code")
    public Contract findByCode(Long code) {
        Optional<Contract> contract = repository.findByCode(code);
        return contract.orElseThrow(()->UnovationExceptions.notFound().withErrors(CONTRACT_NOT_FOUND));
    }

    @Cacheable(value = CONTRACTS, key = "#id + '_' + #hirer.id")
    public Contract findByIdForHirer(String id, Hirer hirer) {
        Optional<Contract> contract = repository.findByIdAndHirerId(id, hirer.getId());
        return contract.orElseThrow(()->UnovationExceptions.notFound().withErrors(CONTRACT_NOT_FOUND));
    }

    public Contract getByIdAndContractorId(String contractId, String contractorId) {
        List<Contract> contracts = repository.findByContractorId(contractorId);
        Optional<Contract> contract = contracts.stream()
                                    .filter(c -> Objects.equals(c.getId(), contractId)).findFirst();
        return contract.orElseThrow(()->  UnovationExceptions.notFound().withErrors(CONTRACTOR_CONTRACT_NOT_FOUND));
    }

    @Transactional
    public void delete(String id) {
        findById(id);
        installmentService.deleteByContract(id);
        repository.delete(id);
    }

    @Cacheable(value = SERVICE_AUTHORIZES, key = "T(java.util.Objects).hash(#filter)")
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

    public List<Contract> findByHirerDocument(String hirerDocument) {
        List<Contract> contracts = repository.findByHirerPersonDocumentNumber(hirerDocument);
        if(contracts.isEmpty()){
            throw UnovationExceptions.notFound().withErrors(CONTRACT_HIRER_NOT_FOUND);
        }
        return contracts;
    }

    public Set<Contract> findByHirerId(String hirerId) {
        return repository.findByHirerId(hirerId);
    }

    public Optional<Contract> findByContractorAndProduct(String document, String productId) {
        return repository.findByContractorPersonDocumentNumberAndProductId(document, productId);
    }

    public List<Contract> getMeValidContracts(String userEmail, String productCode) {
        UserDetail currentUser = userDetailService.getByEmail(userEmail);
        return getContractorValidContracts(currentUser.contractorId(), productCode);
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
        return findByFilter(contractFilter, new UnovationPageRequest());
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

    @Transactional
    public void dealCloseFromCsvForCurrentUser(String email, MultipartFile file){
        UserDetail currentUser = userDetailService.getByEmail(email);
        dealCloseFromCsv(currentUser.myHirer().map(Hirer::getDocumentNumber).orElse(""), file);
    }

    @SneakyThrows
    @Transactional
    public void dealCloseFromCsv(String hirerDocument, MultipartFile file) {
        List<ContractorCsv> dealCloseCsvs = getDealCloseCsvs(file);
        final int[] lineNumber = {0};
        dealCloseCsvs.forEach(line -> {
            lineNumber[0]++;
            line.validate(validator, lineNumber[0]);
            dealClose(line.toPerson(), line.getProduct(), hirerDocument);
        });
        if(dealCloseCsvs.isEmpty()){
            throw UnovationExceptions.badRequest().withErrors(FILE_WIHOUT_LINES_OR_HEADER);
        }
    }

    private List<ContractorCsv> getDealCloseCsvs(MultipartFile multipartFile) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(multipartFile.getInputStream());
        return new CsvToBeanBuilder<ContractorCsv>(inputStreamReader)
                .withType(ContractorCsv.class).withSeparator(';').build().parse();
    }
}
