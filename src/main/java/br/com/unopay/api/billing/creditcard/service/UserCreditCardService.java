package br.com.unopay.api.billing.creditcard.service;

import br.com.unopay.api.billing.creditcard.model.UserCreditCard;
import br.com.unopay.api.billing.creditcard.repository.UserCreditCardRepository;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
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
        userCreditCard.setupMyCreate();
        setValidUser(userCreditCard);
        return save(userCreditCard);
    }

    public UserCreditCard update(String id, UserCreditCard userCreditCard){
        userCreditCard.setupMyCreate();
        setValidUser(userCreditCard);
        UserCreditCard current = findById(id);
        current.updateMe(userCreditCard);
        return userCreditCardRepository.save(current);
    }

    public UserCreditCard updateForUser(String id, UserDetail user, UserCreditCard userCreditCard){
        UserCreditCard current = findByIdForUser(id, user);
        return update(current.getId(), userCreditCard);
    }

    public void delete(String id) {
        findById(id);
        userCreditCardRepository.delete(id);
    }

    public void deleteForUser(String id, UserDetail user){
        UserCreditCard toBeDeleted = findByIdForUser(id, user);
        delete(toBeDeleted.getId());
    }

    public UserCreditCard findById(String id) {
        Optional<UserCreditCard> userCreditCard = userCreditCardRepository.findById(id);
        if (userCreditCard.isPresent()){
            userCreditCard.get().defineMonthBasedOnExpirationDate();
            userCreditCard.get().defineYearBasedOnExpirationDate();
        }
        return userCreditCard.orElseThrow(() ->
                UnovationExceptions.notFound().withErrors(USER_CREDIT_CARD_NOT_FOUND.withOnlyArgument(id)));
    }

    public UserCreditCard findByIdForUser(String id, UserDetail user){
        Optional<UserCreditCard> userCreditCard = userCreditCardRepository.findByIdAndUserId(id, user.getId());
        if (userCreditCard.isPresent()){
            userCreditCard.get().defineMonthBasedOnExpirationDate();
            userCreditCard.get().defineYearBasedOnExpirationDate();
        }
        return userCreditCard.orElseThrow(() ->
                UnovationExceptions.notFound().withErrors(USER_CREDIT_CARD_NOT_FOUND.withOnlyArgument(id)));
    }

    public void setValidUser (UserCreditCard userCreditCard){
        if (userCreditCard.userId() != null) {
            userCreditCard.setUser(userDetailService.getById(userCreditCard.getUser().getId()));
        }
    }
}
