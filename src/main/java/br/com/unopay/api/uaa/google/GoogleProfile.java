package br.com.unopay.api.uaa.google;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class GoogleProfile{

    private String email;

    public GoogleProfile() {}

    public GoogleProfile(Map properties) {
        List emails = (List) properties.get("emails");

        for (Object email: emails) {
            LinkedHashMap obj = (LinkedHashMap) email;
            this.email = obj.get("value").toString();
            break;
        }
    }

    public GoogleProfile(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "GoogleProfile{ email='" + email + '}';
    }
}