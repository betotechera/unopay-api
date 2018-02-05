package br.com.unopay.api.billing.creditcard.service;

import br.com.unopay.api.billing.creditcard.model.UserCreditCard;
import br.com.unopay.api.billing.creditcard.repository.UserCreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserCreditCardService {

    private UserCreditCardRepository repository;

    @Autowired
    public UserCreditCardService(UserCreditCardRepository repository) {
        this.repository = repository;
    }

    public UserCreditCard save(UserCreditCard userCreditCard) {
        return repository.save(userCreditCard);
    }

    public UserCreditCard findById(String id) {
        return repository.findOne(id);
    }

    public UserCreditCard create(UserCreditCard userCreditCard) {
        userCreditCard.setupMyCreate();
        userCreditCard.validateMe();
        userCreditCard.setCreatedDateTime(new Date());
        return save(userCreditCard);
    }
}
