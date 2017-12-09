package br.com.unopay.api.credit.service;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.bacen.service.PaymentRuleGroupService;
import br.com.unopay.api.credit.model.Credit;
import br.com.unopay.api.credit.model.CreditInsertionType;
import br.com.unopay.api.credit.model.CreditProcessed;
import br.com.unopay.api.credit.model.CreditSituation;
import br.com.unopay.api.credit.model.filter.CreditFilter;
import br.com.unopay.api.credit.repository.CreditRepository;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.service.ProductService;
import br.com.unopay.api.util.GenericObjectMapper;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.CREDIT_INSERT_TYPE_NOT_CONFIGURED;
import static br.com.unopay.api.uaa.exception.Errors.HIRER_CREDIT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_RULE_GROUP_REQUIRED;

@Slf4j
@Service
public class CreditService {

    private CreditRepository repository;
    private HirerService hirerService;
    private ProductService productService;
    private PaymentRuleGroupService paymentRuleGroupService;
    @Setter private CreditPaymentAccountService creditPaymentAccountService;
    private GenericObjectMapper genericObjectMapper;
    @Value("${unopay.credit.defaultCreditInsertionType:}")
    private String defaultCreditInsertionType;

    public CreditService(){}

    @Autowired
    public CreditService(CreditRepository repository,
                         HirerService hirerService,
                         ProductService productService,
                         PaymentRuleGroupService paymentRuleGroupService,
                         CreditPaymentAccountService creditPaymentAccountService,
                         GenericObjectMapper genericObjectMapper) {
        this.repository = repository;
        this.hirerService = hirerService;
        this.productService = productService;
        this.paymentRuleGroupService = paymentRuleGroupService;
        this.creditPaymentAccountService = creditPaymentAccountService;
        this.genericObjectMapper = genericObjectMapper;
    }

    public Credit insert(Credit credit) {
        validateProductReference(credit);
        defineDefaultValues(credit);
        validateReferences(credit);
        credit.validateCreditValue();
        Credit inserted =  repository.save(credit);
        if(!inserted.isDirectDebit()){
            creditPaymentAccountService.register(inserted);
        }

        return credit;
    }

    @Transactional
    public void unblockCredit(CreditProcessed processed) {
        Set<Credit> credits = findProcessingByIssuerDocumentAndInsertionType(processed.getDocument(),
                                                                                processed.getInsertionType());
        credits.stream()
                .filter(credit -> credit.valueIs(processed.getValue()))
                .findFirst().ifPresent(credit -> {
                    credit.setSituation(CreditSituation.CONFIRMED);
                    credit.defineAvailableValue();
                    credit.defineBlockedValue();
                    repository.save(credit);
                    creditPaymentAccountService.register(credit);
            log.info("unblock credit for issuer={} of value={} processed",processed.getDocument(),processed.getValue());
        });
    }

    private void defineDefaultValues(Credit credit) {
        if(!credit.withProduct()){
            defineDefaultCreditInsertionType(credit);
        }
        credit.setupMyCreate();
        incrementCreditNumber(credit);
    }

    private void incrementCreditNumber(Credit credit) {
        Optional<Credit> last = repository.findFirstByOrderByCreatedDateTimeDesc();
        Long lastCreditNumber = last.map(Credit::getCreditNumber).orElse(null);
        credit.defineCreditNumber(lastCreditNumber);
    }

    public Credit findByIdForHirer(String id, Hirer hirer) {
        Optional<Credit> credit = repository.findByIdAndHirerDocument(id, hirer.getDocumentNumber());
        return credit.orElseThrow(() -> UnovationExceptions.notFound().withErrors(HIRER_CREDIT_NOT_FOUND));
    }

    public Credit  findById(String id) {
        Optional<Credit> credit = repository.findById(id);
        return credit.orElseThrow(() -> UnovationExceptions.notFound().withErrors(HIRER_CREDIT_NOT_FOUND));
    }

    public Set<Credit> findProcessingByIssuerDocumentAndInsertionType(String issuerDocument, CreditInsertionType type){
        return repository.findByIssuerDocumentAndSituationAndCreditInsertionType(issuerDocument,
                CreditSituation.PROCESSING, type);
    }

    private void defineDefaultCreditInsertionType(Credit credit) {
        if(StringUtils.isEmpty(defaultCreditInsertionType)){
            throw UnovationExceptions.unprocessableEntity().withErrors(CREDIT_INSERT_TYPE_NOT_CONFIGURED);
        }
        credit.defineCreditInsertionType(defaultCreditInsertionType);
    }

    private void validateReferences(Credit credit) {
        hirerService.findByDocumentNumber(credit.getHirerDocument());
        if (!credit.withPaymentRuleGroup()) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_RULE_GROUP_REQUIRED);
        }
        credit.setPaymentRuleGroup(paymentRuleGroupService.getById(credit.getPaymentRuleGroupId()));
    }

    private void validateProductReference(Credit credit) {
        if(credit.withProduct()) {
            Product product = productService.findById(credit.getProductId());
            credit.setProduct(product);
        }
    }

    @Transactional
    public void cancelForHirer(String id, Hirer hirer) {
        Credit credit = findByIdForHirer(id, hirer);
        cancel(credit);
    }

    @Transactional
    public void cancel(String id) {
        Credit credit = findById(id);
        cancel(credit);
    }

    private void cancel(Credit credit) {
        credit.cancel();
        creditPaymentAccountService.subtract(credit);
        repository.save(credit);
    }

    public Page<Credit> findByFilter(CreditFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }
}
