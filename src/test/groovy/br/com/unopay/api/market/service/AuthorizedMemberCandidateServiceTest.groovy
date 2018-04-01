package br.com.unopay.api.market.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.market.model.AuthorizedMemberCandidate
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired

class AuthorizedMemberCandidateServiceTest extends SpockApplicationTests {

    @Autowired
    private AuthorizedMemberCandidateService service
    @Autowired
    private FixtureCreator fixtureCreator

    void 'given valid AuthorizedMember should create'(){
        given:
        AuthorizedMemberCandidate authorizedMember = fixtureCreator.createAuthorizedMemberCandidateToPersist()

        when:
        def result = service.create(authorizedMember)

        then:
        result
    }

    void 'given AuthorizedMember without birthDate should return error'(){
        given:
        AuthorizedMemberCandidate authorizedMember = fixtureCreator.createAuthorizedMemberCandidateToPersist()
        authorizedMember.birthDate = null

        when:
        service.create(authorizedMember)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_BIRTH_DATE_REQUIRED'
    }

    void 'given AuthorizedMember with birthDate before minimum date should return error'(){
        given:
        AuthorizedMemberCandidate authorizedMember = fixtureCreator.createAuthorizedMemberCandidateToPersist()
        authorizedMember.birthDate = new DateTime().minusYears(151).toDate()

        when:
        service.create(authorizedMember)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'INVALID_AUTHORIZED_MEMBER_BIRTH_DATE'
    }

    void 'given AuthorizedMember with birthDate after today should return error'(){
        given:
        AuthorizedMemberCandidate authorizedMember = fixtureCreator.createAuthorizedMemberCandidateToPersist()
        authorizedMember.birthDate = new DateTime().plusDays(1).toDate()
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'INVALID_AUTHORIZED_MEMBER_BIRTH_DATE'
    }

    void "given AuthorizedMember with unknown order should return error"(){
        given:
        AuthorizedMemberCandidate authorizedMember = fixtureCreator.createAuthorizedMemberCandidateToPersist()
        authorizedMember.order.id = "123"

        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'ORDER_NOT_FOUND'
    }

    void 'given AuthorizedMember without gender should return error'(){
        given:
        AuthorizedMemberCandidate authorizedMember = fixtureCreator.createAuthorizedMemberCandidateToPersist()
        authorizedMember.gender = null

        when:
        service.create(authorizedMember)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_GENDER_REQUIRED'
    }

    void 'given AuthorizedMember without relatedness should return error'(){
        given:
        AuthorizedMemberCandidate authorizedMember = fixtureCreator.createAuthorizedMemberCandidateToPersist()
        authorizedMember.relatedness = null

        when:
        service.create(authorizedMember)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_RELATEDNESS_REQUIRED'
    }


    void 'should find known AuthorizedMember'(){
        given:
        AuthorizedMemberCandidate authorizedMember = fixtureCreator.createAuthorizedMemberCandidateForOrder()

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
        AuthorizedMemberCandidate authorizedMember = fixtureCreator.createAuthorizedMemberCandidateForOrder()
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
        def id = authorizedMember.id
        when:
        service.delete(id)
        service.findById(id)
        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_NOT_FOUND'
    }

}
