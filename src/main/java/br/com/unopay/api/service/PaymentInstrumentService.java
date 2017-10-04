package br.com.unopay.api.service;

import br.com.unopay.api.InstrumentNumberGenerator;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.filter.PaymentInstrumentFilter;
import br.com.unopay.api.repository.PaymentInstrumentRepository;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.List;
import java.util.Optional;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.config.CacheConfig.CONTRACTOR_INSTRUMENTS;
import static br.com.unopay.api.model.PaymentInstrumentType.DIGITAL_WALLET;
import static br.com.unopay.api.uaa.exception.Errors.EXTERNAL_ID_OF_PAYMENT_INSTRUMENT_ALREADY_EXISTS;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_INSTRUMENT_NOT_FOUND;

@Service
@Slf4j
@Data
public class PaymentInstrumentService {

    private PaymentInstrumentRepository repository;
    private ProductService productService;
    private ContractorService contractorService;
    private PasswordEncoder passwordEncoder;
    private InstrumentNumberGenerator instrumentNumberGenerator;
    private UserDetailService userDetailService;

    @Autowired
    public PaymentInstrumentService(PaymentInstrumentRepository repository,
                                    ProductService productService,
                                    ContractorService contractorService,
                                    PasswordEncoder passwordEncoder,
                                    InstrumentNumberGenerator instrumentNumberGenerator,
                                    UserDetailService userDetailService) {
        this.repository = repository;
        this.productService = productService;
        this.contractorService = contractorService;
        this.passwordEncoder = passwordEncoder;
        this.instrumentNumberGenerator = instrumentNumberGenerator;
        this.userDetailService = userDetailService;
    }

    @CacheEvict(value = CONTRACTOR_INSTRUMENTS, key = "#instrument.contractor.id")
    public PaymentInstrument save(PaymentInstrument instrument) {
        try {
            validateReference(instrument);
            instrument.setMeUp(generateNumber(instrument));
            if(instrument.hasPassword()){
                String encodedPassword = passwordEncoder.encode(instrument.getPassword());
                instrument.setPassword(encodedPassword);
            }
            instrument.validate();
            return repository.save(instrument);
        }catch (DataIntegrityViolationException e){
            log.info("External id={} of Payment Instrument already exists.", instrument.getExternalNumberId(), e);
            throw UnovationExceptions.conflict().withErrors(EXTERNAL_ID_OF_PAYMENT_INSTRUMENT_ALREADY_EXISTS);
        }
    }

    private String generateNumber(PaymentInstrument instrument) {
        String instrumentNumber = instrumentNumberGenerator.generate(instrument.issuerBin());
        return containsNumber(instrumentNumber)? generateNumber(instrument) : instrumentNumber;
    }

    private boolean containsNumber(String instrumentNumber) {
        return repository.countByNumber(instrumentNumber) > 0;
    }

    public PaymentInstrument findById(String id) {
        Optional<PaymentInstrument> instrument = getById(id);
        return  instrument.orElseThrow(()->UnovationExceptions.notFound().withErrors(PAYMENT_INSTRUMENT_NOT_FOUND));
    }

    public Optional<PaymentInstrument> getById(String id) {
        return repository.findById(id);
    }

    public List<PaymentInstrument> findByContractorId(String contractorId) {
        return repository.findByContractorId(contractorId);
    }

    public List<PaymentInstrument> findMyInstruments(String email){
        UserDetail currentUser = userDetailService.getByEmail(email);
        return findByContractorDocument(currentUser.getContractor().getDocumentNumber());
    }

    public List<PaymentInstrument> findByContractorDocument(String contractorDocumentNumber) {
        return repository.findByContractorPersonDocumentNumber(contractorDocumentNumber);
    }

    public Optional<PaymentInstrument> findDigitalWalletByContractorDocument(String documentNumber) {
        return repository.findFirstByContractorPersonDocumentNumberAndType(documentNumber, DIGITAL_WALLET);
    }

    @CacheEvict(value = CONTRACTOR_INSTRUMENTS, key = "#instrument.contractor.id")
    public void update(String id, PaymentInstrument instrument) {
        PaymentInstrument current = findById(id);
        validateReference(instrument);
        instrument.validate();
        current.updateAllExcept(instrument, "password");
        if(instrument.isResetPassword()){
            current.setPassword(null);
        }
        try{
            repository.save(current);
        }catch (DataIntegrityViolationException e){
            log.info("External id={} of Payment Instrument already exists.", instrument.getExternalNumberId(), e);
            throw UnovationExceptions.conflict().withErrors(EXTERNAL_ID_OF_PAYMENT_INSTRUMENT_ALREADY_EXISTS);
        }
    }

    public void changePassword(String id, String password){
        String encodedPassword = passwordEncoder.encode(password);
        PaymentInstrument current = findById(id);
        current.setPassword(encodedPassword);
        repository.save(current);
    }

    public void checkPassword(String paymentInstrumentId, String password) {
        if(!passwordMatches(paymentInstrumentId,password)){
            throw UnovationExceptions.unauthorized();
        }
    }

    public boolean passwordMatches(String id, String password){
        PaymentInstrument current = findById(id);
        return passwordEncoder.matches(password == null ? "" : password, current.getPassword());
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

    public Page<PaymentInstrument> findByFilter(PaymentInstrumentFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private void validateReference(PaymentInstrument instrument) {
        instrument.setContractor(contractorService.getById(instrument.getContractor().getId()));
        instrument.setProduct(productService.findById(instrument.getProduct().getId()));
    }
}
