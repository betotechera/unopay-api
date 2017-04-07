package br.com.unopay.api.notification.engine

import br.com.unopay.api.SpockApplicationTests
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

class MailValidatorTest extends SpockApplicationTests{

    @Autowired
    MailValidator validator

    @Unroll
    'given #email should be valid'(){
        when:
        boolean isValid = validator.isValid(email)

        then:
        isValid

        where:
        _ | email
        _ | 'teste@teste.com'
        _ | 'file-name-@uol.com.br'
        _ | '&xem_plo@terra.net'
        _ | 'tes_te@gmail.com'
        _ | 'asdf--2@net.me'
        _ | 'test__@ai.jpg'
        _ | 'test@ali.uol'

    }

    @Unroll
    'given #email should not be valid'(){
        when:
        boolean isValid = validator.isValid(email)

        then:
        !isValid

        where:
        _ | email
        _ | 'teste@teste.'
        _ | 'file.com.br'
        _ | '&xemplóó@gmail.com'
        _ | 'téste d@uol.com'
        _ | 'xemm@'
        _ | 'xxxx@..net'
        _ | 'test.@.com.br'
    }
}
