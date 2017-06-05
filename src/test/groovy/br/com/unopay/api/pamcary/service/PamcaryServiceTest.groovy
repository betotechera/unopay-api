package br.com.unopay.api.pamcary.service

import br.com.unopay.api.SpockApplicationTests
import org.springframework.beans.factory.annotation.Autowired

class PamcaryServiceTest extends SpockApplicationTests{

    @Autowired
    PamcaryService service

    def 'soap integration test'(){
        when:
        service.execute()
        then:
        true
    }
}
