package br.com.unopay.api.billing.creditcard.repository;

import br.com.unopay.api.billing.creditcard.model.PersonCreditCard;
import br.com.unopay.api.billing.creditcard.model.filter.PersonCreditCardFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Date;
import java.util.Optional;

public interface PersonCreditCardRepository
        extends UnovationFilterRepository<PersonCreditCard, String, PersonCreditCardFilter> {

    Optional<PersonCreditCard> findById(String id);

    Optional<PersonCreditCard> findByIdAndPersonId(String id, String personId);

    Optional<PersonCreditCard> findFirstByPersonPhysicalPersonDetailEmailAndExpirationDateGreaterThanEqual(String userEmail, Date date);

    Optional<PersonCreditCard> findByGatewayTokenAndPersonId(String token, String personId);

    Optional<PersonCreditCard> findByLastFourDigitsAndPersonId(String lastFourDigits, String personId);
}
