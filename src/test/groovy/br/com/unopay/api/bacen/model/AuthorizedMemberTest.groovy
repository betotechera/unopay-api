package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.market.model.AuthorizedMember

class AuthorizedMemberTest extends FixtureApplicationTest{
    
    def 'should be equal'() {
        when:
        def authorizedMember = Fixture.from(AuthorizedMember).gimme('valid')
        then:
        authorizedMember == authorizedMember
    }

    def 'should not be equal'() {
        when:
        def a = Fixture.from(AuthorizedMember).gimme('valid')
        def b = Fixture.from(AuthorizedMember).gimme('valid')
        then:
        a != b
    }
}
