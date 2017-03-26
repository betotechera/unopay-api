package br.com.unopay.api.notification.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.notification.model.Email
import br.com.unopay.api.notification.model.EventType
import org.springframework.beans.factory.annotation.Autowired

import javax.mail.Message

class SimpleMailMessageFactoryTest extends SpockApplicationTests{


    @Autowired
    MimeMessageFactory factory

    def to = 'ze@unovation.com.br'
    def subject = 'criaçao de senha'
    def unopayFrom = 'no-reply@unovation.com.br'
    def eventType = EventType.CREATE_PASSWORD

    def content = """
                <html>
                    <h1>Olá {{user.name}}</h1>
                    <h1>email: {{user.email}}</h1>
                </html>
            """

    def "given a valid email object should create valid mail message"(){
        given:
        def mail = new Email(to: to, subject: subject, from: unopayFrom)

        when:
        def createdMailMessage = factory.create(mail, content, eventType)

        then:
        createdMailMessage.getRecipients(Message.RecipientType.TO).find().toString() == to

    }

    def "given a email object with invalid email to should return error"(){
        given:
        def mail = new Email(to: 'joao@adsfcom', subject: subject, from: unopayFrom)

        when:
        factory.create(mail, content, eventType)

        then:
        thrown IllegalArgumentException
    }

    def "given a email object without email to should return error"(){
        given:
        def mail = new Email(subject: subject, from: unopayFrom)

        when:
        factory.create(mail, content, eventType)

        then:
        thrown IllegalArgumentException
    }

    def "given a email object with empty email to should return error"(){
        given:
        def mail = new Email(to: '',subject: subject, from: unopayFrom)

        when:
        factory.create(mail, content, eventType)

        then:
        thrown IllegalArgumentException
    }

    def "given a email object with empty content to should return error"(){
        given:
        def mail = new Email(to: to, subject: subject, from: unopayFrom)

        when:
        factory.create(mail, '', eventType)

        then:
        thrown IllegalArgumentException
    }

    def "given a email object without content to should return error"(){
        given:
        def mail = new Email(to: to, subject: subject, from: unopayFrom)

        when:
        factory.create(mail, null, eventType)

        then:
        thrown IllegalArgumentException
    }

    def "given a email object with empty subject to should return error"(){
        given:
        def mail = new Email(to: to, subject: '', from: unopayFrom)

        when:
        factory.create(mail, content, eventType)

        then:
        thrown IllegalArgumentException
    }

    def "given a email object without subject to should return error"(){
        given:
        def mail = new Email(to: to, subject: null, from: unopayFrom)

        when:
        factory.create(mail, content, eventType)

        then:
        thrown IllegalArgumentException
    }

    def "given a email object should create mail message with unopay from mail"(){
        given:
        def mail = new Email(to: to, subject: subject, from: unopayFrom)

        when:
        def createdMailMessage = factory.create(mail, content, eventType)

        then:
        createdMailMessage.from.first().toString() == unopayFrom
    }


}
