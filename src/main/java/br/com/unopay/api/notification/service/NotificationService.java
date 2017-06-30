package br.com.unopay.api.notification.service;

import br.com.unopay.api.config.Queues;
import br.com.unopay.api.infra.Notifier;
import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.notification.model.Email;
import br.com.unopay.api.notification.model.EventType;
import static br.com.unopay.api.notification.model.EventType.CREATE_PASSWORD;
import br.com.unopay.api.notification.model.Notification;
import br.com.unopay.api.uaa.infra.PasswordTokenService;
import br.com.unopay.api.uaa.model.UserDetail;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Data
@ConfigurationProperties("unopay.resetPassword")
public class NotificationService {

    private String url;

    private Notifier notifier;

    private PasswordTokenService passwordTokenService;

    @Autowired
    public NotificationService(PasswordTokenService passwordTokenService,
                               Notifier notifier) {
        this.passwordTokenService = passwordTokenService;
        this.notifier = notifier;
    }

    public void sendNewPassword(UserDetail user, EventType eventType) {
        user.setPassword(null);
        Email email = new Email(user.getEmail());
        String token = passwordTokenService.createToken(user);
        Map<String, Object> payload = buildPayload(user, token);
        Notification notification = new Notification(email, null, eventType, payload);
        notifier.notify(Queues.UNOPAY_NOTIFICAITON, notification);
        log.info("reset password message sent to the queue for {}", user);
    }

    public void sendBatchClosingMail(String emailAsText, BatchClosing batchClosing){
        Email email = new Email(emailAsText);
        Map<String,Object> payload = new HashMap<String, Object>() {{ put("batch", batchClosing); }};
        Notification notification = new Notification(email, null, EventType.BATCH_CLOSED, payload);
        notifier.notify(Queues.UNOPAY_NOTIFICAITON, notification);
    }

    public void sendNewPassword(UserDetail user) {
        sendNewPassword(user, CREATE_PASSWORD);
    }

    private Map<String, Object> buildPayload(UserDetail user, String token) {
        Map<String,Object> payload = new HashMap<>();
        payload.put("user",user);
        payload.put("link",url);
        payload.put("token",token);
        return payload;
    }
}