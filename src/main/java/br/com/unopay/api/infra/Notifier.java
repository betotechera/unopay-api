package br.com.unopay.api.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
        log.info("Notifying the queue={}", queue);
        String payLoadAsString = getNotificationAsString(notification);
        messagingTemplate.convertAndSend(queue,queue,payLoadAsString);
    }

    private String getNotificationAsString(Object notification) {
        try {
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            return objectMapper.writeValueAsString(notification);
        } catch (JsonProcessingException e) {
            log.warn("could not convert notification to string.", e);
            return  null;
        }
    }
}
