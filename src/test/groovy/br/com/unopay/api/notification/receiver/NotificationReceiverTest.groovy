package br.com.unopay.api.notification.receiver

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.notification.model.Notification
import br.com.unopay.api.notification.service.UnopayMailSender
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired

class NotificationReceiverTest extends SpockApplicationTests {

    private NotificationReceiver notificationReceiver
    @Autowired
    private ObjectMapper objectMapper
    private UnopayMailSender unopayMailSender = Mock(UnopayMailSender)

    @Override
    void setup() {
        notificationReceiver = new NotificationReceiver(objectMapper, unopayMailSender)
    }

    def 'notifyCustomer'() {
        given:
        def notification = Fixture.from(Notification).gimme("valid")
        when:
        notificationReceiver.notifyCustomer(toJson(notification))
        then:
        1 * unopayMailSender.send(_)
    }

    def 'notifyCustomer without email should not process'(){
        given:
        Notification notification = Fixture.from(Notification).gimme("valid")
        notification.email = null
        when:
        notificationReceiver.notifyCustomer(toJson(notification))
        then:
        0 * unopayMailSender.send(_)
    }
}
