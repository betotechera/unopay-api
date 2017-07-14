package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class BatchClosingItemTest extends FixtureApplicationTest {

    def 'should create from service authorize'(){
        given:
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid")

        when:
        def batchClosingItem = new BatchClosingItem(serviceAuthorize)

        then:
        batchClosingItem.serviceAuthorize == serviceAuthorize
        batchClosingItem.documentNumberInvoice == serviceAuthorize.contract.hirerDocumentNumber()
    }

    def 'should create from service authorize with pending invoice situation'(){
        given:
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid")

        when:
        def batchClosingItem = new BatchClosingItem(serviceAuthorize)

        then:
        batchClosingItem.invoiceDocumentSituation == DocumentSituation.PENDING
    }

    def 'should create from service authorize with establishment issue invoice type'(){
        given:
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid")

        when:
        def batchClosingItem = new BatchClosingItem(serviceAuthorize)

        then:
        batchClosingItem.issueInvoiceType == serviceAuthorize.establishmentIssueInvoiceType()
    }

    def 'should be equals'(){
        given:
        BatchClosingItem a = Fixture.from(BatchClosingItem.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        BatchClosingItem a = Fixture.from(BatchClosingItem.class).gimme("valid")
        BatchClosingItem b = Fixture.from(BatchClosingItem.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }
}
