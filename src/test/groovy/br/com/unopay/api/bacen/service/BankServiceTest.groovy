package br.com.unopay.api.bacen.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.beans.factory.annotation.Autowired

class BankServiceTest extends SpockApplicationTests {

    @Autowired
    BankService service

    def 'should return when find'(){
        when:
        def page = new UnovationPageRequest(page: 1, size: 10)
        def result = service.findAll(page)

        then:
        !result.content.isEmpty()
        result.totalElements > 0
    }
}
