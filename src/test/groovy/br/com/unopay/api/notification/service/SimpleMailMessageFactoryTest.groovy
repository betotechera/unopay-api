package br.com.unopay.api.notification.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.notification.model.Email
import br.com.unopay.api.notification.model.EventType
import br.com.unopay.bootcommons.exception.BadRequestException
import org.springframework.beans.factory.annotation.Autowired

import javax.mail.Message

class SimpleMailMessageFactoryTest extends SpockApplicationTests{


    @Autowired
    MimeMessageFactory factory

    def to = 'ze@unovation.com.br'
    def eventType = EventType.CREATE_PASSWORD

    def content = """
                <html>
                    <h1>Olá {{user.name}}</h1>
                    <h1>email: {{user.email}}</h1>
                </html>
            """

    def "given a email with only to should create mail with default information"(){
        given:
        def mail = new Email(to: to)

        when:
        def createdMailMessage = factory.create(mail, content, eventType)

        then:
        createdMailMessage.getRecipients(Message.RecipientType.TO).find().toString() == to
        createdMailMessage.getRecipients(Message.RecipientType.TO).find().toString() == to
        createdMailMessage.getFrom().find().toString() == 'Super Saude <no-reply@clinicasclivale.com.br>'
        createdMailMessage.getSubject() == 'Crie sua nova senha'

    }

    def "given a email object with invalid email to should return error"(){
        given:
        def mail = new Email(to: 'joao@adsfcom')

        when:
        factory.create(mail, content, eventType)

        then:
        thrown BadRequestException
    }

    def "given a email object without email to should return error"(){
        given:
        def mail = new Email()

        when:
        factory.create(mail, content, eventType)

        then:
        thrown BadRequestException
    }

    def "given a email object with empty email to should return error"(){
        given:
        def mail = new Email(to: '')

        when:
        factory.create(mail, content, eventType)

        then:
        thrown BadRequestException
    }

    def "given a email object with empty content to should return error"(){
        given:
        def mail = new Email(to: to)

        when:
        factory.create(mail, '', eventType)

        then:
        thrown IllegalArgumentException
    }

    def "given a email object without content to should return error"(){
        given:
        def mail = new Email(to: to)

        when:
        factory.create(mail, null, eventType)

        then:
        thrown IllegalArgumentException
    }

}
