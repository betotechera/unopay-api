package br.com.unopay.api.billing.creditcard.model;


import java.util.Set;

public interface Gateway {

    CreditCard storeCard(StoreCard user, CreditCard card);

    Set<CreditCard> getCards(String userId);

    Transaction cancelTransaction(Transaction transaction, Amount amount);

    Transaction createTransaction(Transaction transaction);

    Transaction captureTransaction(Transaction transaction);


}