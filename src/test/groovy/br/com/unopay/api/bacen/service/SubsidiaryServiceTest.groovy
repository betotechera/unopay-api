package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.Subsidiary
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired

class SubsidiaryServiceTest  extends SpockApplicationTests {

    @Autowired
    SubsidiaryService service

    @Autowired
    EstablishmentService establishmentService

    @Autowired
    AccreditedNetworkService accreditedNetworkService

    Establishment matrixUnderTest

    void setup(){
        matrixUnderTest = createMatrix()
    }



    def 'a valid subsidiary should be created'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }

        when:
        Subsidiary created = service.create(subsidiary)

        then:
        created != null
    }


    def 'a valid subsidiary without bank account should not be updated'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }
        def created = service.create(subsidiary)
        subsidiary.bankAccount = null
        when:
        service.update(created.id, subsidiary)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'BANK_ACCOUNT_REQUIRED'
    }

    def 'a valid subsidiary without bank account id should not be updated'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }
        def created = service.create(subsidiary)
        subsidiary.bankAccount.id = null

        when:
        service.update(created.id, subsidiary)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'BANK_ACCOUNT_ID_REQUIRED'
    }

    def 'a valid subsidiary with unknown bank account id should not be updated'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }
        def created = service.create(subsidiary)
        subsidiary.bankAccount.id = ''

        when:
        service.update(created.id, subsidiary)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'BANK_ACCOUNT_NOT_FOUND'
    }

    def 'a valid subsidiary without bank account should not be created'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }
        subsidiary.bankAccount = null
        when:
        service.create(subsidiary)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'BANK_ACCOUNT_REQUIRED'
    }

    def 'a valid subsidiary without bank account id should be created'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }
        subsidiary.bankAccount.id = null
        when:
        def created = service.create(subsidiary)
        def result = service.findById(created.id)
        then:
        result != null
    }

    def 'a valid subsidiary should be updated'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }
        Subsidiary created = service.create(subsidiary)
        def newField = "teste"
        subsidiary.technicalContact = newField

        when:
        service.update(created.id, subsidiary)
        Subsidiary result = service.findById(created.id)

        then:
        result.technicalContact == newField
    }

    def 'a valid subsidiary without person should not be updated'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }
        def created = service.create(subsidiary)
        subsidiary.person = null
        when:
        service.update(created.id, subsidiary)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'PERSON_REQUIRED'
    }

    def 'a valid subsidiary without person id should not be updated'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }
        def created = service.create(subsidiary)
        subsidiary.person.id = null

        when:
        service.update(created.id, subsidiary)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'PERSON_ID_REQUIRED'
    }

    def 'a valid subsidiary with unknown person id should not be updated'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }
        def created = service.create(subsidiary)
        subsidiary.person.id = ''

        when:
        service.update(created.id, subsidiary)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'PERSON_NOT_FOUND'
    }

    def 'a valid subsidiary without person should not be created'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }
        subsidiary.person = null
        when:
        service.create(subsidiary)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'PERSON_REQUIRED'
    }

    def 'a valid subsidiary without matrix should not be updated'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }
        def created = service.create(subsidiary)
        subsidiary.matrix = null

        when:
        service.update(created.id, subsidiary)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'MATRIX_REQUIRED'
    }

    def 'a valid subsidiary without matrix id should not be updated'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }
        def created = service.create(subsidiary)
        subsidiary.matrix.id = null

        when:
        service.update(created.id, subsidiary)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'CANNOT_CHANGE_MATRIX'
    }

    def 'a subsidiary with changed matrix should not be updated'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }
        def newMatrix = createMatrix()
        def created = service.create(subsidiary)
        subsidiary.matrix = newMatrix

        when:
        service.update(created.id, subsidiary)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'CANNOT_CHANGE_MATRIX'
    }

    def 'a valid subsidiary without matrix should not be created'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }
        subsidiary.matrix = null
        when:
        service.create(subsidiary)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'MATRIX_REQUIRED'
    }

    def 'a known subsidiary should be found'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }
        Subsidiary created = service.create(subsidiary)

        when:
        Subsidiary result = service.findById(created.id)

        then:
        result != null
    }

    def 'a unknown subsidiary should not be found'(){
        when:
        service.findById('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'SUBSIDIARY_NOT_FOUND'
    }

    def 'a known subsidiary should be deleted'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }
        Subsidiary created = service.create(subsidiary)

        when:
        service.delete(created.id)
        service.findById(created.id)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'SUBSIDIARY_NOT_FOUND'
    }

    def 'a unknown subsidiary should not be deleted'(){
        when:
        service.delete('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'SUBSIDIARY_NOT_FOUND'
    }

    private Establishment createMatrix() {
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        accreditedNetworkService.create(establishment.network)
        establishmentService.create(establishment)
    }
}
