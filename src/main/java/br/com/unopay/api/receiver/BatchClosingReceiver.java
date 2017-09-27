package br.com.unopay.api.receiver;

import br.com.unopay.api.config.Queues;
import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.service.BatchClosingService;
import br.com.unopay.api.util.GenericObjectMapper;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("!test")
@Component
public class BatchClosingReceiver {

    private GenericObjectMapper genericObjectMapper;
    private BatchClosingService batchClosingService;

    @Autowired
    public BatchClosingReceiver(GenericObjectMapper genericObjectMapper,
                                BatchClosingService batchClosingService) {
        this.genericObjectMapper = genericObjectMapper;
        this.batchClosingService = batchClosingService;
    }

    @Transactional
    @RabbitListener(queues = Queues.BATCH_CLOSING, containerFactory = Queues.DURABLE_CONTAINER)
    public void batchReceiptNotify(String objectAsString) {
        BatchClosing batchClosing = genericObjectMapper.getAsObject(objectAsString, BatchClosing.class);
        log.info("processing batch closing for establishment={}", batchClosing.establishmentId());
        batchClosingService.process(batchClosing.establishmentId(), batchClosing.getClosingDateTime());
        log.info("processed batch closing for establishment={}", batchClosing.establishmentId());
    }
}
