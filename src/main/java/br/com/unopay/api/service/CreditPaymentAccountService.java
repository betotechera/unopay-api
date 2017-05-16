package br.com.unopay.api.service;

import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.bacen.service.PaymentRuleGroupService;
import br.com.unopay.api.model.Credit;
import br.com.unopay.api.model.CreditPaymentAccount;
import br.com.unopay.api.repository.CreditPaymentAccountRepository;
import static br.com.unopay.api.uaa.exception.Errors.CREDIT_PAYMENT_ACCOUNT_NOT_FOUND;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import static com.google.common.collect.Lists.newArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CreditPaymentAccountService {

    private CreditPaymentAccountRepository repository;
    private PaymentRuleGroupService paymentRuleGroupService;
    private ProductService productService;
    private IssuerService issuerService;

    @Autowired
    public CreditPaymentAccountService(CreditPaymentAccountRepository repository,
                                       PaymentRuleGroupService paymentRuleGroupService,
                                       ProductService productService,
                                       IssuerService issuerService) {
        this.repository = repository;
        this.paymentRuleGroupService = paymentRuleGroupService;
        this.productService = productService;
        this.issuerService = issuerService;
    }

    public CreditPaymentAccount save(CreditPaymentAccount creditPaymentAccount) {
        creditPaymentAccount.setupMyCreate();
        validateReferences(creditPaymentAccount);
        return repository.save(creditPaymentAccount);
    }

    public CreditPaymentAccount findById(String id) {
        Optional<CreditPaymentAccount> creditPaymentAccount = repository.findById(id);
        return creditPaymentAccount.orElseThrow(()-> UnovationExceptions.notFound()
                                                            .withErrors(CREDIT_PAYMENT_ACCOUNT_NOT_FOUND));
    }

    public CreditPaymentAccount register(Credit credit) {
        List<CreditPaymentAccount> creditPayments = findByHirerDocument(credit.getHirerDocument());
        Optional<CreditPaymentAccount> creditPaymentAccount = credit.filterLastByProductAndService(creditPayments);
        creditPaymentAccount.ifPresent(creditPayment -> {
            creditPayment.updateMyBalance(credit);
            repository.save(creditPayment);
        });
        return creditPaymentAccount.orElseGet(()-> save(new CreditPaymentAccount(credit)));
    }

    public List<CreditPaymentAccount> findByHirerDocument(String hirerDocument) {
        return repository.findByHirerDocument(hirerDocument);
    }

    public List<CreditPaymentAccount> findAll(){
        return repository.findAll();
    }

    private void validateReferences(CreditPaymentAccount creditPaymentAccount) {
        creditPaymentAccount
                .setPaymentRuleGroup(paymentRuleGroupService.getById(creditPaymentAccount.getPaymentRuleGroupId()));
        if(creditPaymentAccount.withProduct()) {
            creditPaymentAccount.setIssuer(issuerService.findById(creditPaymentAccount.getProductIssuerId()));
            creditPaymentAccount.setProduct(productService.findById(creditPaymentAccount.getProductId()));
        }
    }

    public void subtract(Credit credit) {
        List<CreditPaymentAccount> creditPayments = findByHirerDocument(credit.getHirerDocument());
        Optional<CreditPaymentAccount> creditPaymentAccount = credit.filterLastByProductAndService(creditPayments);
        creditPaymentAccount.ifPresent(creditPayment -> {
            creditPayment.subtract(credit);
            repository.save(creditPayment);
        });
    }

    public void subtract(String id, BigDecimal value) {
            CreditPaymentAccount paymentAccount = findById(id);
            paymentAccount.subtract(value);
            repository.save(paymentAccount);
    }

    public void giveBack(String id, BigDecimal value) {
        CreditPaymentAccount paymentAccount = findById(id);
        paymentAccount.giveBack(value);
        repository.save(paymentAccount);
    }
}
