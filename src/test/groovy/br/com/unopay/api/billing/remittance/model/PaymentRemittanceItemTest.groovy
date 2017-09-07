package br.com.unopay.api.billing.remittance.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.model.BatchClosing

class PaymentRemittanceItemTest extends FixtureApplicationTest{

    def 'give a establishment with same bank of issuer should be created with credit transfer option'(){
        given:
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).gimme("valid")
        batchClosing.establishment.bankAccount.bank = batchClosing.issuer.paymentAccount.bankAccount.bank

        when:
        def paymentRemittance = new PaymentRemittanceItem(batchClosing)

        then:
        paymentRemittance.transferOption == PaymentTransferOption.CURRENT_ACCOUNT_CREDIT
    }


    def 'give a establishment without same bank of issuer should be created with doc/ted transfer option'(){
        given:
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).gimme("valid")
        batchClosing.establishment.bankAccount.bank.bacenCode = 8888

        when:
        def paymentRemittance = new PaymentRemittanceItem(batchClosing)

        then:
        paymentRemittance.transferOption == PaymentTransferOption.DOC_TED
    }

    def 'should create from batch closed'(){
        given:
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).gimme("valid")

        when:
        def paymentRemittanceItem = new PaymentRemittanceItem(batchClosing)

        then:
        paymentRemittanceItem.payee.documentNumber == batchClosing.establishment.documentNumber()
        paymentRemittanceItem.payee.bankCode == batchClosing.establishment.bankAccount.bacenCode
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
