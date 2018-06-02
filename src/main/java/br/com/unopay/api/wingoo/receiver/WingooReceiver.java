package br.com.unopay.api.wingoo.receiver;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.util.GenericObjectMapper;
import br.com.unopay.api.wingoo.service.WingooService;
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

    @Autowired
    public WingooReceiver(GenericObjectMapper genericObjectMapper,
                          WingooService ticketService) {
        this.genericObjectMapper = genericObjectMapper;
        this.service = ticketService;
    }

    @Transactional
    @RabbitListener(queues = Queues.CONTRACTOR_CREATED, containerFactory = Queues.DURABLE_CONTAINER)
    public void batchReceiptNotify(String objectAsString) {
        Contractor contractor = genericObjectMapper.getAsObject(objectAsString, Contractor.class);
        log.info("sending contractor={} to Wingoo system", contractor.getDocumentNumber());
        service.create(contractor);
        log.info("contractor={} sent to Wingoo system", contractor.getDocumentNumber());
    }
}
