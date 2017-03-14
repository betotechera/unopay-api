package br.com.unopay.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate

class FlywayApplicationTest extends SpockApplicationTests {

    @Autowired
    private JdbcTemplate template


    def 'should return authorities'() {
        when:
        def result = this.template.queryForObject(
                "SELECT COUNT(*) from AUTHORITY", Integer.class)
        then:
        new Integer(2) == result
    }

    def 'should return user details'() {
        when:
        def result = this.template.queryForObject(
                "SELECT COUNT(*) from oauth_user_details", Integer.class)
        then:
        new Integer(4) == result
    }
}
