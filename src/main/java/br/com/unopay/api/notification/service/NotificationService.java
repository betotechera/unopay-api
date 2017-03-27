package br.com.unopay.api.notification.service;

import br.com.unopay.api.config.Queues;
import br.com.unopay.api.notification.model.Email;
import br.com.unopay.api.notification.model.EventType;
import br.com.unopay.api.notification.model.Notification;
import br.com.unopay.api.uaa.model.UserDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RabbitMessagingTemplate messagingTemplate;


    public void sendNewPassword(UserDetail user, String token) {
        user.setPassword(null);
        Email email = new Email(){{setTo(user.getEmail()); }};
        Map<String, Object> payload = new HashMap<String, Object>() {{ put("user", user); }};
        Notification notification = new Notification(){{setEmail(email); setEventType(EventType.CREATE_PASSWORD); setPayload(payload);}};
        notify(notification);
        log.info("reset password message sent to the queue for {}", user);
    }

    public void notify(Notification notification) {
        String payLoadAsString = getNotificationAsString(notification);
        messagingTemplate.convertAndSend(
                Queues.UNOPAY_NOTIFICAITON,
                Queues.UNOPAY_NOTIFICAITON,
                payLoadAsString
        );
    }

    private String getNotificationAsString(Notification notification) {
        try {
            return objectMapper.writeValueAsString(notification);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return  null;
        }
    }
}