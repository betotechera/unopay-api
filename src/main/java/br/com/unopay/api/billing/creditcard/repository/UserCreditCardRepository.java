package br.com.unopay.api.billing.creditcard.repository;

import br.com.unopay.api.billing.creditcard.model.UserCreditCard;
import br.com.unopay.api.billing.creditcard.model.filter.UserCreditCardFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;

public interface UserCreditCardRepository
        extends UnovationFilterRepository<UserCreditCard, String, UserCreditCardFilter> {
}
