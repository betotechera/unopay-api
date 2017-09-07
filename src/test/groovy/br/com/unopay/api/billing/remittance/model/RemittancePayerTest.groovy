package br.com.unopay.api.billing.remittance.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.bacen.model.Issuer

class RemittancePayerTest  extends FixtureApplicationTest {

    def 'should create from issuer'() {
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        when:
        def payer = new RemittancePayer(issuer)
        then:
        payer
        payer.agency == issuer.paymentAccount.bankAccount.agency
        payer.agencyDigit == issuer.paymentAccount.bankAccount.agencyDigit
        payer.accountNumber == issuer.paymentAccount.bankAccount.accountNumber
        payer.accountNumberDigit == issuer.paymentAccount.bankAccount.accountNumberDigit
        payer.streetName == issuer.person.address.streetName
        payer.number == issuer.person.address.number
        payer.complement == issuer.person.address.complement
        payer.district == issuer.person.address.district
        payer.city == issuer.person.address.city
        payer.state == issuer.person.address.state
        payer.zipCode == issuer.person.address.zipCode
        payer.bankCode == issuer.paymentAccount.bankAccount.bacenCode
        payer.name == issuer.person.name
        payer.bankAgreementNumberForCredit == issuer.paymentAccount.bankAgreementNumberForCredit
        payer.bankAgreementNumberForDebit == issuer.paymentAccount.bankAgreementNumberForDebit
    }


    def 'should not be equals'() {
        List list = Fixture.from(RemittancePayee.class).gimme(2, "valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }

    def 'if the credit has a credit'() {
        given:
        RemittancePayee a = Fixture.from(RemittancePayee.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }
}