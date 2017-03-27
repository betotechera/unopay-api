package br.com.unopay.api.notification.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    private Email email;
    private String content;
    private EventType eventType;
    private Map<String, Object> payload;

}