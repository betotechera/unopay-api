package br.com.unopay.api.payment.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.bacen.model.Establishment

class RemittancePayeeTest  extends FixtureApplicationTest{

    def 'should create from batch closing'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        def paymentAccountBank = 241

        when:
        def payee = new RemittancePayee(establishment, paymentAccountBank)

        then:
        payee
        payee.agency == establishment.bankAccount.agency
        payee.agencyDigit == establishment.bankAccount.agencyDigit
        payee.accountNumber == establishment.bankAccount.accountNumber
        payee.accountNumberDigit == establishment.bankAccount.accountNumberDigit
        payee.streetName == establishment.person.address.streetName
        payee.number == establishment.person.address.number
        payee.complement == establishment.person.address.complement
        payee.zipCode == establishment.person.address.zipCode
        payee.district == establishment.person.address.district
        payee.city == establishment.person.address.city
        payee.state == establishment.person.address.state
        payee.bankCode == establishment.bankAccount.bacenCode
        payee.payerBankCode == paymentAccountBank
        payee.name == establishment.person.name
    }

    def 'should not be equals'(){
        List list = Fixture.from(RemittancePayee.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }

    def 'if the credit has a credit'(){
        given:
        RemittancePayee a = Fixture.from(RemittancePayee.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }
}
