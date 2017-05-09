package br.com.unopay.api.service;

import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.bacen.service.PaymentRuleGroupService;
import br.com.unopay.api.model.Credit;
import br.com.unopay.api.model.CreditPaymentAccount;
import br.com.unopay.api.repository.CreditPaymentAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        creditPaymentAccount.setPaymentRuleGroup(paymentRuleGroupService.getById(creditPaymentAccount.getPaymentRuleGroupId()));
        creditPaymentAccount.setIssuer(issuerService.findById(creditPaymentAccount.getProductIssuerId()));
        creditPaymentAccount.setProduct(productService.findById(creditPaymentAccount.getProductId()));
        return repository.save(creditPaymentAccount);
    }

    public CreditPaymentAccount findById(String id) {
        return repository.findOne(id);
    }

    public CreditPaymentAccount create(Credit credit) {
        return save(new CreditPaymentAccount(credit));
    }
}
