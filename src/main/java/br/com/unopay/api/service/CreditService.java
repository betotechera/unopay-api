package br.com.unopay.api.service;

import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.bacen.service.PaymentRuleGroupService;
import br.com.unopay.api.model.Credit;
import br.com.unopay.api.repository.CreditPaymentAccountRepository;
import br.com.unopay.api.repository.CreditRepository;
import static br.com.unopay.api.uaa.exception.Errors.CREDIT_INSERT_TYPE_NOT_CONFIGURED;
import static br.com.unopay.api.uaa.exception.Errors.DEFAULT_PAYMENT_RULE_GROUP_NOT_CONFIGURED;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CreditService {

    private CreditRepository repository;
    private HirerService hirerService;
    private ProductService productService;
    private PaymentRuleGroupService paymentRuleGroupService;
    @Setter
    private CreditPaymentAccountService creditPaymentAccountService;
    @Setter
    private CreditPaymentAccountRepository creditPaymentAccountRepository;

    @Setter
    @Getter
    @Value("${unopay.credit.defaultPaymentRuleGroup:}")
    private String defaultPaymentRuleGroup;

    @Setter
    @Getter
    @Value("${unopay.credit.defaultCreditInsertionType:}")
    private String defaultCreditInsertionType;

    @Autowired
    public CreditService(CreditRepository repository,
                         HirerService hirerService,
                         ProductService productService,
                         PaymentRuleGroupService paymentRuleGroupService,
                         CreditPaymentAccountService creditPaymentAccountService,
                         CreditPaymentAccountRepository creditPaymentAccountRepository) {
        this.repository = repository;
        this.hirerService = hirerService;
        this.productService = productService;
        this.paymentRuleGroupService = paymentRuleGroupService;
        this.creditPaymentAccountService = creditPaymentAccountService;
        this.creditPaymentAccountRepository = creditPaymentAccountRepository;
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

    private void defineDefaultValues(Credit credit) {
        if(!credit.withProduct()){
            defineDefaultCreditInsertionType(credit);
            defineDefaultPaymentRuleGroup(credit);
        }
        credit.setupMyCreate();
        Optional<Credit> last = repository.findFirstByOrderByCreatedDateTimeDesc();
        Long lastCreditNumber = last.map(Credit::getCreditNumber).orElse(null);
        credit.defineCreditNumber(lastCreditNumber);
    }

    public Credit  findById(String id) {
        return repository.findOne(id);
    }

    private void defineDefaultCreditInsertionType(Credit credit) {
        if(StringUtils.isEmpty(defaultCreditInsertionType)){
            throw UnovationExceptions.unprocessableEntity().withErrors(CREDIT_INSERT_TYPE_NOT_CONFIGURED);
        }
        credit.defineCreditInsertionType(defaultCreditInsertionType);
    }

    private void defineDefaultPaymentRuleGroup(Credit credit) {
        if(StringUtils.isEmpty(defaultPaymentRuleGroup)){
            throw UnovationExceptions.unprocessableEntity().withErrors(DEFAULT_PAYMENT_RULE_GROUP_NOT_CONFIGURED);
        }
        PaymentRuleGroup defaultPaymentRuleGroupResult = paymentRuleGroupService.getByCode(defaultPaymentRuleGroup);
        credit.setPaymentRuleGroup(defaultPaymentRuleGroupResult);
    }

    private void validateReferences(Credit credit) {
        hirerService.findByDocumentNumber(credit.getHirerDocument());
        if(credit.withProduct()) {
            credit.setProduct(productService.findById(credit.getProductId()));
        }
        credit.setPaymentRuleGroup(paymentRuleGroupService.getById(credit.getPaymentRuleGroupId()));
    }


    public void cancel(String id) {

    }
}
