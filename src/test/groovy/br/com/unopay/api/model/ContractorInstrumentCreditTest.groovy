package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class ContractorInstrumentCreditTest  extends FixtureApplicationTest {

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
