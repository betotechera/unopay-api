package br.com.unopay.api.billing.remittance.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.bacen.model.Issuer

class PaymentRemittanceTest extends FixtureApplicationTest{

    def 'should create from batch closed'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        when:
        def paymentRemittance = new PaymentRemittance(issuer, 10)
        def payer = paymentRemittance.payer
        then:
        payer
        payer.bankAgreementNumberForCredit == issuer.paymentAccount.bankAgreementNumberForCredit
        payer.bankAgreementNumberForDebit == issuer.paymentAccount.bankAgreementNumberForDebit
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
        payer.documentNumber == issuer.documentNumber()
        payer.bankCode == issuer.getPaymentAccount().getBankAccount().bacenCode
        payer.bankName == issuer.getPaymentAccount().getBankAccount().bank.name
        paymentRemittance.number
    }

    def 'given a credit remittance should return return file uri with PG prefix'(){
        given:
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("valid", new Rule(){{
            add("operationType", PaymentOperationType.CREDIT)
        }})

        when:
        def fileUri = remittance.fileUri.split("/")[2]

        then:
        "PG" == fileUri.subSequence(0,2)
    }

    def 'given a debit remittance should return return file uri with DB prefix'(){
        given:
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("valid", new Rule(){{
            add("operationType", PaymentOperationType.DEBIT)
        }})

        when:
        def fileUri = remittance.fileUri.split("/")[2]

        then:
        "DB" == fileUri.subSequence(0,2)
    }

    def 'should return sum of all payments'(){
        given:
        List<PaymentRemittanceItem> items = Fixture.from(PaymentRemittanceItem.class).gimme(3,"valid")
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("valid", new Rule(){{
            add("remittanceItems", items)
        }})

        when:
        BigDecimal total = remittance.getTotal()

        then:
        total == remittance.remittanceItems.sum { it.value }
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
