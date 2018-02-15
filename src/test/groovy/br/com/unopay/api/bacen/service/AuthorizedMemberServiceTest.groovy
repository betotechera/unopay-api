package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.AuthorizedMember
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import br.com.unopay.bootcommons.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired

class AuthorizedMemberServiceTest extends SpockApplicationTests {
    @Autowired
    AuthorizedMemberService service

    @Autowired
    FixtureCreator fixtureCreator

    void 'given valid AuthorizedMember should create'(){
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        when:
        def result = service.create(authorizedMember)
        then:
        result
    }

    void 'given AuthorizedMember without birthDate should return error'(){
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        authorizedMember.birthDate = null
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_BIRTH_DATE_REQUIRED'
    }

    void "given AuthorizedMember with paymentInstrument that doesn't belong to it's contractor should return error"(){
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        authorizedMember.paymentInstrument = fixtureCreator.createInstrumentToProduct()
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'INSTRUMENT_NOT_BELONGS_TO_CONTRACTOR'
    }

    void 'given AuthorizedMember without contract should return error'(){
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        authorizedMember.contract = null
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'CONTRACT_REQUIRED'
    }

    void 'given AuthorizedMember without gender should return error'(){
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        authorizedMember.gender = null
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_GENDER_REQUIRED'
    }

    void 'given AuthorizedMember without relatedness should return error'(){
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        authorizedMember.relatedness = null
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_RELATEDNESS_REQUIRED'
    }

    void 'given AuthorizedMember without paymentInstrument should return error'(){
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        authorizedMember.paymentInstrument = null
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'PAYMENT_INSTRUMENT_REQUIRED'
    }

    void 'should find known AuthorizedMember'(){
        given:
        AuthorizedMember authorizedMember =  fixtureCreator.createPersistedAuthorizedMember()
        when:
        def found = service.findById(authorizedMember.id)
        then:
        found
    }

    void 'when trying to find unknown AuthorizedMember should return error'(){
        given:
        def id = "123"
        when:
        service.findById(id)
        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_NOT_FOUND'
    }

    void 'should update known AuthorizedMember'() {
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        authorizedMember.name = "new name"
        when:
        service.update(authorizedMember.id, authorizedMember)

        then:
        def found = service.findById(authorizedMember.id)
        found.name == authorizedMember.name
    }

    void 'when trying to delete unknown AuthorizedMember should return error'(){
        given:
        def id = "123"
        when:
        service.delete(id)
        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_NOT_FOUND'
    }

    void 'should delete known AuthorizedMember'(){
        given:
        def authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        def id = authorizedMember.id;
        when:
        service.delete(id)
        service.findById(id)
        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_NOT_FOUND'
    }
}
