package br.com.unopay.api.notification.engine;

import br.com.unopay.api.notification.model.EventType;
import java.io.IOException;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class TemplateLoader {

    private ResourceLoader resourceLoader;

    @SneakyThrows
    @Autowired
    public TemplateLoader(ResourceLoader resourceLoader){
        this.resourceLoader = resourceLoader;
    }

    private String getTemplate(ResourceLoader resourceLoader,String location) throws IOException {
        return IOUtils.toString(getTemplateResource(resourceLoader, location).getInputStream());
    }

    private Resource getTemplateResource(ResourceLoader resourceLoader, String location) {
        return resourceLoader.getResource(location);
    }

    @SneakyThrows
    public String getTemplate(EventType eventType){
        return getTemplate(resourceLoader,String.format("classpath:/%s.html", createDefaultName(eventType.name())));
    }

    private static String createDefaultName(String templateName) {
        return templateName.toLowerCase().replace("_", "-");
    }
}
