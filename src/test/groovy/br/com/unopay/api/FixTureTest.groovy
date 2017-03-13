package br.com.unopay.api

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.uaa.model.UserDetail
import ch.qos.logback.core.net.server.Client

class FixTureTest  extends SpockApplicationTests{

    void 'when load known template should be found'(){
        when:
        UserDetail userDetail = Fixture.from(UserDetail.class).gimme("without-group")

        then:
        userDetail != null
    }

    void 'when load unknown template should not be found'(){
        when:
        Fixture.from(Client.class).gimme("valid")

        then:
        thrown(IllegalArgumentException)
    }
}
