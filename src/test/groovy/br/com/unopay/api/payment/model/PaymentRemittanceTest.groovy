package br.com.unopay.api.payment.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.Issuer

class PaymentRemittanceTest extends FixtureApplicationTest{

    def 'should create from batch closed'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        when:
        def paymentRemittance = new PaymentRemittance(issuer, 10)

        then:
        paymentRemittance.issuer == issuer
        paymentRemittance.issuerBankCode == issuer.paymentAccount.bankAccount.bacenCode
        paymentRemittance.number
    }

    def 'should be created with processing situation'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        when:
        def paymentRemittance = new PaymentRemittance(issuer, 10)

        then:
        paymentRemittance.situation == RemittanceSituation.PROCESSING
    }

    def 'should be created with supplier payment service type'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        when:
        def paymentRemittance = new PaymentRemittance(issuer, 10)

        then:
        paymentRemittance.paymentServiceType == PaymentServiceType.SUPPLIER_PAYMENT
    }

    def 'should be created with credit operation type'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        when:
        def paymentRemittance = new PaymentRemittance(issuer, 10)

        then:
        paymentRemittance.operationType == PaymentOperationType.CREDIT
    }

    def 'give a establishment with same bank of issuer should be created with credit transfer option'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        establishment.bankAccount.bank = issuer.paymentAccount.bankAccount.bank
        def paymentRemittance = new PaymentRemittance(issuer, 10)

        when:
        paymentRemittance.defineTransferOption(establishment.bankAccount.bank)

        then:
        paymentRemittance.transferOption == PaymentTransferOption.CURRENT_ACCOUNT_CREDIT
    }

    def 'give a establishment without same bank of issuer should be created with credit transfer option'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        establishment.bankAccount.bank.bacenCode = 8888
        def paymentRemittance = new PaymentRemittance(issuer, 10)

        when:
        paymentRemittance.defineTransferOption(establishment.bankAccount.bank)

        then:
        paymentRemittance.transferOption == PaymentTransferOption.DOC_TED
    }

    def 'should be created with created date time'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        when:
        def paymentRemittance = new PaymentRemittance(issuer, 10)

        then:
        paymentRemittance.createdDateTime
    }

    def 'should not be equals'(){
        List list = Fixture.from(PaymentRemittance.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }

    def 'if the credit has a credit'(){
        given:
        PaymentRemittance a = Fixture.from(PaymentRemittance.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }
}
