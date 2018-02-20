package br.com.unopay.api.billing.creditcard.repository;

import br.com.unopay.api.billing.creditcard.model.UserCreditCard;
import br.com.unopay.api.billing.creditcard.model.filter.UserCreditCardFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;

import java.util.Optional;

public interface UserCreditCardRepository
        extends UnovationFilterRepository<UserCreditCard, String, UserCreditCardFilter> {

    Optional<UserCreditCard> findById(String id);

    Optional<UserCreditCard> findByIdAndUserId(String id, String userId);

    Optional<UserCreditCard> findByLastFourDigitsAndUserId(String lastFourDigits, String userId);
}
