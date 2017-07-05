package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import static br.com.unopay.api.function.FixtureFunctions.instant

class BatchClosingTest extends FixtureApplicationTest {

    def 'should create from service authorize'(){
        given:
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid")

        when:
        def batchClosing = new BatchClosing(serviceAuthorize,0)

        then:
        batchClosing.accreditedNetwork == serviceAuthorize.contract.product.accreditedNetwork
        batchClosing.establishment == serviceAuthorize.establishment
        batchClosing.hirer == serviceAuthorize.contract.hirer
        batchClosing.issuer == serviceAuthorize.contract.product.issuer
        batchClosing.period == serviceAuthorize.establishment.checkout.period
        batchClosing.issueInvoice == serviceAuthorize.contract.issueInvoice
        batchClosing.closingDateTime < instant("1 second from now")
        batchClosing.closingDateTime > instant("1 second ago")
        batchClosing.number
    }

    def 'should create from service authorize with right payment release date'(){
        given:
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid")

        when:
        def batchClosing = new BatchClosing(serviceAuthorize,0)

        then:
        def closingPaymentDays = serviceAuthorize.establishment.checkout.closingPaymentDays
        batchClosing.paymentReleaseDateTime >= instant("${closingPaymentDays} day from now")
    }

    def 'should create from service authorize with right finished situation'(){
        given:
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid", new Rule(){{
            add("contract.issueInvoice", false)
        }})

        when:
        def batchClosing = new BatchClosing(serviceAuthorize,0)

        then:
        batchClosing.defineSituation().situation == BatchClosingSituation.FINALIZED
    }

    def 'should create from service authorize with right document received situation'(){
        given:
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid", new Rule(){{
            add("contract.issueInvoice", true)
        }})

        when:
        def batchClosing = new BatchClosing(serviceAuthorize,0)

        then:
        batchClosing.defineSituation().situation == BatchClosingSituation.DOCUMENT_RECEIVED
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
        BatchClosing a = Fixture.from(BatchClosing.class).gimme("valid", new Rule(){{
            add("number", "AAAAAAA")
        }})
        BatchClosing b = Fixture.from(BatchClosing.class).gimme("valid", new Rule(){{
            add("number", "BBBBBB")
        }})

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }
}
