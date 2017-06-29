package br.com.unopay.api.pamcary.receiver;

import br.com.unopay.api.config.Queues;
import br.com.unopay.api.model.FreightReceipt;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.pamcary.service.PamcaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class PamcaryReceiver {

    private ObjectMapper objectMapper;
    private PamcaryService pamcaryService;

    @Autowired
    public PamcaryReceiver(ObjectMapper objectMapper, PamcaryService pamcaryService) {
        this.objectMapper = objectMapper;
        this.pamcaryService = pamcaryService;
    }

    @RabbitListener(queues = Queues.PAMCARY_TRAVEL_DOCUMENTS)
    void freightReceiptNotify(String objectAsString) {
        FreightReceipt freightReceipt = getAsObject(objectAsString, FreightReceipt.class);
        if(freightReceipt != null && freightReceipt.getEstablishment() != null) {
            String documentNumber = freightReceipt.getEstablishment().documentNumber();
            log.info("confirmDocDelivery received to establishment={}", documentNumber);
            pamcaryService.confirmDocDelivery(documentNumber, freightReceipt.getCargoContract());
            pamcaryService.updateDoc(documentNumber, freightReceipt.getCargoContract());
            pamcaryService.generateVoucherDelivery(documentNumber,freightReceipt.getCargoContract());
            return;
        }
        log.error("invalid freightReceipt received when try confirmDocDelivery");
    }

    @RabbitListener(queues = Queues.PAMCARY_AUTHORIZATION_SUPPLY)
    void authorizationSupplyNotify(String objectAsString) {
        ServiceAuthorize serviceAuthorize = getAsObject(objectAsString, ServiceAuthorize.class);
        if(serviceAuthorize != null && serviceAuthorize.getEstablishment() != null) {
            String documentNumber = serviceAuthorize.getEstablishment().documentNumber();
            log.info("supplyConfirm received to establishment={}", documentNumber);
            pamcaryService.supplyConfirm(documentNumber, serviceAuthorize);
            return;
        }
        log.error("invalid serviceAuthorize received when try supplyConfirm");
    }

    <T> T getAsObject(String notificationAsString, Class<T> klass) {
        try {
            return objectMapper.readValue(notificationAsString,klass);
        } catch (IOException e) {
            log.error("unable to parse class={}",klass.getSimpleName(), e);
            return null;
        }
    }
}
