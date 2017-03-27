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

    @Autowired
    private Jinjava jinjava;

    @Autowired
    private TemplateLoader templateLoader;

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
            try {
                String template = templateLoader.getTemplate(notification.getEventType().toString());
                cache.put(cacheKey(notification), template);
            } catch(Exception e) {
                log.error("template not found to event={}", notification.getEventType());
                throw new IllegalStateException("template not found for event" + notification.getEventType(), e);
            }
        }
        return cache.get(cacheKey(notification));
    }

    private String cacheKey(Notification notification){
        return notification.getEventType().toString();
    }


}
