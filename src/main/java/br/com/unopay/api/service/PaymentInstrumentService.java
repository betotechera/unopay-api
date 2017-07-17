package br.com.unopay.api.service;

import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.filter.PaymentInstrumentFilter;
import br.com.unopay.api.repository.PaymentInstrumentRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.config.CacheConfig.CONTRACTOR_INSTRUMENTS;
import static br.com.unopay.api.uaa.exception.Errors.EXTERNAL_ID_OF_PAYMENT_INSTRUMENT_ALREADY_EXISTS;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_INSTRUMENT_NOT_FOUND;

@Service
@Slf4j
public class PaymentInstrumentService {

    private PaymentInstrumentRepository repository;
    private ProductService productService;
    private ContractorService contractorService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public PaymentInstrumentService(PaymentInstrumentRepository repository,
                                    ProductService productService,
                                    ContractorService contractorService,
                                    PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.productService = productService;
        this.contractorService = contractorService;
        this.passwordEncoder = passwordEncoder;
    }

    @CacheEvict(value = CONTRACTOR_INSTRUMENTS, key = "#instrument.contractor.id")
    public PaymentInstrument save(PaymentInstrument instrument) {
        try {
            validateReference(instrument);
            instrument.setMeUp();
            if(instrument.hasPassword()){
                String encodedPassword = passwordEncoder.encode(instrument.getPassword());
                instrument.setPassword(encodedPassword);
            }
            instrument.validate();
            return repository.save(instrument);
        }catch (DataIntegrityViolationException e){
            log.info("External id={} of Payment Instrument already exists.", instrument.getExternalNumberId());
            throw UnovationExceptions.conflict().withErrors(EXTERNAL_ID_OF_PAYMENT_INSTRUMENT_ALREADY_EXISTS);
        }
    }

    public PaymentInstrument findById(String id) {
        Optional<PaymentInstrument> instrument = repository.findById(id);
        return  instrument.orElseThrow(()->UnovationExceptions.notFound().withErrors(PAYMENT_INSTRUMENT_NOT_FOUND));
    }

    public List<PaymentInstrument> findByContractorId(String contractorId) {
        List<PaymentInstrument> contracts = repository.findByContractorId(contractorId);
        return contracts;
    }

    public List<PaymentInstrument> findByContractorDocument(String contractorDocumentNumber) {
        List<PaymentInstrument> contracts = repository.findByContractorPersonDocumentNumber(contractorDocumentNumber);
        return contracts;
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
            log.info("External id={} of Payment Instrument already exists.", instrument.getExternalNumberId());
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
