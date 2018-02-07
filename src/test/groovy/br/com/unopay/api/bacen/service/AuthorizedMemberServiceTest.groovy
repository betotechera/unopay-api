package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.AuthorizedMember
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired

class AuthorizedMemberServiceTest extends SpockApplicationTests {
    @Autowired
    AuthorizedMemberService service

    void 'given valid AuthorizedMember should create'(){
        given:
        AuthorizedMember authorizedMember = Fixture.from(AuthorizedMember).gimme("valid")
        when:
        def result = service.create(authorizedMember)
        then:
        result
    }

    void 'given AuthorizedMember without birthDate should return error'(){
        given:
        AuthorizedMember authorizedMember = Fixture.from(AuthorizedMember).gimme("valid")
        authorizedMember.birthDate = null
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_BIRTH_DATE_REQUIRED'
    }

    void 'given AuthorizedMember without gender should return error'(){
        given:
        AuthorizedMember authorizedMember = Fixture.from(AuthorizedMember).gimme("valid")
        authorizedMember.gender = null
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_GENDER_REQUIRED'
    }

    void 'given AuthorizedMember without relatedness should return error'(){
        given:
        AuthorizedMember authorizedMember = Fixture.from(AuthorizedMember).gimme("valid")
        authorizedMember.relatedness = null
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_RELATEDNESS_REQUIRED'
    }
}
