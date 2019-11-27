package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

class IntegrationInformationTest extends FixtureApplicationTest {

    def 'should be equals'(){
        given:
        IntegrationInformation a = Fixture.from(IntegrationInformation.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        IntegrationInformation a = Fixture.from(IntegrationInformation.class).gimme("valid")
        IntegrationInformation b = Fixture.from(IntegrationInformation.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }

    void 'when validating IntegrationInformation without payzenShopId should return error'() {
        given:
        IntegrationInformation integrationInformation = Fixture.from(IntegrationInformation.class).gimme("valid")
        integrationInformation.payzenShopId = null

        when:
        integrationInformation.validate()

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find()?.logref == 'PAYZEN_SHOP_ID_REQUIRED'
    }

    void 'when validating IntegrationInformation without payzenShopKey should return error'() {
        given:
        IntegrationInformation integrationInformation = Fixture.from(IntegrationInformation.class).gimme("valid")
        integrationInformation.payzenShopKey = null

        when:
        integrationInformation.validate()

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find()?.logref == 'PAYZEN_SHOP_KEY_REQUIRED'
    }

}
