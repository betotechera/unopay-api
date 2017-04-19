package br.com.unopay.api.notification.engine;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TemplateLoader {

    private String resetTemplate;
    private String newUserTemplate;

    @SneakyThrows
    @Autowired
    public TemplateLoader(ResourceLoader resourceLoader){
        this.resetTemplate = getTemplate(resourceLoader,"classpath:/password-reset.html");
        this.newUserTemplate =  getTemplate(resourceLoader,"classpath:/create-password.html");
    }

    private String getTemplate(ResourceLoader resourceLoader,String location) throws IOException {
        return IOUtils.toString(getTemplateResource(resourceLoader, location).getInputStream());
    }

    private Resource getTemplateResource(ResourceLoader resourceLoader, String location) {
        return resourceLoader.getResource(location);
    }

    public String getTemplate(String template){
        if("passwordReset".equals(template)) {
            return resetTemplate;
        }
        if("createPassword".equals(template)) {
            return newUserTemplate;
        }
        return "<h3> Ola {{user.name}} <br> utilize a senha: {{token}} " +
                "para gerar sua nova senha clicando nesse link: </h3>";
    }
}
