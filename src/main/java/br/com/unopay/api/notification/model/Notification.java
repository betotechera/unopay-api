package br.com.unopay.api.notification.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Notification {

    public Notification(){}

    private Email email;
    private String content;
    private EventType eventType;
    private Map<String, Object> payload;

}