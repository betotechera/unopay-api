package br.com.unopay.api.billing.creditcard.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.billing.creditcard.model.UserCreditCard
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.uaa.service.UserDetailService
import br.com.unopay.bootcommons.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired

class UserCreditCardServiceTest extends SpockApplicationTests {

    @Autowired
    private UserCreditCardService userCreditCardService

    @Autowired
    private FixtureCreator fixtureCreator

    @Autowired
    UserDetailService userDetailService

    def 'given a valid user credit card should be created'(){

        given:
        def user = fixtureCreator.createUser()
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("user", user)
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
        def user = fixtureCreator.createUser()
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("user", user);
            add("createdDateTime", null)
        }})

        when:
        UserCreditCard created = userCreditCardService.create(userCreditCard)
        UserCreditCard found = userCreditCardService.findById(created.id)

        then:
        timeComparator.compare(found.createdDateTime, new Date()) == 0
    }

    def 'given a user credit card with blank user value should return error'(){

        given:
        def user = fixtureCreator.createUser()
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("user", user.with { id = ''; it })
        }})

        when:
        userCreditCardService.create(userCreditCard)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'USER_REQUIRED'
    }

    def 'given a user credit card with unknown user value should return error'(){

        given:
        def user = fixtureCreator.createUser()
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("user", user.with { id = user.getId().reverse(); it })
        }})

        when:
        userCreditCardService.create(userCreditCard)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'USER_NOT_FOUND'
    }
}
