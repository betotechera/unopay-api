package br.com.unopay.api.wingoo.receiver;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.market.model.ContractorProduct;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.service.PaymentInstrumentService;
import br.com.unopay.api.util.GenericObjectMapper;
import br.com.unopay.api.wingoo.service.WingooService;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("!test")
@Component
public class WingooReceiver {

    private GenericObjectMapper genericObjectMapper;
    private WingooService service;
    private PaymentInstrumentService paymentInstrumentService;

    @Autowired
    public WingooReceiver(GenericObjectMapper genericObjectMapper,
                          WingooService ticketService,
                          PaymentInstrumentService paymentInstrumentService) {
        this.genericObjectMapper = genericObjectMapper;
        this.service = ticketService;
        this.paymentInstrumentService = paymentInstrumentService;
    }

    //@RabbitListener(queues = Queues.CONTRACTOR_CREATED, containerFactory = Queues.DURABLE_CONTAINER)
    public void wingooReceiptNotify(String objectAsString) {
        ContractorProduct contractor = genericObjectMapper.getAsObject(objectAsString, ContractorProduct.class);
        log.info("sending the contractor={} of the hirer={} to the Wingoo's system", contractor.getDocumentNumber(), contractor.getHirerDocument());
        service.create(contractor);
        log.info("the contractor={} was sent to the wingoo's system", contractor.getDocumentNumber());
    }

    private Optional<PaymentInstrument> getContractorInstrument(Contractor contractor) {
        return paymentInstrumentService
                .findDigitalWalletByContractorDocument(contractor.getDocumentNumber());
    }
}
