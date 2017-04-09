package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Service
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired

class ServiceServiceTest extends SpockApplicationTests {

    @Autowired
    ServiceService service

    def 'a valid provider should be created'(){
        given:
        Service provider = Fixture.from(Service.class).gimme("valid")

        when:
        Service created = service.create(provider)

        then:
        created != null
    }

    def 'a valid provider should be updated'(){
        given:
        Service provider = Fixture.from(Service.class).gimme("valid")
        Service created = service.create(provider)
        def newField = "teste"
        provider.setName(newField)

        when:
        service.update(created.id, provider)
        Service result = service.findById(created.id)

        then:
        result.name == newField
    }

    def 'a known provider should be found'(){
        given:
        Service provider = Fixture.from(Service.class).gimme("valid")
        Service created = service.create(provider)

        when:
        Service result = service.findById(created.id)

        then:
        result != null
    }

    def 'a unknown event should not be found'(){
        when:
        service.findById('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'SERVICE_NOT_FOUND'
    }

    def 'a known service should be deleted'(){
        given:
        Service provider = Fixture.from(Service.class).gimme("valid")
        Service created = service.create(provider)

        when:
        service.delete(created.id)
        service.findById(created.id)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'SERVICE_NOT_FOUND'
    }

    def 'given a service with val tax only should be created'(){
        given:
        Service provider = Fixture.from(Service.class).gimme("valid")
        provider.setTaxVal(100.0)
        provider.setTaxPercent(null)

        when:
        Service created = service.create(provider)
        def result = service.findById(created.id)

        then:
        result.taxVal == 100.0
        result.taxPercent == null
    }

    def 'given a service with percent tax only should be created'(){
        given:
        Service provider = Fixture.from(Service.class).gimme("valid")
        provider.setTaxVal(null)
        provider.setTaxPercent(1.0)

        when:
        Service created = service.create(provider)
        def result = service.findById(created.id)

        then:
        result.taxVal == null
        result.taxPercent == 1.0d
    }

    def 'given a service without tax should be created'(){
        given:
        Service provider = Fixture.from(Service.class).gimme("valid")
        provider.setTaxVal(null)
        provider.setTaxPercent(null)

        when:
        service.create(provider)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'LEAST_ONE_TAX_REQUIRED'
    }

    def 'a known service with event should not be deleted'(){
        when:
        service.delete('2')

        then:
        def ex = thrown(ConflictException)
        ex.errors.find().logref == 'SERVICE_WITH_EVENTS'
    }
    def 'a unknown service should not be deleted'(){
        when:
        service.delete('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'SERVICE_NOT_FOUND'
    }
}
