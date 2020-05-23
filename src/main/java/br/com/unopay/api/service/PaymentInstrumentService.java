package br.com.unopay.api.service;

import br.com.unopay.api.InstrumentNumberGenerator;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.filter.IssuerFilter;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.PaymentInstrumentSituation;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.filter.PaymentInstrumentFilter;
import br.com.unopay.api.repository.PaymentInstrumentRepository;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public PaymentInstrument save(PaymentInstrument instrument) {
        validateReference(instrument);
        return create(instrument);
    }

    public PaymentInstrument createForIssuer(Issuer issuer, PaymentInstrument instrument) {
        validateReferenceForIssuer(issuer, instrument);
        return create(instrument);
    }

    private PaymentInstrument create(PaymentInstrument instrument) {
        try {
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

    public PaymentInstrument findByIdForIssuer(String id, Issuer issuer) {
        Optional<PaymentInstrument> instrument = repository.findByIdAndProductIssuerId(id, issuer.getId());
        return  instrument.orElseThrow(()->UnovationExceptions.notFound().withErrors(PAYMENT_INSTRUMENT_NOT_FOUND));
    }

    public PaymentInstrument findByNumber(String number) {
        Optional<PaymentInstrument> instrument = repository.findByNumber(number);
        return  instrument.orElseThrow(()->UnovationExceptions.notFound().withErrors(PAYMENT_INSTRUMENT_NOT_FOUND));
    }

    public Optional<PaymentInstrument> getById(String id) {
        return repository.findById(id);
    }

    public List<PaymentInstrument> findByContractorId(String contractorId) {
        return repository.findByContractorId(contractorId);
    }

    public PaymentInstrument findByIdAndContractorId(String id, String contractorId) {
        Optional<PaymentInstrument> instrument = repository.findByIdAndContractorId(id, contractorId);
        return  instrument.orElseThrow(()->UnovationExceptions.notFound().withErrors(PAYMENT_INSTRUMENT_NOT_FOUND));
    }

    public List<PaymentInstrument> findMyInstruments(String email){
        UserDetail currentUser = userDetailService.getByEmail(email);
        if(currentUser.isContractorType()) {
            return findByContractorDocument(currentUser.getContractor().getDocumentNumber());
        }
        return Collections.emptyList();
    }

    public List<PaymentInstrument> findByContractorDocument(String contractorDocumentNumber) {
        return repository.findByContractorPersonDocumentNumber(contractorDocumentNumber);
    }

    public Optional<PaymentInstrument> findDigitalWalletByContractorDocument(String documentNumber) {
        return repository.findFirstByContractorPersonDocumentNumberAndType(documentNumber, DIGITAL_WALLET);
    }

    public void update(String id, PaymentInstrument instrument) {
        PaymentInstrument current = findById(id);
        update(instrument, current);
    }

    public void updateForIssuer(String id, Issuer issuer, PaymentInstrument instrument) {
        PaymentInstrument current = findByIdForIssuer(id, issuer);
        update(instrument, current);
    }

    private void update(PaymentInstrument instrument, PaymentInstrument current) {
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

    public void deleteForIssuer(String id, Issuer issuer) {
        findByIdForIssuer(id, issuer);
        repository.delete(id);
    }

    public Page<PaymentInstrument> findByFilter(PaymentInstrumentFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private void validateReference(PaymentInstrument instrument) {
        instrument.setContractor(contractorService.getById(instrument.getContractor().getId()));
        instrument.setProduct(productService.findById(instrument.getProduct().getId()));
    }

    private void validateReferenceForIssuer(Issuer issuer, PaymentInstrument instrument) {
        instrument.setContractor(contractorService.getByIdForIssuer(instrument.getContractor().getId(), issuer));
        instrument.setProduct(productService.findByIdForIssuer(instrument.getProduct().getId(), issuer));
    }

    public void cancel(String contractorDocument, Product product) {
        List<PaymentInstrument> instruments = findByContractorDocument(contractorDocument);
        instruments.stream().filter(current -> current.hasProduct(product)).forEach(current-> {
            current.cancel();
            repository.save(current);
        });
    }

    public List<PaymentInstrument> listForMenu() {
        PaymentInstrumentFilter filter = new PaymentInstrumentFilter();
        UnovationPageRequest pageable = new UnovationPageRequest();
        pageable.setSize(50);
        return findByFilter(filter, pageable).getContent();
    }
}
