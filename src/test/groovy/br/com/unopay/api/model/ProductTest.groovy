package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.credit.model.CreditInsertionType
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

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
        a.paymentInstrumentTypes == b.paymentInstrumentTypes
        a.serviceTypes.findAll { it in b.serviceTypes}
        a.serviceTypes.size() == b.serviceTypes.size()
        a.creditInsertionTypes == b.creditInsertionTypes
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
        a.paymentInstrumentTypes != b.paymentInstrumentTypes
        a.creditInsertionTypes != b.creditInsertionTypes
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
        a.paymentInstrumentTypes == b.paymentInstrumentTypes
        a.serviceTypes.findAll { it in b.serviceTypes}
        a.serviceTypes.size() == b.serviceTypes.size()
        a.creditInsertionTypes == b.creditInsertionTypes
        a.paymentInstrumentValidDays == b.paymentInstrumentValidDays
        a.situation == b.situation
        a.membershipFee == b.membershipFee
        a.creditInsertionFee == b.creditInsertionFee
        a.paymentInstrumentEmissionFee == b.paymentInstrumentEmissionFee
        a.paymentInstrumentSecondCopyFee == b.paymentInstrumentSecondCopyFee
        a.administrationCreditInsertionFee == b.administrationCreditInsertionFee
    }

    def 'should return error when code length is greater then maximum size'(){
        def product = new Product().with { code = 'AAAAAAAAA'; it }
        when:
        product.validate()

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find()?.logref == 'CODE_LENGTH_NOT_ACCEPTED'
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

    def 'should give error on creditInsertionType validation'(){
        given:
        Product a = Fixture.from(Product.class).gimme("valid")
        a.creditInsertionTypes = [CreditInsertionType.DIRECT_DEBIT, CreditInsertionType.BOLETO]

        when:
            a.validateCreditInsertionType(CreditInsertionType.CREDIT_CARD)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find()?.logref == 'CREDIT_INSERTION_TYPE_NOT_IN_PRODUCT'
    }

}