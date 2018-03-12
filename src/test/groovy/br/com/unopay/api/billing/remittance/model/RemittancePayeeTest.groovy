package br.com.unopay.api.billing.remittance.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.Hirer

class RemittancePayeeTest  extends FixtureApplicationTest{

    def 'should create from establishment'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        def paymentAccountBank = 241
        BigDecimal receivable = 500

        when:
        def payee = new RemittancePayee(establishment, paymentAccountBank, receivable)

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
        payee.bankCode == establishment.bankAccount.bacenCode()
        payee.payerBankCode == paymentAccountBank
        payee.name == establishment.person.shortName
        payee.receivable == receivable
    }

    def 'should create from hirer'(){
        given:
        Hirer hirer = Fixture.from(Hirer.class).gimme("valid")
        def paymentAccountBank = 241
        BigDecimal receivable = 500

        when:
        def payee = new RemittancePayee(hirer, paymentAccountBank, receivable)

        then:
        payee
        payee.agency == hirer.bankAccount.agency
        payee.agencyDigit == hirer.bankAccount.agencyDigit
        payee.accountNumber == hirer.bankAccount.accountNumber
        payee.accountNumberDigit == hirer.bankAccount.accountNumberDigit
        payee.streetName == hirer.person.address.streetName
        payee.number == hirer.person.address.number
        payee.complement == hirer.person.address.complement
        payee.zipCode == hirer.person.address.zipCode
        payee.district == hirer.person.address.district
        payee.city == hirer.person.address.city
        payee.state == hirer.person.address.state
        payee.bankCode == hirer.bankAccount.bacenCode()
        payee.payerBankCode == paymentAccountBank
        payee.name == hirer.person.shortName
        payee.receivable == receivable
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
