package br.com.unopay.api.billing.creditcard.service;

import br.com.unopay.api.billing.creditcard.model.UserCreditCard;
import br.com.unopay.api.billing.creditcard.repository.UserCreditCardRepository;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.Optional;

import static br.com.unopay.api.uaa.exception.Errors.USER_CREDIT_CARD_NOT_FOUND;

@Service
public class UserCreditCardService {

    private UserCreditCardRepository userCreditCardRepository;
    private UserDetailService userDetailService;

    @Autowired
    public UserCreditCardService(UserCreditCardRepository repository,
                                 UserDetailService userDetailService) {
        this.userCreditCardRepository = repository;
        this.userDetailService = userDetailService;
    }

    public UserCreditCard save(UserCreditCard userCreditCard) {
        return userCreditCardRepository.save(userCreditCard);
    }

    public UserCreditCard create(UserCreditCard userCreditCard) {
        userCreditCard.validateMe();
        userCreditCard.setupMyCreate();
        validateUserCreditCard(userCreditCard);
        return save(userCreditCard);
    }

    public UserCreditCard update(String id, UserCreditCard userCreditCard){
        userCreditCard.validateMe();
        userCreditCard.setupMyCreate();
        validateUserCreditCard(userCreditCard);
        UserCreditCard current = findById(id);
        current.updateMe(userCreditCard);
        return userCreditCardRepository.save(current);
    }

    public void delete(String id) {
        findById(id);
        userCreditCardRepository.delete(id);
    }

    public UserCreditCard findById(String id) {
        Optional<UserCreditCard> userCreditCard = userCreditCardRepository.findById(id);
        return userCreditCard.orElseThrow(() ->
                UnovationExceptions.notFound().withErrors(USER_CREDIT_CARD_NOT_FOUND.withOnlyArgument(id)));
    }

    public void validateUserCreditCard (UserCreditCard userCreditCard){
        setValidUser(userCreditCard);
    }

    public void setValidUser (UserCreditCard userCreditCard){
        if (userCreditCard.getUser().getId() != null) {
            userCreditCard.setUser(userDetailService.getById(userCreditCard.getUser().getId()));
        }
    }
}
