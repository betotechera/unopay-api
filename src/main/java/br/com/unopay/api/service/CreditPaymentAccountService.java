package br.com.unopay.api.service;

import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.bacen.service.PaymentRuleGroupService;
import br.com.unopay.api.model.Credit;
import br.com.unopay.api.model.CreditPaymentAccount;
import br.com.unopay.api.repository.CreditPaymentAccountRepository;
import static com.google.common.collect.Lists.newArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        return repository.findOne(id);
    }

    public CreditPaymentAccount register(Credit credit) {
        CreditPaymentAccount creditPaymentAccount = repository.findByServiceType(credit.getServiceType());
        if(creditPaymentAccount != null){
            creditPaymentAccount.updateMyBalance(credit);
            return repository.save(creditPaymentAccount);
        }
        return save(new CreditPaymentAccount(credit));
    }

    public List<CreditPaymentAccount> findAll(){
        return newArrayList(repository.findAll());
    }

    private void validateReferences(CreditPaymentAccount creditPaymentAccount) {
        creditPaymentAccount.setPaymentRuleGroup(paymentRuleGroupService.getById(creditPaymentAccount.getPaymentRuleGroupId()));
        creditPaymentAccount.setIssuer(issuerService.findById(creditPaymentAccount.getProductIssuerId()));
        creditPaymentAccount.setProduct(productService.findById(creditPaymentAccount.getProductId()));
    }
}
