package br.com.unopay.api.notification.service;

import br.com.unopay.api.notification.engine.TemplateProcessor;
import br.com.unopay.api.notification.model.Notification;
import br.com.unopay.api.notification.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.Date;


@Slf4j
@Component
public class UnopayMailSender {


    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MimeMessageFactory messageFactory;

    @Autowired
    private NotificationRepository repository;

    @Autowired
    TemplateProcessor templateProcessor;

    public void send(Notification notification) {
        if(valid(notification)) {
            try {
                String content = templateProcessor.renderHtml(notification);
                MimeMessage mailMessage = messageFactory.create(notification.getEmail(), content,notification.getEventType());
                Date dateSent = repository.getDateWhenSent(notification, content);
                if(dateSent != null) {
                    mailAlreadySent(notification, dateSent);
                    return;
                }
                sendMail(mailMessage, notification, content);
            } catch (Exception ex){
                log.error("Error when try send mail of type={}, error message={}", notification.getEventType(), ex.getMessage());
            }
        }
    }

    private void mailAlreadySent(Notification notification, Date dateSent) {
        log.info("Mail already sent type={} to={} sentDate={}", notification.getEventType(), notification.getEmail().getTo(), dateSent);
    }

    private void sendMail(MimeMessage mailMessage, Notification notification, String content) {
        mailSender.send(mailMessage);
        repository.record(notification, content);
        log.info("Sending mail message type={} to={}", notification.getEventType().toString(), notification.getEmail().getTo());
    }

    private boolean valid(Notification notification){
        boolean valid = notification.getEmail() != null && notification.getEmail().getTo() != null; //TODO validate email
        if(!valid){
            log.warn("Invalid notification mail. Type={}", notification.getEventType());
        }
        return valid;
    }
}
