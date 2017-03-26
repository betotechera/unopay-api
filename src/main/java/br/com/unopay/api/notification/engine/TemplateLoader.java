package br.com.unopay.api.notification.engine;

import org.springframework.stereotype.Component;

@Component
public class TemplateLoader {

    public String getTemplate(String template){
        return "<h3> Ola {{user.name}} <br> utilize a senha: {{token}} para gerar sua nova senha clicando nesse link: {{link}}</h3>";
    }
}
