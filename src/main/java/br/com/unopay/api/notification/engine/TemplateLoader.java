package br.com.unopay.api.notification.engine;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class TemplateLoader {

    private String USER_CREATED;
    private String CREATE_PASSWORD;


    ;

    @SneakyThrows
    @Autowired
    public TemplateLoader(ResourceLoader resourceLoader){
        Resource createPassword = resourceLoader.getResource("classpath:/password-reset.html");
        Resource userCreated = resourceLoader.getResource("classpath:/create-password.html");
        USER_CREATED =  IOUtils.toString(userCreated.getInputStream());
        CREATE_PASSWORD =  IOUtils.toString(createPassword.getInputStream());

    }

    public String getTemplate(String template){
        if("USER_CREATED".equals(template))
            return USER_CREATED;
        if("CREATE_PASSWORD".equals(template))
            return CREATE_PASSWORD;
        return "<h3> Ola {{user.name}} <br> utilize a senha: {{token}} para gerar sua nova senha clicando nesse link: </h3>";
    }
}
