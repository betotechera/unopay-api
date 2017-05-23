package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import groovy.time.TimeCategory
import spock.lang.Unroll

class ContractorInstrumentCreditTest  extends FixtureApplicationTest {

    def setup(){
        Integer.mixin(TimeCategory)
    }

    def 'should update me'(){
        given:
        ContractorInstrumentCredit a = Fixture.from(ContractorInstrumentCredit.class).gimme("allFields")
        ContractorInstrumentCredit b = Fixture.from(ContractorInstrumentCredit.class).gimme("allFields")
        b.getContract().setId('65545')
        b.getPaymentInstrument().setId('65545')
        b.getCreditPaymentAccount().setId('65545')

        when:
        a.updateMe(b)

        then:
        a.contract == b.contract
        a.paymentInstrument == b.paymentInstrument
        a.serviceType == b.serviceType
        a.creditInsertionType == b.creditInsertionType
        a.installmentNumber == b.installmentNumber
        a.createdDateTime == b.createdDateTime
        a.value == b.value
        a.situation == b.situation
        a.availableBalance == b.availableBalance
        a.blockedBalance == b.blockedBalance
    }

    def 'only fields with value should be updated'(){
        given:
        ContractorInstrumentCredit a = Fixture.from(ContractorInstrumentCredit.class).gimme("allFields")
        a.setCreatedDateTime(new Date())
        ContractorInstrumentCredit b = new ContractorInstrumentCredit()

        when:
        a.updateMe(b)

        then:
        a.contract != b.contract
        a.paymentInstrument != b.paymentInstrument
        a.serviceType != b.serviceType
        a.creditInsertionType != b.creditInsertionType
        a.installmentNumber != b.installmentNumber
        a.createdDateTime != b.createdDateTime
        a.value != b.value
        a.situation != b.situation
        a.availableBalance != b.availableBalance
        a.blockedBalance != b.blockedBalance
    }

    @Unroll
    'given a credit #situationUnderTest should return error'(){
        given:
        ContractorInstrumentCredit credit = Fixture.from(ContractorInstrumentCredit.class).gimme("allFields")
        credit.with { situation = situationUnderTest }

        when:
        credit.validate()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CREDIT_UNAVAILABLE'

        where:
        _|situationUnderTest
        _|CreditSituation.CANCELED
        _|CreditSituation.EXPIRED
        _|CreditSituation.PROCESSING
        _|CreditSituation.TO_COLLECT
        _|CreditSituation.CONFIRMED
    }

    def 'given a credit AVAILABLE should not return error'(){
        given:
        ContractorInstrumentCredit credit = Fixture.from(ContractorInstrumentCredit.class).gimme("allFields")
        credit.with { situation = CreditSituation.AVAILABLE }

        when:
        credit.validate()

        then:
        notThrown(UnprocessableEntityException)
    }

    def 'given a credit expired should return error'(){
        given:
        ContractorInstrumentCredit credit = Fixture.from(ContractorInstrumentCredit.class).gimme("allFields")
        credit.with { expirationDateTime = 1.second.ago }

        when:
        credit.validate()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CREDIT_EXPIRED'
    }

    def 'given a credit in progress should not return expired error'(){
        given:
        ContractorInstrumentCredit credit = Fixture.from(ContractorInstrumentCredit.class).gimme("allFields")
        credit.with { expirationDateTime = 1.second.from.now }

        when:
        credit.validate()

        then:
        notThrown(UnprocessableEntityException)
    }

    def 'should be equals'(){
        given:
        ContractorInstrumentCredit a = Fixture.from(ContractorInstrumentCredit.class).gimme("allFields")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        List list = Fixture.from(ContractorInstrumentCredit.class).gimme(2,"allFields")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }
}
