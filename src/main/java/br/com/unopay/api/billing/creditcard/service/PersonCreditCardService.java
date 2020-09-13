package br.com.unopay.api.billing.creditcard.service;

import br.com.unopay.api.billing.creditcard.model.CreditCard;
import br.com.unopay.api.billing.creditcard.model.Gateway;
import br.com.unopay.api.billing.creditcard.model.PersonCreditCard;
import br.com.unopay.api.billing.creditcard.model.StoreCard;
import br.com.unopay.api.billing.creditcard.model.filter.PersonCreditCardFilter;
import br.com.unopay.api.billing.creditcard.repository.PersonCreditCardRepository;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Date;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.USER_CREDIT_CARD_NOT_FOUND;

@Service
public class PersonCreditCardService {

    private static final int NUMBER_OF_DIGITS = 4;

    private PersonCreditCardRepository personCreditCardRepository;
    private PersonService personService;
    @Setter private Gateway gateway;

    @Autowired
    public PersonCreditCardService(PersonCreditCardRepository repository,
                                   PersonService personService, Gateway gateway) {
        this.personCreditCardRepository = repository;
        this.personService = personService;
        this.gateway = gateway;
    }

    public PersonCreditCard save(PersonCreditCard personCreditCard) {
        return personCreditCardRepository.save(personCreditCard);
    }

    public PersonCreditCard create(PersonCreditCard personCreditCard) {
        personCreditCard.setupMyCreate();
        setValidUser(personCreditCard);
        return save(personCreditCard);
    }

    public PersonCreditCard storeForUser(Person person, CreditCard creditCard) {
        Optional<PersonCreditCard> found = findOptionalByLastFourDigitsForUser(creditCard.lastValidFourDigits(), person);
        return found.orElseGet(() -> {
            person.setIssuerDocument(creditCard.getIssuerDocument());
            gateway.storeCard(person, creditCard);
            PersonCreditCard personCreditCard = new PersonCreditCard(person, creditCard);
            return create(personCreditCard);
        });
    }

    public CreditCard storeCard(StoreCard person, CreditCard card) {
        return gateway.storeCard(person, card);
    }

    public PersonCreditCard update(String id, PersonCreditCard personCreditCard){
        personCreditCard.setupMyCreate();
        setValidUser(personCreditCard);
        PersonCreditCard current = findById(id);
        current.updateMe(personCreditCard);
        return personCreditCardRepository.save(current);
    }

    public PersonCreditCard updateForUser(String id, Person person, PersonCreditCard personCreditCard){
        PersonCreditCard current = findByIdForUser(id, person);
        return update(current.getId(), personCreditCard);
    }

    public void delete(String id) {
        findById(id);
        personCreditCardRepository.delete(id);
    }

    public void deleteForUser(String id, Person person){
        PersonCreditCard toBeDeleted = findByIdForUser(id, person);
        delete(toBeDeleted.getId());
    }

    public PersonCreditCard findById(String id) {
        return getUserCreditCardWithMonthAndYear(id, () -> personCreditCardRepository.findById(id));
    }

    public String getLastActiveTokenByUser(String userEmail) {
        return personCreditCardRepository
                .findByPersonPhysicalPersonDetailEmailAndExpirationDateGreaterThanEqual(userEmail, new Date())
                .map(PersonCreditCard::getGatewayToken).orElse(null);
    }

    public PersonCreditCard findByTokenForUser(String token, Person person) {
        return getUserCreditCardWithMonthAndYear(token, () ->
                personCreditCardRepository.findByGatewayTokenAndPersonId(token, person.getId()));
    }

    public PersonCreditCard findByIdForUser(String id, Person person){
        return getUserCreditCardWithMonthAndYear(id, () -> personCreditCardRepository.findByIdAndPersonId(id, person.getId()));
    }

    public PersonCreditCard findByNumberForPerson(String number, Person person) {
        return findByLastFourDigitsForUser(number.substring(number.length() - NUMBER_OF_DIGITS), person);
    }

    private PersonCreditCard findByLastFourDigitsForUser(String lastFourDigits, Person person) {
        return getUserCreditCardWithMonthAndYear
                (lastFourDigits, () -> findOptionalByLastFourDigitsForUser(lastFourDigits, person));
    }

    public Optional<PersonCreditCard> findOptionalByLastFourDigitsForUser(String lastFourDigits, Person person){
        return personCreditCardRepository.findByLastFourDigitsAndPersonId(lastFourDigits, person.getId());
    }

    private PersonCreditCard getUserCreditCardWithMonthAndYear(String id, Supplier<Optional<PersonCreditCard>> personCreditCard){
        Optional<PersonCreditCard> credit = personCreditCard.get();
        return credit.map(PersonCreditCard::defineMonthAndYearBasedOnExpirationDate).orElseThrow(() ->
                UnovationExceptions.notFound().withErrors(USER_CREDIT_CARD_NOT_FOUND.withOnlyArgument(id)));
    }

    public Page<PersonCreditCard> findByFilter(PersonCreditCardFilter filter, UnovationPageRequest pageable){
        return personCreditCardRepository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));

    }

    public void setValidUser (PersonCreditCard personCreditCard){
        personCreditCard.setPerson(personService.findById(personCreditCard.personId()));
    }

}
