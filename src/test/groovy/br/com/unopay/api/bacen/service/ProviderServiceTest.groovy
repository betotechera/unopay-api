package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Provider
import br.com.unopay.bootcommons.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired

class ProviderServiceTest extends SpockApplicationTests {

    @Autowired
    ProviderService service

    def 'a valid provider should be created'(){
        given:
        Provider provider = Fixture.from(Provider.class).gimme("valid")

        when:
        Provider created = service.create(provider)

        then:
        created != null
    }

    def 'a valid provider should be updated'(){
        given:
        Provider provider = Fixture.from(Provider.class).gimme("valid")
        Provider created = service.create(provider)
        def newField = "teste"
        provider.setName(newField)

        when:
        service.update(created.id, provider)
        Provider result = service.findById(created.id)

        then:
        result.name == newField
    }

    def 'a known provider should be found'(){
        given:
        Provider provider = Fixture.from(Provider.class).gimme("valid")
        Provider created = service.create(provider)

        when:
        Provider result = service.findById(created.id)

        then:
        result != null
    }

    def 'a known event should be found'(){
        given:
        Provider provider = Fixture.from(Provider.class).gimme("valid")
        Provider created = service.create(provider)

        when:
        Provider result = service.findById(created.id)

        then:
        result != null
    }

    def 'a unknown event should not be found'(){
        when:
        service.findById('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'PROVIDER_NOT_FOUND'
    }

    def 'a known event should be deleted'(){
        given:
        Provider provider = Fixture.from(Provider.class).gimme("valid")
        Provider created = service.create(provider)

        when:
        service.delete(created.id)
        service.findById(created.id)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'PROVIDER_NOT_FOUND'
    }

    def 'a unknown event should not be deleted'(){
        when:
        service.delete('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'PROVIDER_NOT_FOUND'
    }
}
