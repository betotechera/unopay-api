package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.bootcommons.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired

class IssuerServiceTest  extends SpockApplicationTests {

    @Autowired
    IssuerService service

    def 'a valid issuer should be created'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        when:
        Issuer created = service.create(issuer)
        Issuer result = service.findById(created.getId())

        then:
        result != null
    }

    def 'given a existing issuer should be updated'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        Issuer created = service.create(issuer)

        issuer.with { tax = 0.3 }
        when:
        service.update(created.id, issuer)
        Issuer result = service.findById(created.id)

        then:
        result != null
        result.tax == 0.3d
    }

    def 'given a unknown issuer when updated should not be found'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        issuer.with { tax = 0.3 }
        when:
        service.update('', issuer)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ISSUER_NOT_FOUND'
    }

    def 'given a unknown issuer when find should not be found'(){
        when:
        service.findById('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ISSUER_NOT_FOUND'
    }

}
