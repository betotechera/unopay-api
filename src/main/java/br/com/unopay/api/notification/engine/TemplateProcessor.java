package br.com.unopay.api.notification.engine;

import br.com.unopay.api.notification.model.Notification;
import com.hubspot.jinjava.Jinjava;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@Data
public class TemplateProcessor {

    static HashMap<String, String> cache = new HashMap<>();

    private Jinjava jinjava;

    private TemplateLoader templateLoader;

    @Autowired
    public TemplateProcessor(Jinjava jinjava, TemplateLoader templateLoader) {
        this.jinjava = jinjava;
        this.templateLoader = templateLoader;
    }

    public String renderHtml(Notification notification) {
        log.info("getting html notification");
        validateNotification(notification);
        String  htmlTemplate = getCachedTemplate(notification);
        return process(htmlTemplate, notification.getPayload());
    }

    private String process(String htmlTemplate, Map<String, ?> parametersMap) {
        return jinjava.render(htmlTemplate, parametersMap);
    }

    private void validateNotification(Notification notification) {
        if(notification.getEventType() == null) {
            throw new IllegalArgumentException();
        }

        if(notification.getPayload() == null) {
            throw new IllegalArgumentException();
        }
    }

    private String getCachedTemplate(Notification notification) {
        if (!cache.containsKey(cacheKey(notification))) {
            String template = templateLoader.getTemplate(notification.getEventType());
            cache.put(cacheKey(notification), template);
        }
        return cache.get(cacheKey(notification));
    }

    private String cacheKey(Notification notification){
        return notification.getEventType().toString();
    }


}
