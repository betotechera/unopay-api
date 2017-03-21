package br.com.unopay.api.uaa.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.uaa.model.UserType
import org.springframework.beans.factory.annotation.Autowired

class UserTypeServiceTest extends SpockApplicationTests {

    @Autowired
    UserTypeService userTypeService

    void 'when find user types should return all'() {

        when:
        Set<UserType> types = userTypeService.findAll()

        then:
        types.size() >= 2
    }
}

