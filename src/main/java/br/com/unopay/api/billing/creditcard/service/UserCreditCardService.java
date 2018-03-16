package br.com.unopay.api.billing.creditcard.service;

import br.com.unopay.api.billing.creditcard.model.CreditCard;
import br.com.unopay.api.billing.creditcard.model.Gateway;
import br.com.unopay.api.billing.creditcard.model.UserCreditCard;
import br.com.unopay.api.billing.creditcard.model.filter.UserCreditCardFilter;
import br.com.unopay.api.billing.creditcard.repository.UserCreditCardRepository;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.function.Supplier;

import static br.com.unopay.api.uaa.exception.Errors.USER_CREDIT_CARD_NOT_FOUND;

@Service
public class UserCreditCardService {

    private static final int NUMBER_OF_DIGITS = 4;

    private UserCreditCardRepository userCreditCardRepository;
    private UserDetailService userDetailService;
    @Setter private Gateway gateway;

    @Autowired
    public UserCreditCardService(UserCreditCardRepository repository,
                                 UserDetailService userDetailService, Gateway gateway) {
        this.userCreditCardRepository = repository;
        this.userDetailService = userDetailService;
        this.gateway = gateway;
    }

    public UserCreditCard save(UserCreditCard userCreditCard) {
        return userCreditCardRepository.save(userCreditCard);
    }

    public UserCreditCard create(UserCreditCard userCreditCard) {
        userCreditCard.setupMyCreate();
        setValidUser(userCreditCard);
        return save(userCreditCard);
    }

    public UserCreditCard storeForUser(UserDetail userDetail, CreditCard creditCard) {
        Optional<UserCreditCard> found = findOptionalByLastFourDigitsForUser(creditCard.lastValidFourDigits(), userDetail);
        return found.orElseGet(() -> {
            gateway.storeCard(userDetail, creditCard);
            UserCreditCard userCreditCard = new UserCreditCard(userDetail, creditCard);
            return create(userCreditCard);
        });
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
        return getUserCreditCardWithMonthAndYear(id, () -> userCreditCardRepository.findById(id));
    }

    public UserCreditCard findByTokenForUser(String token, UserDetail user) {
        return getUserCreditCardWithMonthAndYear(token, () ->
                userCreditCardRepository.findByGatewayTokenAndUserId(token, user.getId()));
    }

    public UserCreditCard findByIdForUser(String id, UserDetail user){
        return getUserCreditCardWithMonthAndYear(id, () -> userCreditCardRepository.findByIdAndUserId(id, user.getId()));
    }

    public UserCreditCard findByNumberForUser(String number, UserDetail user) {
        return findByLastFourDigitsForUser(number.substring(number.length() - NUMBER_OF_DIGITS), user);
    }

    private UserCreditCard findByLastFourDigitsForUser(String lastFourDigits, UserDetail user) {
        return getUserCreditCardWithMonthAndYear
                (lastFourDigits, () -> findOptionalByLastFourDigitsForUser(lastFourDigits, user));
    }

    public Optional<UserCreditCard> findOptionalByLastFourDigitsForUser(String lastFourDigits, UserDetail user){
        return userCreditCardRepository.findByLastFourDigitsAndUserId(lastFourDigits, user.getId());
    }

    private UserCreditCard getUserCreditCardWithMonthAndYear(String id, Supplier<Optional<UserCreditCard>> userCreditCard){
        Optional<UserCreditCard> credit = userCreditCard.get();
        credit.ifPresent(UserCreditCard::defineMonthAndYearBasedOnExpirationDate);
        return credit.orElseThrow(() ->
                UnovationExceptions.notFound().withErrors(USER_CREDIT_CARD_NOT_FOUND.withOnlyArgument(id)));
    }

    public Page<UserCreditCard> findByFilter(UserCreditCardFilter filter, UnovationPageRequest pageable){
        return userCreditCardRepository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));

    }

    public void setValidUser (UserCreditCard userCreditCard){
        userCreditCard.setUser(userDetailService.getById(userCreditCard.userId()));
    }

}
