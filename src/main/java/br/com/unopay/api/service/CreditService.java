package br.com.unopay.api.service;

import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.bacen.service.PaymentRuleGroupService;
import br.com.unopay.api.model.Credit;
import br.com.unopay.api.repository.CreditRepository;
import static br.com.unopay.api.uaa.exception.Errors.DEFAULT_PAYMENT_RULE_GROUP_NOT_CONFIGURED;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
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
    private PaymentRuleGroupService paymentRuleGroupService;

    @Setter
    @Value("${unopay.credit.defaultPaymentRuleGroup:}")
    private String defaultPaymentRuleGroup;

    @Autowired
    public CreditService(CreditRepository repository,
                         HirerService hirerService,
                         PaymentRuleGroupService paymentRuleGroupService) {
        this.repository = repository;
        this.hirerService = hirerService;
        this.paymentRuleGroupService = paymentRuleGroupService;
    }

    public Credit insert(Credit credit) {
        credit.validate();
        credit.setupMyCreate();
        updateBalances(credit);
        if(!credit.withProduct()){
            defineDefaultPaymentRuleGroup(credit);
        }
        hirerService.findByDocumentNumber(credit.getHirerDocument());
        log.info("Insert credit value={} from hirer={}, available balance={}, block balance={}", credit.getValue(),
                credit.getHirerDocument(), credit.getAvailableBalance(), credit.getBlockedBalance());
        return repository.save(credit);
    }

    private void defineDefaultPaymentRuleGroup(Credit credit) {
        if(StringUtils.isEmpty(defaultPaymentRuleGroup)){
            throw UnovationExceptions.unprocessableEntity().withErrors(DEFAULT_PAYMENT_RULE_GROUP_NOT_CONFIGURED);
        }
        PaymentRuleGroup defaultPaymentRuleGroupResult = paymentRuleGroupService.getByCode(defaultPaymentRuleGroup);
        credit.setPaymentRuleGroup(defaultPaymentRuleGroupResult);
    }

    private void updateBalances(Credit credit) {
        Optional<Credit> lastCredit = repository.findFirstByOrderByCreatedDateTimeDesc();
        credit.incrementAvailableBalance(lastCredit.orElse(null));
        credit.incrementBlockedBalance(lastCredit.orElse(null));
    }

    public Credit findById(String id) {
        return repository.findOne(id);
    }
}
