package br.com.unopay.api.notification.service;


import br.com.unopay.api.notification.model.Email;
import br.com.unopay.api.notification.model.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Component
public class MimeMessageFactory {

    @Autowired
    private JavaMailSender mailSender;


    public MimeMessage create(Email email, String content, EventType eventType) throws MessagingException, UnsupportedEncodingException {
        validate(email, content, eventType);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8"){{
           setTo(email.getTo());
           setSubject(email.getSubject());
        }};
        mimeMessageHelper.setFrom(email.getFrom(), email.getPersonalFrom());
        mimeMessageHelper.setText(content, true);
        //TODO: email validate
        return message;
    }

    private void validate(Email email, String content, EventType eventType) {
        notEmpty(email.getSubject(), "subject");
        notEmpty(content, "content");
        notEmpty(eventType.toString(), "eventType");
        validateEmailTo(email);
    }

    private void validateEmailTo(Email email) {
        if (email.getTo() == null || email.getTo().isEmpty()) //TODO: validate emails
            throw new IllegalArgumentException("Invalid mail to");
    }

    private void notEmpty(String value, String fieldName) {
        if (value == null || value.isEmpty()) throw new IllegalArgumentException(String.format("%s cannot be null",fieldName));
    }

}
