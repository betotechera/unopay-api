package br.com.unopay.api.market.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest

class HirerNegotiationTest extends FixtureApplicationTest {


    def 'given Hirer Negotiation should set hirerDocumentNumber'() {

        given:
        String hirerDocumentNumber = value
        HirerNegotiation hirerNegotiation = Fixture.from(HirerNegotiation.class).gimme("valid", new Rule(){{
            add("hirer.person.document.number", hirerDocumentNumber)
        }})

        when:
        hirerNegotiation.setHirerDocumentNumber()

        then:
        hirerNegotiation.hirerDocumentNumber == hirerNegotiation.hirer.person.document.number

        where:
        _ | value
        _ | ""
        _ | "45214521459"

    }

    def 'given Hirer Negotiation should set issuerDocumentNumber'() {

        given:
        String issuerDocumentNumber = value
        HirerNegotiation hirerNegotiation = Fixture.from(HirerNegotiation.class).gimme("valid", new Rule(){{
            add("product.issuer.person.document.number", issuerDocumentNumber)
        }})

        when:
        hirerNegotiation.setIssuerDocumentNumber()

        then:
        hirerNegotiation.issuerDocumentNumber == hirerNegotiation.product.issuer.person.document.number

        where:
        _ | value
        _ | ""
        _ | "85963256978"

    }

    def 'given Hirer Negotiation setMeUp should set hirerDocumentNumber'() {

        given:
        HirerNegotiation hirerNegotiation = Fixture.from(HirerNegotiation.class).gimme("valid")

        when:
        hirerNegotiation.setMeUp()

        then:
        hirerNegotiation.hirerDocumentNumber == hirerNegotiation.hirer.person.document.number

    }

    def 'given Hirer Negotiation setMeUp should set issuerDocumentNumber'() {

        given:
        HirerNegotiation hirerNegotiation = Fixture.from(HirerNegotiation.class).gimme("valid")

        when:
        hirerNegotiation.setMeUp()

        then:
        hirerNegotiation.issuerDocumentNumber == hirerNegotiation.product.issuer.person.document.number

    }
}
