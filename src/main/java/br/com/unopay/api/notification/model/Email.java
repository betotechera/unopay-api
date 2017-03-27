package br.com.unopay.api.notification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Email {

    private String to;
    private String subject;
    private String from;
    private String personalFrom;

    public Email(String to) {
        this.to = to;
    }
}