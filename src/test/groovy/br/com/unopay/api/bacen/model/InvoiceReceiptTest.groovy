package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests

class InvoiceReceiptTest extends SpockApplicationTests {

    def 'should be equals'(){
        given:
        InvoiceReceipt a = Fixture.from(InvoiceReceipt.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        InvoiceReceipt a = Fixture.from(InvoiceReceipt.class).gimme("valid")
        InvoiceReceipt b = Fixture.from(InvoiceReceipt.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }
}
