package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.Subsidiary
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired

class EstablishmentServiceTest  extends SpockApplicationTests {

    @Autowired
    EstablishmentService service

    @Autowired
    AccreditedNetworkService networkService

    @Autowired
    SubsidiaryService subsidiaryService

    def 'a valid establishment should be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())

        when:
        Establishment created = service.create(establishment)

        then:
        created != null
    }

    def 'a valid establishment should be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        Establishment created = service.create(establishment)

        def newField = "teste"
        establishment.technicalContact = newField

        when:
        service.update(created.id, establishment)
        Establishment result = service.findById(created.id)

        then:
        result.technicalContact == newField
    }

    def 'a valid establishment with unknown network should not be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        establishment.network.id = ''

        when:
        service.create(establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ACCREDITED_NETWORK_NOT_FOUND'
    }

    def 'a valid establishment without network id should not be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        establishment.network.id = null
        when:
        service.create(establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'ACCREDITED_NETWORK_ID_REQUIRED'
    }

    def 'a valid establishment with unknown brand flag should not be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        establishment.brandFlag.id = ''
        when:
        service.create(establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'BRAND_FLAG_NOT_FOUND'
    }

    def 'a valid establishment without brand flag id should not be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        establishment.brandFlag.id = null
        when:
        service.create(establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'BRAND_FLAG_ID_REQUIRED'
    }

    def 'a valid establishment with unknown operational contact should not be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        establishment.operationalContact.id = ''
        when:
        service.create(establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTACT_NOT_FOUND'
    }

    def 'a valid establishment without operational contact id should be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        establishment.operationalContact.id = null

        when:
        def created = service.create(establishment)
        def result = service.findById(created.id)

        then:
        result != null
    }

    def 'a valid establishment with unknown administrative contact should not be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        establishment.administrativeContact.id = ''
        when:
        service.create(establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTACT_NOT_FOUND'
    }

    def 'a valid establishment without administrative contact id should be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        establishment.administrativeContact.id = null

        when:
        def created = service.create(establishment)
        def result = service.findById(created.id)

        then:
        result != null
    }

    def 'a valid establishment with unknown financier contact should not be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        establishment.financierContact.id = ''
        when:
        service.create(establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTACT_NOT_FOUND'
    }

    def 'a valid establishment without financier contact id should be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        establishment.financierContact.id = null

        when:
        def created = service.create(establishment)
        def result = service.findById(created.id)

        then:
        result != null
    }

    def 'a valid establishment without brand flag should not be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        establishment.brandFlag = null
        when:
        service.create(establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'BRAND_FLAG_REQUIRED'
    }

    def 'a valid establishment without network should not be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        establishment.network = null
        when:
        service.create(establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'ACCREDITED_NETWORK_REQUIRED'
    }

    def 'a valid establishment with unknown network should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        def created = service.create(establishment)
        establishment.network.id = ''
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ACCREDITED_NETWORK_NOT_FOUND'
    }

    def 'a valid establishment without network id should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        def created = service.create(establishment)
        establishment.network.id = null
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'ACCREDITED_NETWORK_ID_REQUIRED'
    }

    def 'a valid establishment without network should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        def created = service.create(establishment)
        establishment.network = null
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'ACCREDITED_NETWORK_REQUIRED'
    }


    def 'a valid establishment with unknown brand flag should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        def created = service.create(establishment)
        establishment.brandFlag.id = ''
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'BRAND_FLAG_NOT_FOUND'
    }

    def 'a valid establishment without brand flag id should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        def created = service.create(establishment)
        establishment.brandFlag.id = null
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'BRAND_FLAG_ID_REQUIRED'
    }

    def 'a valid establishment without brand flag should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        def created = service.create(establishment)
        establishment.brandFlag = null
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'BRAND_FLAG_REQUIRED'
    }


    def 'a valid establishment with unknown operational contact should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        def created = service.create(establishment)
        establishment.operationalContact.id = ''
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTACT_NOT_FOUND'
    }

    def 'a valid establishment without operational contact id should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        def created = service.create(establishment)
        establishment.operationalContact.id = null
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'CONTACT_ID_REQUIRED'
    }

    def 'a valid establishment with unknown administrative contact should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        def created = service.create(establishment)
        establishment.administrativeContact.id = ''
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTACT_NOT_FOUND'
    }

    def 'a valid establishment without administrative contact id should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        def created = service.create(establishment)
        establishment.administrativeContact.id = null
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'CONTACT_ID_REQUIRED'
    }

    def 'a valid establishment with unknown financier contact should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        def created = service.create(establishment)
        establishment.financierContact.id = ''
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTACT_NOT_FOUND'
    }

    def 'a valid establishment without financier contact id should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        def created = service.create(establishment)
        establishment.financierContact.id = null
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'CONTACT_ID_REQUIRED'
    }

    def 'a known establishment should be found'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        Establishment created = service.create(establishment)

        when:
        Establishment result = service.findById(created.id)

        then:
        result != null
    }

    def 'a unknown establishment should not be found'(){
        when:
        service.findById('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ESTABLISHMENT_NOT_FOUND'
    }

    def 'a known establishment should be deleted'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        Establishment created = service.create(establishment)

        when:
        service.delete(created.id)
        service.findById(created.id)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ESTABLISHMENT_NOT_FOUND'
    }

    def 'a known establishment with subsidiary should not be deleted'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid")
        networkService.create(establishment.getNetwork())
        Establishment created = service.create(establishment)
        subsidiary.matrix = created
        subsidiaryService.create(subsidiary)

        when:
        service.delete(created.id)

        then:
        def ex = thrown(ConflictException)
        ex.errors.find().logref == 'ESTABLISHMENT_WITH_SUBSIDIARY'
    }
    def 'a unknown establishment should not be deleted'(){
        when:
        service.delete('')
        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ESTABLISHMENT_NOT_FOUND'
    }
}
