package br.com.unopay.api.billing.creditcard.service;

import br.com.unopay.api.billing.creditcard.model.Gateway;
import br.com.unopay.api.billing.creditcard.model.PaymentRequest;
import br.com.unopay.api.billing.creditcard.model.Transaction;
import br.com.unopay.api.billing.creditcard.model.TransactionStatus;
import br.com.unopay.api.billing.creditcard.model.filter.TransactionFilter;
import br.com.unopay.api.billing.creditcard.repository.TransactionRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.INVALID_PAYMENT_VALUE;
import static br.com.unopay.api.uaa.exception.Errors.ORDER_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.ORDER_WITH_PENDING_TRANSACTION;
import static br.com.unopay.api.uaa.exception.Errors.ORDER_WITH_PROCESSED_TRANSACTION;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_REQUEST_REQUIRED;

@Slf4j
@Service
public class TransactionService {

    private TransactionRepository repository;
    @Setter private Gateway gateway;

    public TransactionService(){}

    @Autowired
    public TransactionService(TransactionRepository repository,
                              Gateway gateway){
        this.repository = repository;
        this.gateway = gateway;
    }

    public Transaction save(Transaction transaction) {
        return repository.save(transaction);
    }

    public Transaction findById(String id) {
        return repository.findOne(id);
    }

    public Transaction create(PaymentRequest paymentRequest) {
        validatePaymentRequest(paymentRequest);
        Transaction transaction = paymentRequest.toTransaction();
        validate(transaction);
        Transaction created = save(transaction);
        gateway.createTransaction(created);
        return save(created);
    }

    public Page<Transaction> findByFilter(TransactionFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private void validate(Transaction transaction) {
        if(transaction.getOrderId() == null){
            throw UnovationExceptions.conflict().withErrors(ORDER_REQUIRED);
        }
        transaction.checkValue();
        checkAlreadyCreated(transaction.getOrderId());
    }



    private void checkAlreadyCreated(String orderId) {
        Optional<Transaction> byOrder = repository.findByOrderId(orderId);
        byOrder.ifPresent(transaction -> {
            if(transaction.getStatus() == TransactionStatus.PENDING){
                throw UnovationExceptions.conflict().withErrors(ORDER_WITH_PENDING_TRANSACTION);
            }
            if(transaction.getStatus() == TransactionStatus.AUTHORIZED){
                throw UnovationExceptions.conflict().withErrors(ORDER_WITH_PROCESSED_TRANSACTION);
            }
        });
    }


    private void validatePaymentRequest(PaymentRequest paymentRequest) {
        if(paymentRequest == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_REQUEST_REQUIRED);
        }
    }
}
