package br.com.unopay.api.notification.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.notification.engine.TemplateProcessor
import br.com.unopay.api.notification.model.Email
import br.com.unopay.api.notification.model.Notification
import br.com.unopay.api.notification.repository.NotificationRepository
import br.com.unopay.api.uaa.model.UserDetail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender

class UnopayMailSenderTest extends SpockApplicationTests{


    @Autowired
    UnopayMailSender service

    def mailSender = Mock(JavaMailSender)
    def templateProcessor = Mock(TemplateProcessor)
    def repository = Mock(NotificationRepository)
    def content = """
                <html>
                    <h1>Olá {{user.name}}</h1>
                    <h1>email: {{user.email}}</h1>
                </html>
            """

    def setup(){
        service.mailSender = mailSender
        service.templateProcessor = templateProcessor
        service.repository = repository
        templateProcessor.renderHtml(_) >> content
    }

    def "given a notification with valid email to should send message"(){
        given:
        Notification notification = Fixture.from(Notification.class).gimme("valid")
        notification.with { payload = [user: new UserDetail(name:'ze', email: 'ze@teste.com')]}
        when:
        service.send(notification)

        then:
        1 * mailSender.send(_)
    }

    def "given a notification without email to should not send message"(){
        given:
        Notification notification = Fixture.from(Notification.class).gimme("valid").with { email = null; it }
        when:
            service.send(notification)

        then:
            0 * mailSender.send(_)
    }

    def "given a notification with invalid email to should not send message"(){
        given:
        Email invalidEmail = Fixture.from(Email.class).gimme("invalid-email")
        Notification notification = Fixture.from(Notification.class).gimme("valid").with { email = invalidEmail; it }
        when:
        service.send(notification)

        then:
        0 * mailSender.send(_)
    }

    def "when try render invalid template should not send message"(){
        given:
        Notification notification = Fixture.from(Notification.class).gimme("valid").with { payload = null; it }
        when:
        service.send(notification)

        then:
        1 * templateProcessor.renderHtml(_) >> { throw  new IllegalArgumentException() }
        0 * mailSender.send(_)
        notThrown  IllegalArgumentException
    }


}
