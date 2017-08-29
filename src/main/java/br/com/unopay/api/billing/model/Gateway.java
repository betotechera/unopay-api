package br.com.unopay.api.billing.model;


import br.com.unopay.api.model.Address;
import br.com.unopay.api.uaa.model.UserDetail;
import java.util.Set;

public interface Gateway {

    CreditCard storeCard(UserDetail user, CreditCard card, Address billingAddress);

    Set<CreditCard> getCards(String userId);

    Transaction createTransaction(Transaction transaction);

    Transaction cancelTransaction(Transaction transaction, Amount amount);

    Transaction captureTransaction(Transaction transaction);


}