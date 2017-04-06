package br.com.unopay.api.bacen.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.beans.factory.annotation.Autowired

class BankServiceTest extends SpockApplicationTests {

    @Autowired
    BankService service

    def 'should return when find'(){
        when:
        def result = service.findAll("all")

        then:
        !result.isEmpty()
        result.size() > 0
    }
}
