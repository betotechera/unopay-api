package br.com.unopay.api.notification.engine;

import br.com.unopay.api.notification.model.Notification;
import com.hubspot.jinjava.Jinjava;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TemplateProcessor {

    static HashMap<String, String> cache = new HashMap<>();

    @Autowired
    Jinjava jinjava;

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
        Validate.notNull(notification.getEventType());
        Validate.notNull(notification.getPayload());
    }

    private String getCachedTemplate(Notification notification) {
        if (!cache.containsKey(cacheKey(notification))) {
            try {
                String template = ""; //TODO getTemplate
                cache.put(notification.getEventType().toString(), template);
                cache.put(cacheKey(notification), template);
            } catch(Exception e){ //TODO IOEXECPTION
                log.error("template not found to event={}", notification.getEventType());
                throw new IllegalStateException("template not found to event");
            }
        }
        return cache.get(cacheKey(notification));
    }

    private String cacheKey(Notification notification){
        return notification.getEventType().toString();
    }


}
