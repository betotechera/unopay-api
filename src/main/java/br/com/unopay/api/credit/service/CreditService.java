package br.com.unopay.api.credit.service;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.bacen.service.PaymentRuleGroupService;
import br.com.unopay.api.billing.creditcard.model.Transaction;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.credit.model.Credit;
import br.com.unopay.api.credit.model.CreditInsertionType;
import br.com.unopay.api.credit.model.CreditProcessed;
import br.com.unopay.api.credit.model.CreditSituation;
import br.com.unopay.api.credit.model.filter.CreditFilter;
import br.com.unopay.api.credit.repository.CreditRepository;
import br.com.unopay.api.infra.Notifier;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.notification.model.EventType;
import br.com.unopay.api.notification.service.NotificationService;
import br.com.unopay.api.service.ProductService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.credit.model.CreditInsertionType.CREDIT_CARD;
import static br.com.unopay.api.credit.model.CreditTarget.HIRER;
import static br.com.unopay.api.uaa.exception.Errors.HIRER_CREDIT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_RULE_GROUP_REQUIRED;

@Slf4j
@Service
public class CreditService {

    private CreditRepository repository;
    private HirerService hirerService;
    private ProductService productService;
    private PaymentRuleGroupService paymentRuleGroupService;
    private IssuerService issuerService;
    @Setter private CreditPaymentAccountService creditPaymentAccountService;
    @Setter private Notifier notifier;
    private NotificationService notificationService;

    public CreditService(){}

    @Autowired
    public CreditService(CreditRepository repository,
                         HirerService hirerService,
                         ProductService productService,
                         PaymentRuleGroupService paymentRuleGroupService,
                         IssuerService issuerService,
                         CreditPaymentAccountService creditPaymentAccountService,
                         Notifier notifier, NotificationService notificationService) {
        this.repository = repository;
        this.hirerService = hirerService;
        this.productService = productService;
        this.paymentRuleGroupService = paymentRuleGroupService;
        this.issuerService = issuerService;
        this.creditPaymentAccountService = creditPaymentAccountService;
        this.notifier = notifier;
        this.notificationService = notificationService;
    }

    @Transactional
    public Credit insert(Credit credit) {
        validateProductReference(credit);
        credit.setupMyCreate();
        incrementCreditNumber(credit);
        validateReferences(credit);
        credit.validateCreditValue();
        Credit inserted =  repository.save(credit);
        if(!inserted.isDirectDebit()){
           notifier.notify(Queues.HIRER_CREDIT_CREATED, inserted);
        }
        return credit;
    }

    @Transactional
    public void unblockCredit(CreditProcessed processed) {
        Set<Credit> credits = findProcessingByIssuerAndInsertionType(processed.getIssuerId(),
                                                                                processed.getInsertionType());
        credits.stream()
                .filter(credit -> credit.valueIs(processed.getValue()))
                .findFirst().ifPresent(credit -> {
                    credit.setSituation(CreditSituation.CONFIRMED);
                    credit.defineAvailableValue();
                    credit.defineBlockedValue();
                    repository.save(credit);
                    creditPaymentAccountService.register(credit);
                    notificationService.sendPaymentEmail(credit,  EventType.PAYMENT_APPROVED);
            log.info("unblock credit for issuer={} of value={} processed",processed.getIssuerId(),processed.getValue());
        });
    }

    private void incrementCreditNumber(Credit credit) {
        Optional<Credit> last = repository.findFirstByOrderByCreatedDateTimeDesc();
        Long lastCreditNumber = last.map(Credit::getCreditNumber).orElse(null);
        credit.defineCreditNumber(lastCreditNumber);
    }

    public Credit findByIdForHirer(String id, Hirer hirer) {
        Optional<Credit> credit = repository.findByIdAndHirerId(id, hirer.getId());
        return credit.orElseThrow(() -> UnovationExceptions.notFound().withErrors(HIRER_CREDIT_NOT_FOUND));
    }

    public Credit  findById(String id) {
        Optional<Credit> credit = repository.findById(id);
        return credit.orElseThrow(() -> UnovationExceptions.notFound().withErrors(HIRER_CREDIT_NOT_FOUND));
    }

    public Set<Credit> findProcessingByIssuerAndInsertionType(String issuerId, CreditInsertionType type){
        return repository.findByIssuerIdAndSituationAndCreditInsertionType(issuerId,
                CreditSituation.PROCESSING, type);
    }

    private void validateReferences(Credit credit) {
        hirerService.getById(credit.hirerId());
        if (!credit.withPaymentRuleGroup()) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_RULE_GROUP_REQUIRED);
        }
        if(!credit.withProduct()){
            credit.setIssuer(issuerService.findById(credit.issuerId()));
        }
        credit.setPaymentRuleGroup(paymentRuleGroupService.getById(credit.getPaymentRuleGroupId()));
    }

    private void validateProductReference(Credit credit) {
        if(credit.withProduct()) {
            Product product = productService.findById(credit.getProductId());
            credit.setProduct(product);
            credit.setIssuer(product.getIssuer());
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

    public void save(Credit credit) {
        repository.save(credit);
    }

    public void processAsPaid(String creditId) {
        Credit credit = findById(creditId);
        credit.setSituation(CreditSituation.CONFIRMED);
        save(credit);
        unblockCredit(credit);
    }

    public void process(Credit credit, Transaction transaction) {
        unblockCredit(credit);
        updateStatus(credit, transaction);
    }

    private void unblockCredit(Credit credit) {
        CreditProcessed processed = new CreditProcessed(credit.getHirer().getDocumentNumber(),
                credit.getValue(), CREDIT_CARD, HIRER);
        unblockCredit(processed);
    }

    private void updateStatus(Credit credit, Transaction transaction) {
        Credit current = findById(credit.getId());
        current.defineStatus(transaction.getStatus());
        save(current);
        if(!credit.confirmed()){
            notificationService.sendPaymentEmail(current,  EventType.PAYMENT_DENIED);
        }
    }
}
