package br.com.unopay.api.notification.model;


import lombok.Data;

import java.util.Map;

@Data
public class Notification {

    private Email email;
    private String content;
    private EventType eventType;
    private Map<String, Object> payload;
}
