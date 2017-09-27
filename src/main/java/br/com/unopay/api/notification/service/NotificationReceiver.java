package br.com.unopay.api.notification.service;

import br.com.unopay.api.config.Queues;
import br.com.unopay.api.notification.model.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@Slf4j
class NotificationReceiver {

    private ObjectMapper objectMapper;

    private UnopayMailSender unopayMailSender;

    @Autowired
    public NotificationReceiver(ObjectMapper objectMapper, UnopayMailSender unopayMailSender) {
        this.objectMapper = objectMapper;
        this.unopayMailSender = unopayMailSender;
    }

    @RabbitListener(queues = Queues.NOTIFICATION, containerFactory = Queues.DURABLE_CONTAINER)
    void notifyCustomer(String notificationAsString) {
        Notification notification = getAsNotification(notificationAsString);
        log.info("notification received to event={}", notification.getEventType());
        if(notification.getEmail() != null) {
            sendMailNotification(notification);
        }
    }

    private void sendMailNotification(Notification notification) {
        unopayMailSender.send(notification);
    }

    Notification getAsNotification(String notificationAsString) {
        try {
            return objectMapper.readValue(notificationAsString, Notification.class);
        } catch (IOException e) {
            log.error("unable to parse Notification", e);
            return null;
        }
    }
}
