package br.com.unopay.api.notification.repository;

import br.com.unopay.api.notification.model.EventType;
import br.com.unopay.api.notification.model.Notification;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;

@Repository
public class NotificationRepository {

    private RedisTemplate template;

    @Autowired
    public NotificationRepository(RedisTemplate template) {
        this.template = template;
    }

    public void record(Notification notification, String content) {
        String key = getKey(notification, content);
        opsForValue().set(key, getValue());
    }

    public Object getDateWhenSent(Notification notification, String content) {
        return  opsForValue().get(getKey(notification, content));
    }

    private String hashContent(String content) {
        return DigestUtils.sha256Hex(content);
    }

    private String getValue() {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
        return format.format(new Date());
    }

    private String formatEventTypeString(EventType eventType) {
        return eventType.toString().toLowerCase();
    }

    String getKey(Notification notification, String content) {
        return String.format("%s:%s:%s", formatEventTypeString(notification.getEventType()),notification.getEmail().getTo(),hashContent(content) );
    }

    private ValueOperations opsForValue() {
        return template.opsForValue();
    }
}
