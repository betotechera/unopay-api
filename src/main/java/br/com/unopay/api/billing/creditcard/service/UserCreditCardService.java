package br.com.unopay.api.billing.creditcard.service;

import br.com.unopay.api.billing.creditcard.model.UserCreditCard;
import br.com.unopay.api.billing.creditcard.repository.UserCreditCardRepository;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.repository.UserDetailRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserCreditCardService {

    private UserCreditCardRepository userCreditCardRepository;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    public UserCreditCardService(UserCreditCardRepository repository) {
        this.userCreditCardRepository = repository;
    }

    public UserCreditCard save(UserCreditCard userCreditCard) {
        return userCreditCardRepository.save(userCreditCard);
    }

    public UserCreditCard findById(String id) {
        return userCreditCardRepository.findOne(id);
    }

    public UserCreditCard create(UserCreditCard userCreditCard) {
        userCreditCard.setupMyCreate();
        userCreditCard.setCreatedDateTime(new Date());
        userCreditCard.validateMe();
        validateUserCreditCard(userCreditCard);
        return save(userCreditCard);
    }

    public void validateUserCreditCard (UserCreditCard userCreditCard){
        validateUser(userCreditCard);
    }

    public void validateUser (UserCreditCard userCreditCard){
        if (userCreditCard.getUser() == null || userCreditCard.getUser().getId() == ""){
            throw UnovationExceptions.notFound().withErrors(Errors.USER_REQUIRED);
        }
        else {
            UserDetail userDetail = userCreditCard.getUser();
            if (userDetailRepository.findById(userDetail.getId()) != null){
                return;
            }
            else {
                throw UnovationExceptions.notFound().withErrors(Errors.USER_NOT_FOUND);
            }
        }
    }
}
