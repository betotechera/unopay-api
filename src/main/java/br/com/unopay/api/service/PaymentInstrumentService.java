package br.com.unopay.api.service;

import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.filter.PaymentInstrumentFilter;
import br.com.unopay.api.repository.PaymentInstrumentRepository;
import static br.com.unopay.api.uaa.exception.Errors.EXTERNAL_ID_OF_PAYMENT_INSTRUMENT_ALREADY_EXISTS;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_INSTRUMENT_NOT_FOUND;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        return repository.findByContractorId(contractorId);
    }

    public void update(String id, PaymentInstrument instrument) {
        PaymentInstrument current = findById(id);
        validateReference(instrument);
        current.updateMe(instrument);
        if(instrument.hasPassword()){
            String encodedPassword = passwordEncoder.encode(instrument.getPassword());
            instrument.setPassword(encodedPassword);
        }

        instrument.validate();
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
