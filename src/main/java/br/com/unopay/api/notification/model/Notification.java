package br.com.unopay.api.notification.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
public class Notification {

    public Notification(){}

    private Email email;
    private String content;
    private EventType eventType;
    private Map<String, Object> payload;

}