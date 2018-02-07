package br.com.unopay.api.billing.creditcard.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.billing.creditcard.model.UserCreditCard
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.bootcommons.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired

class UserCreditCardServiceTest extends SpockApplicationTests {

    @Autowired
    private UserCreditCardService userCreditCardService

    @Autowired
    private FixtureCreator fixtureCreator

    private UserDetail userDetail

    void setup(){
        userDetail = fixtureCreator.createUser()
    }

    def 'given a valid user credit card should be created'(){

        given:
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("user", userDetail)
        }})
        userCreditCard.setupMyCreate()

        when:
        UserCreditCard saved = userCreditCardService.save(userCreditCard)
        UserCreditCard found = userCreditCardService.findById(saved.id)

        then:
        found
    }

    def 'given a user credit card without creation date should be created'(){

        given:
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("user", userDetail);
            add("createdDateTime", null)
        }})

        when:
        UserCreditCard created = userCreditCardService.create(userCreditCard)
        UserCreditCard found = userCreditCardService.findById(created.id)

        then:
        timeComparator.compare(found.createdDateTime, new Date()) == 0
    }

    def 'given a user credit card with unknown user should return error'(){

        given:
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("user", userDetail.with { id = userDetail.getId().reverse(); it })
        }})

        when:
        userCreditCardService.create(userCreditCard)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'USER_NOT_FOUND'
    }

    def 'known user credit card should be deleted'(){

        given:
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("user", userDetail)
        }})
        def created = userCreditCardService.create(userCreditCard)

        when:
        userCreditCardService.delete(created.id)
        userCreditCardService.findById(created.id)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'USER_CREDIT_CARD_NOT_FOUND'
    }

    def 'unkown user credit card should not be deleted'(){

        when:
        userCreditCardService.findById('')

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'USER_CREDIT_CARD_NOT_FOUND'
    }

    def 'known user credit card should be updated'(){

        given:
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("user", userDetail)
            add("lastFourDigits", "1234")
        }})
        def created = userCreditCardService.create(userCreditCard)
        def fourDigits = "4321"
        userCreditCard.lastFourDigits = fourDigits

        when:
        userCreditCardService.update(created.id, userCreditCard)
        def result = userCreditCardService.findById(created.id)

        then:
        result.lastFourDigits == fourDigits
    }



}
