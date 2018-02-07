package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.HirerNegotiation
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
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

    def 'given a negotiation with past effective date should not be created'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
            add("effectiveDate", instant("one second ago"))
        }})

        when:
        service.create(negotiation)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'EFFECTIVE_DATE_IS_BEFORE_CREATION'
    }

    def 'given a negotiation without effective date should not be created'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
            add("effectiveDate", null)
        }})

        when:
        service.create(negotiation)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'EFFECTIVE_DATE_REQUIRED'
    }

    def 'given a negotiation without past effective date should not be created'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
            add("effectiveDate", instant("one second ago"))
        }})

        when:
        service.create(negotiation)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'EFFECTIVE_DATE_IS_BEFORE_CREATION'
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


    def 'given a negotiation without product should not be created'(){
        given:
        def hirer = fixtureCreator.createHirer()
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", null)
        }})

        when:
        service.create(negotiation)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'PRODUCT_NOT_FOUND'
    }

    def 'given a negotiation without hirer should not be created'(){
        given:
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).gimme("valid", new Rule(){{
            add("hirer", null)
            add("product", product)
        }})

        when:
        service.create(negotiation)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'HIRER_NOT_FOUND'
    }


    def 'given a negotiation without installments should be created with product installments'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
            add("installments", null)
        }})

        when:
        HirerNegotiation created = service.create(negotiation)
        HirerNegotiation found = service.findById(created.id)

        then:
        found.installments == product.paymentInstallments
    }

    def 'given a negotiation without installment value should be created with product installment value'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
            add("installmentValue", null)
        }})

        when:
        HirerNegotiation created = service.create(negotiation)
        HirerNegotiation found = service.findById(created.id)

        then:
        found.installmentValue == product.installmentValue
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
