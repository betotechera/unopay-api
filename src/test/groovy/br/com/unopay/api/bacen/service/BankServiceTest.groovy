package br.com.unopay.api.bacen.service

import br.com.unopay.api.SpockApplicationTests
import org.springframework.beans.factory.annotation.Autowired

class BankServiceTest extends SpockApplicationTests {

    @Autowired
    BankService service

    def 'should return when find'(){
        when:
        def result = service.findAll()

        then:
        !result.isEmpty()
        result.size() > 0
    }
}
