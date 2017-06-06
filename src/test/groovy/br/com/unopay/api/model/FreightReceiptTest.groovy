package br.com.unopay.api.model

import br.com.unopay.api.FixtureApplicationTest

class FreightReceiptTest  extends FixtureApplicationTest {

    def 'should be equals'(){
        given:
        FreightReceipt a = new FreightReceipt()

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        List list = Arrays.asList(new FreightReceipt(), new FreightReceipt())

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }
}
