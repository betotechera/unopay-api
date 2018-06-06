package br.com.unopay.api.notification.service;

import br.com.unopay.api.billing.boleto.model.Ticket;
import br.com.unopay.api.billing.remittance.model.PaymentRemittance;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.infra.Notifier;
import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.model.Billable;
import br.com.unopay.api.notification.model.Email;
import br.com.unopay.api.notification.model.EventType;
import br.com.unopay.api.notification.model.Notification;
import br.com.unopay.api.uaa.infra.PasswordTokenService;
import br.com.unopay.api.uaa.model.RequestOrigin;
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
@ConfigurationProperties(prefix = "unopay.notification")
public class NotificationService {

    private Map<String, String> resetPassword = new HashMap<>();

    private Map<String, Email> clientFrom = new HashMap<>();

    private Notifier notifier;

    private PasswordTokenService passwordTokenService;

    @Autowired
    public NotificationService(PasswordTokenService passwordTokenService,
                               Notifier notifier) {
        this.passwordTokenService = passwordTokenService;
        this.notifier = notifier;
    }

    public void sendNewPassword(UserDetail user, EventType eventType, String requestOrigin) {
        user.setPassword(null);
        Email email = getEmail(user.getEmail(), requestOrigin);
        String token = passwordTokenService.createToken(user);

        Map<String,Object> payload = new HashMap<>();
        payload.put("user",user);
        payload.put("link",linkForOrigin(requestOrigin));
        payload.put("token",token);
        payload.put("requestOrigin", requestOrigin);

        sendNotificationToQueue(payload, eventType, email);
        log.info("reset password message sent to the queue for {}", user);
    }

    private String linkForOrigin(String requestOrigin) {
        String link = resetPassword.get(requestOrigin);
        return link == null ? RequestOrigin.BACKOFFICE.name() : link;
    }

    public void sendNewPassword(UserDetail user, String requestOrigin) {
        sendNewPassword(user, CREATE_PASSWORD, requestOrigin);
    }

    public void sendBatchClosedMail(String emailAsText, BatchClosing batchClosing){
        HashMap<String, Object> payload = new HashMap<String, Object>() {{ put("batch", batchClosing);  }};
        sendEmailToQueue(emailAsText, payload, EventType.BATCH_CLOSED);
    }

    public void sendRemittanceCreatedMail(String emailAsText, PaymentRemittance remittance){
        Map<String,Object> payload = new HashMap<String, Object>() {{ put("remittance", remittance); }};
        sendEmailToQueue(emailAsText, payload, EventType.REMITTANCE_CREATED);
    }

    public void sendPaymentEmail(Billable order, EventType eventType){
        Map<String,Object> payload = new HashMap<String, Object>() {{ put("order", order); }};
        sendEmailToQueue(order, payload, eventType);
    }

    public void sendTicketIssued(Billable billable, Ticket ticket) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("billable", billable);
        payload.put("ticket", ticket);
        sendEmailToQueue(billable, payload, EventType.BOLETO_ISSUED);
    }

    private void sendEmailToQueue(String emailAsText, final Map<String,Object>  payload, EventType eventType) {
        Email email = new Email(emailAsText);
        sendNotificationToQueue(payload, eventType, email);
    }

    private void sendEmailToQueue(Billable billable, final Map<String,Object>  payload, EventType eventType) {
        Email email = getEmail(billable.getBillingMail(), billable.getIssuer().documentNumber());
        sendNotificationToQueue(payload, eventType, email);
    }

    private void sendNotificationToQueue(Map<String, Object> payload, EventType eventType, Email email) {
        Notification notification = new Notification(email, null, eventType, payload);
        notifier.notify(Queues.NOTIFICATION, notification);
    }

    private Email getEmail(String email, String requestOrigin) {
        Email emailWithFrom = clientFrom.get(requestOrigin);
        if(emailWithFrom != null){
            emailWithFrom.setTo(email);
            return emailWithFrom;
        }
        return new Email(email);
    }

    public Map<String, Email> getClientFrom(){
        return this.clientFrom;
    }

    public Map<String, String> getResetPassword(){
        return this.resetPassword;
    }
}