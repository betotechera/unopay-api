package br.com.unopay.api.billing.creditcard.gateway.payzen.service;

import br.com.unopay.api.billing.creditcard.gateway.payzen.model.Payzen;
import br.com.unopay.api.billing.creditcard.gateway.payzen.model.PayzenResponseTranslator;
import br.com.unopay.api.billing.creditcard.model.StoreCard;
import br.com.unopay.api.billing.creditcard.model.Amount;
import br.com.unopay.api.billing.creditcard.model.CreditCard;
import br.com.unopay.api.billing.creditcard.model.Gateway;
import br.com.unopay.api.billing.creditcard.model.Transaction;
import br.com.unopay.api.billing.creditcard.model.TransactionStatus;
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
    public CreditCard storeCard(StoreCard user, CreditCard card) {
        String token = payzen.storeCard(user, card);
        card.setToken(token);
        return card;
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
