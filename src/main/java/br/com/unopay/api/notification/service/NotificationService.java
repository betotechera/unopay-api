package br.com.unopay.api.notification.service;

import br.com.unopay.api.config.Queues;
import br.com.unopay.api.infra.Notifier;
import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.notification.model.Email;
import br.com.unopay.api.notification.model.EventType;
import br.com.unopay.api.notification.model.Notification;
import br.com.unopay.api.billing.remittance.model.PaymentRemittance;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.uaa.infra.PasswordTokenService;
import br.com.unopay.api.uaa.model.UserDetail;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.notification.model.EventType.CREATE_PASSWORD;

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
        notifier.notify(Queues.NOTIFICATION, notification);
        log.info("reset password message sent to the queue for {}", user);
    }

    public void sendNewPassword(UserDetail user) {
        sendNewPassword(user, CREATE_PASSWORD);
    }

    public void sendBatchClosedMail(String emailAsText, BatchClosing batchClosing){
        HashMap<String, Object> payload = new HashMap<String, Object>() {{ put("batch", batchClosing);  }};
        sendEmailToQueue(emailAsText, payload, EventType.BATCH_CLOSED);
    }

    public void sendRemittanceCreatedMail(String emailAsText, PaymentRemittance remittance){
        Map<String,Object> payload = new HashMap<String, Object>() {{ put("remittance", remittance); }};
        sendEmailToQueue(emailAsText, payload, EventType.REMITTANCE_CREATED);
    }

    public void sendPaymentEmail(Order order, EventType eventType){
        Map<String,Object> payload = new HashMap<String, Object>() {{ put("order", order); }};
        sendEmailToQueue(order.personEmail(), payload, eventType);
    }

    private Map<String, Object> buildPayload(UserDetail user, String token) {
        Map<String,Object> payload = new HashMap<>();
        payload.put("user",user);
        payload.put("link",url);
        payload.put("token",token);
        return payload;
    }

    private void sendEmailToQueue(String emailAsText, final Map<String,Object>  payload, EventType eventType) {
        Email email = new Email(emailAsText);
        Notification notification = new Notification(email, null, eventType, payload);
        notifier.notify(Queues.NOTIFICATION, notification);
    }
}