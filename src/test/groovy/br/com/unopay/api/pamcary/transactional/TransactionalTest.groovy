package br.com.unopay.api.pamcary.transactional

import br.com.unopay.api.SpockApplicationTests
import org.springframework.beans.factory.annotation.Autowired

class TransactionalTest extends SpockApplicationTests{

    @Autowired
    WSTransacional_Service service


    def 'soap'(){
        System.setProperty("javax.net.ssl.keyStore", "/usr/lib/jvm/java-8-oracle/jre/lib/security/cacerts")
        System.setProperty("javax.net.ssl.keyStorePassword", "Ro@dcard")
        System.setProperty("javax.net.ssl.trustStore", "/usr/lib/jvm/java-8-oracle/jre/lib/security/cacerts")
        System.setProperty("javax.net.ssl.trustStorePassword", "Ro@dcard")
        when:
        print("teste")
        then:
        true
    }
}
