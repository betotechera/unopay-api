package br.com.unopay.api.market.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import static br.com.unopay.api.function.FixtureFunctions.instant
import br.com.unopay.api.market.model.HirerNegotiation
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.apache.commons.beanutils.BeanUtils
import org.joda.time.DateTimeComparator
import org.joda.time.DateTimeFieldType
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

    def 'when create negotiation should be created with created date time'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
            add("createdDateTime", null)
        }})

        when:
        HirerNegotiation created = service.create(negotiation)
        HirerNegotiation found = service.findById(created.id)

        then:
        timeComparator.compare(found.createdDateTime, new Date()) == 0
    }

    def 'when create negotiation should be created inactive'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
            add("active", Boolean.TRUE)
        }})

        when:
        HirerNegotiation created = service.create(negotiation)
        HirerNegotiation found = service.findById(created.id)

        then:
        !found.active
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

    def 'when try create more one negotiation for same product should return error'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
        }})
        service.create(negotiation)

        when:
        service.create(negotiation)

        then:
        def ex = thrown(ConflictException)
        ex.errors.find().logref == 'NEGOTIATION_FOR_PRODUCT_AND_HIRER_EXISTING'
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
        HirerNegotiation negotiation = fixtureCreator.createNegotiation(hirer, product, instant("one day from now"))

        when:
        service.update(negotiation.id, negotiation.with { installments = 40; it })
        HirerNegotiation found = service.findById(negotiation.id)

        then:
        found.installments == negotiation.installments
    }

    def 'given a known hirer negotiation should be active'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = fixtureCreator.createNegotiation(hirer, product, instant("one day from now"))
        negotiation.active = Boolean.FALSE
        service.save(negotiation)

        when:
        service.defineActive(negotiation.getId())
        HirerNegotiation found = service.findById(negotiation.id)

        then:
        found.active
    }

    def 'should not update product and hirer'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = fixtureCreator.createNegotiation(hirer, product, instant("one day from now"))
        def newProduct = fixtureCreator.createProduct()
        def newHirer = fixtureCreator.createHirer()
        HirerNegotiation cloned = BeanUtils.cloneBean(negotiation)
        when:
        service.update(negotiation.id, cloned.with { product = newProduct; hirer = newHirer; it })
        HirerNegotiation found = service.findById(negotiation.id)

        then:
        found.hirer.id == negotiation.hirer.id
        found.product.id == negotiation.product.id
    }

    def 'given negotiation with past effect date should not be updated'(){
        given:
        def negotiation = fixtureCreator.createNegotiation()

        when:
        service.update(negotiation.id, negotiation.with {
            installments = 40; effectiveDate = instant("one second ago")
            it
        })

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'EFFECTIVE_DATE_IS_BEFORE_CREATION'
    }

    def 'given negotiation without effect date should not change effective date'(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        HirerNegotiation cloned = BeanUtils.cloneBean(negotiation)

        when:
        service.update(negotiation.id, cloned.with { effectiveDate = null; it })
        HirerNegotiation found = service.findById(negotiation.id)
        then:
        DateTimeComparator timeComparator = DateTimeComparator.getInstance(DateTimeFieldType.dayOfMonth())
        timeComparator.compare(found.effectiveDate, negotiation.effectiveDate) == 0
    }

    def 'when create negotiation should be created with hirerDocumentNumber'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
        }})

        when:
        HirerNegotiation created = service.create(negotiation)
        HirerNegotiation found = service.findById(created.id)

        then:
        found.hirerDocumentNumber == hirer.getDocumentNumber()
    }

    def 'when create negotiation should be created with issuerDocumentNumber'(){
        given:
        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProduct()
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).gimme("valid", new Rule(){{
            add("hirer", hirer)
            add("product", product)
        }})

        when:
        HirerNegotiation created = service.create(negotiation)
        HirerNegotiation found = service.findById(created.id)

        then:
        found.issuerDocumentNumber == product.issuer.documentNumber()
    }

}
