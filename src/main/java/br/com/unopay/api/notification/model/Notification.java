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

    public boolean hasPersonalFrom(){
        return this.getEmail() != null &&
                this.getEmail().getFrom() != null &&
                this.getEmail().getPersonalFrom() != null;
    }

    public String getPersonalFrom() {
        if(this.email != null){
            return this.getPersonalFrom();
        }
        return null;
    }

    public String getFrom() {
        if(this.email != null) {
            return this.email.getFrom();
        }
        return null;
    }
}