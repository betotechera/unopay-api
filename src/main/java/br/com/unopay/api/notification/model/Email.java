package br.com.unopay.api.notification.model;

import lombok.Data;

@Data
public class Email {

    private String to;
    private String subject;
    private String from;
    private String personalFrom;
}
