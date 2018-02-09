package br.com.unopay.api.billing.creditcard.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.billing.creditcard.model.UserCreditCard
import br.com.unopay.api.billing.creditcard.model.filter.UserCreditCardFilter
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page

import static org.hamcrest.Matchers.hasSize
import static spock.util.matcher.HamcrestSupport.that

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

    def 'when find user credit card by known user should return'(){

        given:
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("user", userDetail)
        }})
        userCreditCardService.create(userCreditCard)
        def userCreditCardSearch = new UserCreditCardFilter().with { user = userDetail.id; it }

        when:
        def page = new UnovationPageRequest() {{ setPage(1); setSize(20) }}
        Page<UserCreditCard> userCreditCards = userCreditCardService.findByFilter(userCreditCardSearch, page)

        then:
        that userCreditCards.content, hasSize(1)
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

    def 'known user credit card should be found with its user'(){

        given:
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("user", userDetail)
        }})
        UserCreditCard created = userCreditCardService.create(userCreditCard)

        when:
        UserCreditCard found = userCreditCardService.findByIdForUser(created.id, userDetail)

        then:
        created.id == found.id
    }

    def 'known user credit card to be find with a different user should return error'(){

        given:
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("user", userDetail)
        }})
        UserDetail differentUser = fixtureCreator.createUser()
        UserCreditCard created = userCreditCardService.create(userCreditCard)

        when:
        userCreditCardService.findByIdForUser(created.id, differentUser)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'USER_CREDIT_CARD_NOT_FOUND'
    }

    def 'known user credit card should be updated with its user'(){

        given:
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("user", userDetail)
            add("lastFourDigits", "1234")
        }})
        UserCreditCard created = userCreditCardService.create(userCreditCard)
        def fourDigits = "4321"
        userCreditCard.lastFourDigits = fourDigits

        when:
        userCreditCardService.updateForUser(created.id, userDetail, userCreditCard)
        def result = userCreditCardService.findById(created.id)

        then:
        result.lastFourDigits == fourDigits
    }

    def 'known user credit card to be updated with a different user should return error'(){

        given:
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("user", userDetail)
            add("lastFourDigits", "1234")
        }})
        UserCreditCard newUserCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("user", userDetail)
            add("lastFourDigits", "4321")
        }})
        UserDetail differentUser = fixtureCreator.createUser()
        UserCreditCard created = userCreditCardService.create(userCreditCard)

        when:
        userCreditCardService.updateForUser(created.id, differentUser, newUserCreditCard)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'USER_CREDIT_CARD_NOT_FOUND'
    }

    def 'known user credit card should be deleted with its user'(){

        given:
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("user", userDetail)
        }})
        UserCreditCard created = userCreditCardService.create(userCreditCard)
        userCreditCardService.deleteForUser(created.id, userDetail)

        when:
        userCreditCardService.findById(created.id)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'USER_CREDIT_CARD_NOT_FOUND'
    }

    def 'known user credit card to be deleted with a different user should return error'(){

        given:
        UserCreditCard userCreditCard = Fixture.from(UserCreditCard).gimme("valid", new Rule(){{
            add("user", userDetail)
        }})
        UserCreditCard created = userCreditCardService.create(userCreditCard)
        UserDetail differentUser = fixtureCreator.createUser()

        when:
        userCreditCardService.deleteForUser(created.id, differentUser)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'USER_CREDIT_CARD_NOT_FOUND'
    }

}
