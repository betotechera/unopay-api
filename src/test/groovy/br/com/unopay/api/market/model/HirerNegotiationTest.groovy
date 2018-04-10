package br.com.unopay.api.market.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest

class HirerNegotiationTest extends FixtureApplicationTest {

    def 'given Hirer Negotiation with Hirer that has Document number should return true'() {

        given:
        HirerNegotiation hirerNegotiation = Fixture.from(HirerNegotiation.class).gimme("valid")

        when:
        boolean hasHirerDocumentNumber = hirerNegotiation.hasHirerDocumentNumber()

        then:
        hasHirerDocumentNumber

    }

    def 'given Hirer Negotiation with Hirer that has not Document number should return false'() {

        given:
        HirerNegotiation hirerNegotiation = Fixture.from(HirerNegotiation.class).gimme("valid", new Rule(){{
            add("hirer.person.document.number", "")
        }})

        when:
        boolean hasHirerDocumentNumber = hirerNegotiation.hasHirerDocumentNumber()

        then:
        !hasHirerDocumentNumber

    }

    def 'given Hirer Negotiation with Product that has Issuer with Document number should return true'() {

        given:
        HirerNegotiation hirerNegotiation = Fixture.from(HirerNegotiation.class).gimme("valid")

        when:
        boolean hasIssuerDocumentNumber = hirerNegotiation.hasIssuerDocumentNumber()

        then:
        hasIssuerDocumentNumber

    }

    def 'given Hirer Negotiation with Product that has Issuer without Document number should return false'() {

        given:
        HirerNegotiation hirerNegotiation = Fixture.from(HirerNegotiation.class).gimme("valid", new Rule(){{
            add("product.issuer.person.document.number", "")
        }})

        when:
        boolean hasIssuerDocumentNumber = hirerNegotiation.hasIssuerDocumentNumber()

        then:
        !hasIssuerDocumentNumber

    }
}
