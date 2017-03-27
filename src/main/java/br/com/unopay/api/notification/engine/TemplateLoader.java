package br.com.unopay.api.notification.engine;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class TemplateLoader {

    private String PASSWORD_RESET;
    private String CREATE_PASSWORD;

    @SneakyThrows
    @Autowired
    public TemplateLoader(ResourceLoader resourceLoader){
        Resource createPassword = resourceLoader.getResource("classpath:/password-reset.html");
        Resource passwordReset = resourceLoader.getResource("classpath:/create-password.html");
        PASSWORD_RESET =  IOUtils.toString(passwordReset.getInputStream());
        CREATE_PASSWORD =  IOUtils.toString(createPassword.getInputStream());

    }

    public String getTemplate(String template){
        if("PASSWORD_RESET".equals(template))
            return PASSWORD_RESET;
        if("CREATE_PASSWORD".equals(template))
            return CREATE_PASSWORD;
        return "<h3> Ola {{user.name}} <br> utilize a senha: {{token}} para gerar sua nova senha clicando nesse link: </h3>";
    }
}
