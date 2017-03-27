package br.com.unopay.api.notification.service;

import br.com.unopay.api.config.Queues;
import br.com.unopay.api.notification.model.Email;
import br.com.unopay.api.notification.model.EventType;
import br.com.unopay.api.notification.model.Notification;
import br.com.unopay.api.uaa.infra.PasswordTokenService;
import br.com.unopay.api.uaa.model.UserDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static br.com.unopay.api.notification.model.EventType.CREATE_PASSWORD;

@Service
@Slf4j
@Data
@Configuration("unopay.resetPassword")
public class NotificationService {
    private static final String url = "http://unopay.qa.unovation.com.br/#/password/";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitMessagingTemplate messagingTemplate;

    @Autowired
    private PasswordTokenService passwordTokenService;

    public void sendNewPassword(UserDetail user, EventType eventType) {
        user.setPassword(null);
        Email email = new Email(user.getEmail());
        String token = passwordTokenService.createToken(user);
        Map<String, Object> payload = buildPayload(user, token);
        Notification notification = new Notification(email, null, CREATE_PASSWORD, payload);
        notify(notification);
        log.info("reset password message sent to the queue for {}", user);
    }

    public void sendNewPassword(UserDetail user) {
        sendNewPassword(user, CREATE_PASSWORD);
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
            log.warn("could not convert notification to string.", e);
            return  null;
        }
    }

    private Map<String, Object> buildPayload(UserDetail user, String token) {
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("user", user);
        payloadMap.put("link", url);
        payloadMap.put("token", token);
        return payloadMap;
    }
}