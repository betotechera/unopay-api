package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class InvoiceReceiptTest extends FixtureApplicationTest {

    def 'should be equals'(){
        given:
        InvoiceReceipt a = Fixture.from(InvoiceReceipt.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        List list = Fixture.from(InvoiceReceipt.class).gimme(2, "valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals

    }
}
