package br.com.unopay.api.billing.creditcard.gateway.payzen.service;

import br.com.unopay.api.billing.creditcard.gateway.payzen.model.Payzen;
import br.com.unopay.api.billing.creditcard.gateway.payzen.model.PayzenResponseTranslator;
import br.com.unopay.api.billing.creditcard.model.Amount;
import br.com.unopay.api.billing.creditcard.model.CreditCard;
import br.com.unopay.api.billing.creditcard.model.Gateway;
import br.com.unopay.api.billing.creditcard.model.Transaction;
import br.com.unopay.api.billing.creditcard.model.TransactionStatus;
import br.com.unopay.api.model.Address;
import br.com.unopay.api.uaa.model.UserDetail;
import eu.payzen.webservices.sdk.ServiceResult;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayzenService implements Gateway {

    private Payzen payzen;
    private PayzenResponseTranslator translator;

    @Autowired
    public PayzenService(Payzen payzen,
                         PayzenResponseTranslator translator){
        this.payzen = payzen;
        this.translator = translator;
    }

    @Override
    public CreditCard storeCard(UserDetail user, CreditCard card, Address billingAddress) {
        return null;
    }

    @Override
    public Set<CreditCard> getCards(String userId) {
        return null;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        ServiceResult serviceResult = payzen.createTransaction(transaction);
        TransactionStatus status = translator.translate(serviceResult);
        transaction.setStatus(status);
        return transaction;
    }

    @Override
    public Transaction cancelTransaction(Transaction transaction, Amount amount) {
        return null;
    }

    @Override
    public Transaction captureTransaction(Transaction transaction) {
        return null;
    }
}
