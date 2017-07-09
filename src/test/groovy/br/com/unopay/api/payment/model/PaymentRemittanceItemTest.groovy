package br.com.unopay.api.payment.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.model.BatchClosing

class PaymentRemittanceItemTest extends FixtureApplicationTest{

    def 'should create from batch closed'(){
        given:
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).gimme("valid")

        when:
        def paymentRemittanceItem = new PaymentRemittanceItem(batchClosing)

        then:
        paymentRemittanceItem.establishment == batchClosing.establishment
        paymentRemittanceItem.establishmentBankCode == batchClosing.establishment.bankAccount.bacenCode
    }

    def 'should create from batch closed with processing situation'(){
        given:
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).gimme("valid")

        when:
        def paymentRemittanceItem = new PaymentRemittanceItem(batchClosing)

        then:
        paymentRemittanceItem.situation == RemittanceSituation.PROCESSING
    }

    def 'should not be equals'(){
        List list = Fixture.from(PaymentRemittanceItem.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }

    def 'if the credit has a credit'(){
        given:
        PaymentRemittanceItem a = Fixture.from(PaymentRemittanceItem.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }
}
