package br.com.unopay.api.notification.engine;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class TemplateLoader {

    private String passwordReset;
    private String createPassword;

    @SneakyThrows
    @Autowired
    public TemplateLoader(ResourceLoader resourceLoader){
        Resource passwordReset = resourceLoader.getResource("classpath:/password-reset.html");
        Resource createPassword  = resourceLoader.getResource("classpath:/create-password.html");
        this.passwordReset =  IOUtils.toString(passwordReset.getInputStream());
        this.createPassword =  IOUtils.toString(createPassword.getInputStream());

    }

    public String getTemplate(String template){
        if("passwordReset".equals(template)) {
            return passwordReset;
        }
        if("createPassword".equals(template)) {
            return createPassword;
        }
        return "<h3> Ola {{user.name}} <br> utilize a senha: {{token}} " +
                "para gerar sua nova senha clicando nesse link: </h3>";
    }
}
