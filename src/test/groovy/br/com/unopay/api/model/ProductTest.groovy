package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.model.Product

class ProductTest  extends FixtureApplicationTest {

    def 'should update me'(){
        given:
        Product a = Fixture.from(Product.class).gimme("valid")
        Product b = Fixture.from(Product.class).gimme("valid")

        when:
         a.updateMe(b)

        then:
        a.code == b.code
        a.name == b.name
        a.type == b.type
        a.issuer == b.issuer
        a.paymentRuleGroup == b.paymentRuleGroup
        a.accreditedNetwork == b.accreditedNetwork
        a.paymentInstrumentType == b.paymentInstrumentType
        a.serviceType.findAll { it in b.serviceType}
        a.serviceType.size() == b.serviceType.size()
        a.creditInsertionType == b.creditInsertionType
        a.minimumCreditInsertion == b.minimumCreditInsertion
        a.maximumCreditInsertion == b.maximumCreditInsertion
        a.paymentInstrumentValidDays == b.paymentInstrumentValidDays
        a.situation == b.situation
        a.membershipFee == b.membershipFee
        a.creditInsertionFee == b.creditInsertionFee
        a.paymentInstrumentEmissionFee == b.paymentInstrumentEmissionFee
        a.paymentInstrumentSecondCopyFee == b.paymentInstrumentSecondCopyFee
        a.administrationCreditInsertionFee == a.administrationCreditInsertionFee
    }

    def 'should be equals'(){
        given:
        Product a = Fixture.from(Product.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        List list = Fixture.from(Product.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }
}