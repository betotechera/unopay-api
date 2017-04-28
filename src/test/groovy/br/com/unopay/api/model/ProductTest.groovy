package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.model.Product

class ProductTest  extends FixtureApplicationTest {

    def 'should update me'(){
        given:
        Product a = Fixture.from(Product.class).gimme("valid")
        Product b = Fixture.from(Product.class).gimme("valid")
        b.getIssuer().setId('65545')
        b.getPaymentRuleGroup().setId('65545')
        b.getAccreditedNetwork().setId('65545')

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
        a.administrationCreditInsertionFee == b.administrationCreditInsertionFee
    }

    def 'fields without value should not be updated'(){
        given:
        Product a = Fixture.from(Product.class).gimme("valid")
        Product b = new Product()

        when:
        a.updateMe(b)

        then:
        a.code != b.code
        a.name != b.name
        a.type != b.type
        a.issuer != b.issuer
        a.paymentRuleGroup != b.paymentRuleGroup
        a.accreditedNetwork != b.accreditedNetwork
        a.paymentInstrumentType != b.paymentInstrumentType
        a.creditInsertionType != b.creditInsertionType
        a.minimumCreditInsertion != b.minimumCreditInsertion
        a.maximumCreditInsertion != b.maximumCreditInsertion
        a.paymentInstrumentValidDays != b.paymentInstrumentValidDays
        a.situation != b.situation
        a.membershipFee != b.membershipFee
        a.creditInsertionFee != b.creditInsertionFee
        a.paymentInstrumentEmissionFee != b.paymentInstrumentEmissionFee
        a.paymentInstrumentSecondCopyFee != b.paymentInstrumentSecondCopyFee
        a.administrationCreditInsertionFee != b.administrationCreditInsertionFee
    }

    def 'references fields without id value should not be updated'(){
        given:
        Product a = Fixture.from(Product.class).gimme("valid")
        Product b = Fixture.from(Product.class).gimme("valid")
        b.getPaymentRuleGroup().setId(null)
        b.getAccreditedNetwork().setId(null)
        b.getIssuer().setId(null)

        when:
        a.updateMe(b)

        then:
        a.code == b.code
        a.name == b.name
        a.type == b.type
        a.issuer != b.issuer
        a.paymentRuleGroup != b.paymentRuleGroup
        a.accreditedNetwork != b.accreditedNetwork
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
        a.administrationCreditInsertionFee == b.administrationCreditInsertionFee
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