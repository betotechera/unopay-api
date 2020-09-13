package br.com.unopay.api.billing.creditcard.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.billing.creditcard.model.CardBrand
import br.com.unopay.api.billing.creditcard.model.CreditCard
import br.com.unopay.api.billing.creditcard.model.Gateway
import br.com.unopay.api.billing.creditcard.model.GatewaySource
import br.com.unopay.api.billing.creditcard.model.PersonCreditCard
import br.com.unopay.api.billing.creditcard.model.filter.PersonCreditCardFilter
import br.com.unopay.api.model.Person
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import static org.hamcrest.Matchers.hasSize
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import static spock.util.matcher.HamcrestSupport.that

class PersonCreditCardServiceTest extends SpockApplicationTests {

    @Autowired
    private PersonCreditCardService personCreditCardService

    @Autowired
    private FixtureCreator fixtureCreator

    private Gateway gatewayMock = Mock(Gateway)

    private UserDetail userDetail

    void setup(){
        userDetail = fixtureCreator.createContractorUser()
        personCreditCardService.gateway = gatewayMock
    }

    def 'given a valid user credit card should be created'(){

        given:
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", userDetail.getContractor().getPerson())
        }})
        userCreditCard.setupMyCreate()

        when:
        PersonCreditCard saved = personCreditCardService.save(userCreditCard)
        PersonCreditCard found = personCreditCardService.findById(saved.id)

        then:
        found
    }

    def 'given a user credit card without creation date should be created'(){

        given:
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", userDetail.getContractor().getPerson())
            add("createdDateTime", null)
        }})

        when:
        PersonCreditCard created = personCreditCardService.create(userCreditCard)
        PersonCreditCard found = personCreditCardService.findById(created.id)

        then:
        timeComparator.compare(found.createdDateTime, new Date()) == 0
    }

    def 'given a user credit card with unknown user should return error'(){

        given:
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", new Person(id: '11222'))
        }})

        when:
        personCreditCardService.create(userCreditCard)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PERSON_NOT_FOUND'
    }

    def 'when find user credit card by known user should return'(){

        given:
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", userDetail.getContractor().getPerson())
        }})
        personCreditCardService.create(userCreditCard)
        def userCreditCardSearch = new PersonCreditCardFilter().with { person = userDetail.getContractor().getPerson().id; it }

        when:
        def page = new UnovationPageRequest() {{ setPage(1); setSize(20) }}
        Page<PersonCreditCard> userCreditCards = personCreditCardService.findByFilter(userCreditCardSearch, page)

        then:
        that userCreditCards.content, hasSize(1)
    }

    def 'known user credit card should be deleted'(){

        given:
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", userDetail.getContractor().getPerson())
        }})
        def created = personCreditCardService.create(userCreditCard)

        when:
        personCreditCardService.delete(created.id)
        personCreditCardService.findById(created.id)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'USER_CREDIT_CARD_NOT_FOUND'
    }

    def 'unkown user credit card should not be deleted'(){

        when:
        personCreditCardService.findById('')

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'USER_CREDIT_CARD_NOT_FOUND'
    }

    def 'known user credit card should be updated'(){

        given:
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", userDetail.getContractor().getPerson())
            add("lastFourDigits", "1234")
        }})
        def created = personCreditCardService.create(userCreditCard)
        def fourDigits = "4321"
        userCreditCard.lastFourDigits = fourDigits

        when:
        personCreditCardService.update(created.id, userCreditCard)
        def result = personCreditCardService.findById(created.id)

        then:
        result.lastFourDigits == fourDigits
    }

    def 'known user credit card should be found with its user'(){

        given:
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", userDetail.getContractor().getPerson())
        }})
        PersonCreditCard created = personCreditCardService.create(userCreditCard)

        when:
        PersonCreditCard found = personCreditCardService.findByIdForUser(created.id, userDetail.getContractor().getPerson())

        then:
        created.id == found.id
    }

    def 'known user credit card to be find with a different user should return error'(){

        given:
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", userDetail.getContractor().getPerson())
        }})
        UserDetail differentUser = fixtureCreator.createContractorUser()
        PersonCreditCard created = personCreditCardService.create(userCreditCard)

        when:
        personCreditCardService.findByIdForUser(created.id, differentUser.getContractor().getPerson())

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'USER_CREDIT_CARD_NOT_FOUND'
    }

    def 'known user credit card should be updated with its user'(){

        given:
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", userDetail.getContractor().getPerson())
            add("lastFourDigits", "1234")
        }})
        PersonCreditCard created = personCreditCardService.create(userCreditCard)
        def fourDigits = "4321"
        userCreditCard.lastFourDigits = fourDigits

        when:
        personCreditCardService.updateForUser(created.id, userDetail.getContractor().getPerson(), userCreditCard)
        def result = personCreditCardService.findById(created.id)

        then:
        result.lastFourDigits == fourDigits
    }

    def 'known user credit card to be updated with a different user should return error'(){

        given:
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", userDetail.getContractor().getPerson())
            add("lastFourDigits", "1234")
        }})
        PersonCreditCard newUserCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", userDetail.getContractor().getPerson())
            add("lastFourDigits", "4321")
        }})
        UserDetail differentUser = fixtureCreator.createContractorUser()
        PersonCreditCard created = personCreditCardService.create(userCreditCard)

        when:
        personCreditCardService.updateForUser(created.id, differentUser.getContractor().person, newUserCreditCard)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'USER_CREDIT_CARD_NOT_FOUND'
    }

    def 'known user credit card should be deleted with its user'(){

        given:
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", userDetail.getContractor().getPerson())
        }})
        PersonCreditCard created = personCreditCardService.create(userCreditCard)
        personCreditCardService.deleteForUser(created.id, userDetail.getContractor().getPerson())

        when:
        personCreditCardService.findById(created.id)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'USER_CREDIT_CARD_NOT_FOUND'
    }

    def 'known user credit card to be deleted with a different user should return error'(){

        given:
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", userDetail.getContractor().getPerson())
        }})
        PersonCreditCard created = personCreditCardService.create(userCreditCard)
        UserDetail differentUser = fixtureCreator.createContractorUser()

        when:
        personCreditCardService.deleteForUser(created.id, differentUser.getContractor().getPerson())

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'USER_CREDIT_CARD_NOT_FOUND'
    }

    def 'given UserDetail and CreditCard should create UserCreditCard with their values'(){

        given:
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard")

        when:
        PersonCreditCard stored = personCreditCardService.storeForUser(userDetail.getContractor().person, creditCard)
        PersonCreditCard found = personCreditCardService.findById(stored.id)

        then:
        found.personId() == userDetail.contractor.person.id
        found.holderName == creditCard.getHolderName()
        found.brand == CardBrand.fromCardNumber(creditCard.getNumber())
        found.lastFourDigits == creditCard.lastValidFourDigits()
        found.expirationMonth == creditCard.getExpiryMonth()
        found.expirationYear == creditCard.getExpiryYear()
        found.gatewaySource == GatewaySource.PAYZEN
        found.gatewayToken == creditCard.getToken()

    }

    def 'given valid UserCreditCard and its User should be found by its number'(){

        given:
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", userDetail.getContractor().getPerson())
        }})
        PersonCreditCard created = personCreditCardService.create(userCreditCard)

        when:
        PersonCreditCard found = personCreditCardService.findByNumberForPerson(created.lastFourDigits, userDetail.getContractor().getPerson())

        then:
        found.lastFourDigits == created.lastFourDigits
        found.personId() == userDetail.getContractor().getPerson().getId()
    }

    def 'given valid known credit card should not be stored'(){

        given:
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard")
        personCreditCardService.storeForUser(userDetail.getContractor().getPerson(), creditCard)

        when:
        personCreditCardService.storeForUser(userDetail.getContractor().getPerson(), creditCard)
        PersonCreditCard found = personCreditCardService.findByNumberForPerson(creditCard.number, userDetail.getContractor().person)

        then:
        found.lastFourDigits == creditCard.lastValidFourDigits()
    }


    def 'when store card should call gateway store card'(){

        given:
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard")

        when:
        personCreditCardService.storeForUser(userDetail.getContractor().getPerson(), creditCard)

        then:
        1 * gatewayMock.storeCard(userDetail, creditCard)
    }

    def 'given valid UserCreditCard and a different User should return error'(){

        given:
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", userDetail.getContractor().getPerson())
        }})
        PersonCreditCard created = personCreditCardService.create(userCreditCard)
        UserDetail differentUser = fixtureCreator.createContractorUser()

        when:
        personCreditCardService.findByNumberForPerson(created.lastFourDigits, differentUser.getContractor().getPerson())

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'USER_CREDIT_CARD_NOT_FOUND'
    }

    def 'given invalid UserCreditCard number should return error'(){

        given:
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", userDetail.getContractor().getPerson())
            add("lastFourDigits", "1234")
        }})
        personCreditCardService.create(userCreditCard)
        String differentNumber = "4321"

        when:
        personCreditCardService.findByNumberForPerson(differentNumber, userDetail.getContractor().getPerson())

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'USER_CREDIT_CARD_NOT_FOUND'
    }

    def 'given Credit Card with number length smaller than minimum should return error'(){

        given:
        CreditCard creditCard = Fixture.from(CreditCard).gimme("payzenCard", new Rule(){{
            add("number", "123")
        }})

        when:
        personCreditCardService.storeForUser(userDetail.getContractor().getPerson(), creditCard)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INVALID_NUMBER'
    }
}
