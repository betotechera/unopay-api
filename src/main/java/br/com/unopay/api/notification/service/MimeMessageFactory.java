package br.com.unopay.api.notification.service;

import br.com.unopay.api.notification.engine.MailValidator;
import br.com.unopay.api.notification.model.Email;
import br.com.unopay.api.notification.model.EventType;
import java.util.EnumMap;
import javax.mail.internet.MimeMessage;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "unopay.notification")
public class MimeMessageFactory {

    private JavaMailSender mailSender;

    private MailValidator mailValidator;

    private EnumMap<EventType, String> subjectByEvent = new EnumMap<>(EventType.class);

    private Email defaultFrom;

    @Autowired
    public MimeMessageFactory(JavaMailSender mailSender, MailValidator mailValidator) {
        this.mailSender = mailSender;
        this.mailValidator = mailValidator;
    }

    @SneakyThrows
    public MimeMessage create(Email email, String content, EventType eventType) {
        validate(email, content, eventType);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
        mimeMessageHelper.setTo(email.getTo());
        mimeMessageHelper.setSubject(subjectByEvent.get(eventType));
        mimeMessageHelper.setFrom(defaultFrom.getFrom(), defaultFrom.getPersonalFrom());
        mimeMessageHelper.setText(content, true);

        if(!mailValidator.isValid(email.getTo())) {
            throw new IllegalArgumentException();
        }

        return message;
    }

    private void validate(Email email, String content, EventType eventType) {
        notEmpty(content, "content");
        notEmpty(eventType.toString(), "eventType");
        validateEmailTo(email);
    }

    private void validateEmailTo(Email email) {
        mailValidator.check(email.getTo());
    }

    private void notEmpty(String value, String fieldName) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(String.format("%s cannot be null",fieldName));
        }
    }

}
