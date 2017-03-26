package br.com.unopay.api.notification.service;

import br.com.unopay.api.config.Queues;
import br.com.unopay.api.notification.model.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RabbitMessagingTemplate messagingTemplate;


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