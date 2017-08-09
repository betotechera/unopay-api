package br.com.unopay.api.service;

import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.bacen.service.PaymentRuleGroupService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.model.Credit;
import br.com.unopay.api.model.CreditInsertionType;
import br.com.unopay.api.model.CreditSituation;
import br.com.unopay.api.model.filter.CreditFilter;
import br.com.unopay.api.repository.CreditRepository;
import br.com.unopay.api.util.GenericObjectMapper;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.CREDIT_INSERT_TYPE_NOT_CONFIGURED;
import static br.com.unopay.api.uaa.exception.Errors.HIRER_CREDIT_NOT_FOUND;

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
        credit.validateCreditValue();
        defineDefaultValues(credit);
        validateReferences(credit);
        Credit inserted =  repository.save(credit);
        if(!inserted.isDirectDebit()){
            creditPaymentAccountService.register(inserted);
        }
        return credit;
    }

    @Transactional
    public void unblockCredit(Pair<String, BigDecimal> pair) {
        Set<Credit> credits = findDirectDebitProcessingByIssuerDocument(pair.getLeft());
        credits.stream()
                .filter(credit -> credit.valueIs(pair.getRight()))
                .findFirst().ifPresent(credit -> {
                    credit.setSituation(CreditSituation.CONFIRMED);
                    credit.defineAvailableValue();
                    credit.defineBlockedValue();
                    repository.save(credit);
                    creditPaymentAccountService.register(credit);
            log.info("unblock credit for issuer={} of value={} processed", pair.getLeft(), pair.getRight());
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

    public Credit  findById(String id) {
        Optional<Credit> credit = repository.findById(id);
        return credit.orElseThrow(() -> UnovationExceptions.notFound().withErrors(HIRER_CREDIT_NOT_FOUND));
    }

    public Set<Credit> findDirectDebitProcessingByIssuerDocument(String issuerDocument) {
        return repository.findByIssuerDocumentAndSituationAndCreditInsertionType(issuerDocument,
                CreditSituation.PROCESSING, CreditInsertionType.DIRECT_DEBIT);
    }

    private void defineDefaultCreditInsertionType(Credit credit) {
        if(StringUtils.isEmpty(defaultCreditInsertionType)){
            throw UnovationExceptions.unprocessableEntity().withErrors(CREDIT_INSERT_TYPE_NOT_CONFIGURED);
        }
        credit.defineCreditInsertionType(defaultCreditInsertionType);
    }

    private void validateReferences(Credit credit) {
        hirerService.findByDocumentNumber(credit.getHirerDocument());
        if(credit.withProduct()) {
            credit.setProduct(productService.findById(credit.getProductId()));
        }
        credit.setPaymentRuleGroup(paymentRuleGroupService.getById(credit.getPaymentRuleGroupId()));
    }


    @Transactional
    public void cancel(String id) {
        Credit credit = findById(id);
        credit.cancel();
        creditPaymentAccountService.subtract(credit);
        repository.save(credit);
    }

    @Transactional
    @RabbitListener(queues = Queues.UNOPAY_CREDIT_PROCESSED)
    public void creditReceiptNotify(String objectAsString) {
        ImmutablePair pair = genericObjectMapper.getAsObject(objectAsString, ImmutablePair.class);
        log.info("credit notification for issuer={} of value={} received", pair.getLeft(), pair.getRight());
        unblockCredit(pair);
    }

    public Page<Credit> findByFilter(CreditFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }
}
