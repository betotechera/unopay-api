package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class BatchClosingTest extends FixtureApplicationTest {

    def 'should create from service authorize'(){
        given:
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid")

        when:
        def batchClosing = new BatchClosing(serviceAuthorize)

        then:
        batchClosing.accreditedNetwork == serviceAuthorize.contract.product.accreditedNetwork
        batchClosing.establishment == serviceAuthorize.establishment
        batchClosing.hirer == serviceAuthorize.contract.hirer
        batchClosing.issuer == serviceAuthorize.contract.product.issuer
        batchClosing.period == serviceAuthorize.establishment.checkout.period
        batchClosing.issueInvoice == serviceAuthorize.contract.issueInvoice
        batchClosing.closingDateTime != null
    }

    def 'should be equals'(){
        given:
        BatchClosing a = Fixture.from(BatchClosing.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        BatchClosing a = Fixture.from(BatchClosing.class).gimme("valid")
        BatchClosing b = Fixture.from(BatchClosing.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }
}
