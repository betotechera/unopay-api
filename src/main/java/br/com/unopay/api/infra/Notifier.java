package br.com.unopay.api.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Notifier {

    private RabbitMessagingTemplate messagingTemplate;

    private ObjectMapper objectMapper;

    @Autowired
    public Notifier(RabbitMessagingTemplate messagingTemplate,
                    ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    public void notify(String queue, Object notification) {
        String payLoadAsString = getNotificationAsString(notification);
        messagingTemplate.convertAndSend(queue,queue,payLoadAsString);
    }

    private String getNotificationAsString(Object notification) {
        try {
            return objectMapper.writeValueAsString(notification);
        } catch (JsonProcessingException e) {
            log.warn("could not convert notification to string.", e);
            return  null;
        }
    }
}
