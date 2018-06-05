package br.com.unopay.api.notification.service;

import br.com.unopay.api.notification.engine.MailValidator;
import br.com.unopay.api.notification.engine.TemplateProcessor;
import br.com.unopay.api.notification.model.Notification;
import br.com.unopay.api.notification.repository.NotificationRepository;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class UnopayMailSender {


    JavaMailSender mailSender;

    private MimeMessageFactory messageFactory;

    NotificationRepository repository;

    private MailValidator mailValidator;

    TemplateProcessor templateProcessor;

    @Autowired
    public UnopayMailSender(JavaMailSender mailSender, MimeMessageFactory messageFactory,
                            NotificationRepository repository, MailValidator mailValidator,
                            TemplateProcessor templateProcessor) {
        this.mailSender = mailSender;
        this.messageFactory = messageFactory;
        this.repository = repository;
        this.mailValidator = mailValidator;
        this.templateProcessor = templateProcessor;
    }

    @SneakyThrows
    public void send(Notification notification) {
        if(valid(notification)) {
            try {
                String content = templateProcessor.renderHtml(notification);
                MimeMessage mailMessage = messageFactory
                                                .create(notification.getEmail(), content,notification.getEventType());
                Object dateSent = repository.getDateWhenSent(notification, content);
                if(dateSent != null) {
                    mailAlreadySent(notification, dateSent);
                    return;
                }
                if(notification.hasPersonalFrom()){
                    mailMessage.setFrom(new InternetAddress(notification.getFrom(), notification.getPersonalFrom()));
                }
                sendMail(mailMessage, notification, content);
            } catch (IllegalArgumentException e) {
                log.error("Error when try send mail of type={}, error message={}",
                                                                    notification.getEventType(), e.getMessage(), e);
            }
        }
    }

    private void mailAlreadySent(Notification notification, Object dateSent) {
        log.info("Mail already sent type={} to={} sentDate={}",
                                            notification.getEventType(), notification.getEmail().getTo(), dateSent);
    }

    private void sendMail(MimeMessage mailMessage, Notification notification, String content) {
        mailSender.send(mailMessage);
        repository.record(notification, content);
        log.info("Sending mail message type={} to={}",
                                            notification.getEventType().toString(), notification.getEmail().getTo());
    }

    private boolean valid(Notification notification){
        boolean valid = notification.getEmail() != null
                && notification.getEmail().getTo() != null
                && mailValidator.isValid(notification.getEmail().getTo());
        if(!valid){
            String mail = notification.getEmail() != null ? notification.getEmail().getTo() : "";
            log.warn("Invalid notification mail. Type={} mail={}", notification.getEventType(), mail);
        }
        return valid;
    }
}
