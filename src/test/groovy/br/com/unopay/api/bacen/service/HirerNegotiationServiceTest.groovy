package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.HirerNegotiation
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.bootcommons.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired

class HirerNegotiationServiceTest extends SpockApplicationTests{

    @Autowired
    private HirerNegotiationService service
    @Autowired
    private FixtureCreator fixtureCreator

    def 'valid hirer negotiation should be created'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
        }})

        when:
        HirerNegotiation created = service.save(negotiation)
        HirerNegotiation found = service.findById(created.id)

        then:
        found
    }

    def 'given a negotiation with unknown hirer should not be created'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).gimme("valid", new Rule(){{
            add("hirer", hirer.with { id = ''; it })
            add("product", product)
        }})

        when:
        service.create(negotiation)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'HIRER_NOT_FOUND'
    }

    def 'given a negotiation with unknown product should not be created'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product.with { id = ''; it })
        }})

        when:
        service.create(negotiation)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'PRODUCT_NOT_FOUND'
    }

    def 'given a known hirer negotiation should be updated'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
        }})

        when:
        service.update(negotiation.id, negotiation.with { installments = 40; it })
        HirerNegotiation found = service.findById(negotiation.id)

        then:
        found.installments == negotiation.installments
    }


    def 'given a known hirer negotiation when update with unknown hirer should not be updated'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
        }})

        when:
        service.update(negotiation.id, negotiation.with { installments = 40; hirer.id = ''; it })

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'HIRER_NOT_FOUND'
    }

    def 'given a known hirer negotiation when update with unknown product should not be updated'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
        }})

        when:
        service.update(negotiation.id, negotiation.with { installments = 40; product.id = ''; it })

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'PRODUCT_NOT_FOUND'
    }
}
