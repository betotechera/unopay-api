package br.com.unopay.api.service;

import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.bacen.service.PaymentRuleGroupService;
import br.com.unopay.api.model.Credit;
import br.com.unopay.api.model.PaymentAccount;
import br.com.unopay.api.repository.PaymentAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentAccountService {

    private PaymentAccountRepository repository;
    private PaymentRuleGroupService paymentRuleGroupService;
    private ProductService productService;
    private IssuerService issuerService;

    @Autowired
    public PaymentAccountService(PaymentAccountRepository repository,
                                 PaymentRuleGroupService paymentRuleGroupService,
                                 ProductService productService,
                                 IssuerService issuerService) {
        this.repository = repository;
        this.paymentRuleGroupService = paymentRuleGroupService;
        this.productService = productService;
        this.issuerService = issuerService;
    }

    public PaymentAccount save(PaymentAccount paymentAccount) {
        paymentAccount.setupMyCreate();
        paymentAccount.setPaymentRuleGroup(paymentRuleGroupService.getById(paymentAccount.getPaymentRuleGroupId()));
        paymentAccount.setIssuer(issuerService.findById(paymentAccount.getProductIssuerId()));
        paymentAccount.setProduct(productService.findById(paymentAccount.getProductId()));
        return repository.save(paymentAccount);
    }

    public PaymentAccount findById(String id) {
        return repository.findOne(id);
    }

    public PaymentAccount create(Credit credit) {
        return save(new PaymentAccount(credit));
    }
}
