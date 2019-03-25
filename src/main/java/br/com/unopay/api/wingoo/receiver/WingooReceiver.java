package br.com.unopay.api.wingoo.receiver;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.config.Queues;
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

    @Transactional
    @RabbitListener(queues = Queues.CONTRACTOR_CREATED, containerFactory = Queues.DURABLE_CONTAINER)
    public void wingooReceiptNotify(String objectAsString) {
        Contractor contractor = genericObjectMapper.getAsObject(objectAsString, Contractor.class);
        log.info("sending contractor={} to Wingoo system", contractor.getDocumentNumber());
        Optional<PaymentInstrument> paymentInstrument = getContractorInstrument(contractor);
        String instrumentNumber = paymentInstrument.map(PaymentInstrument::getNumber).orElse(null);
        String issuerDocument = paymentInstrument.map(PaymentInstrument::getProduct)
                                .map(Product::getIssuer).map(Issuer::documentNumber).orElse(null);
        service.create(contractor, instrumentNumber,issuerDocument);
        log.info("contractor={} sent to Wingoo system", contractor.getDocumentNumber());
    }

    private Optional<PaymentInstrument> getContractorInstrument(Contractor contractor) {
        return paymentInstrumentService
                .findDigitalWalletByContractorDocument(contractor.getDocumentNumber());
    }
}
