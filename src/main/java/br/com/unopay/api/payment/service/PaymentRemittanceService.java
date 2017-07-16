package br.com.unopay.api.payment.service;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.fileuploader.service.FileUploaderService;
import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.payment.cnab240.Cnab240Generator;
import br.com.unopay.api.payment.cnab240.filler.FilledRecord;
import br.com.unopay.api.payment.model.PaymentRemittance;
import br.com.unopay.api.payment.model.PaymentRemittanceItem;
import br.com.unopay.api.payment.repository.PaymentRemittanceRepository;
import br.com.unopay.api.service.BatchClosingService;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentRemittanceService {

    private PaymentRemittanceRepository repository;
    private BatchClosingService batchClosingService;
    private PaymentRemittanceItemService paymentRemittanceItemService;
    private IssuerService issuerService;
    @Setter private Cnab240Generator cnab240Generator;
    @Setter private FileUploaderService fileUploaderService;

    @Autowired
    public PaymentRemittanceService(PaymentRemittanceRepository repository,
                                    BatchClosingService batchClosingService,
                                    PaymentRemittanceItemService paymentRemittanceItemService,
                                    IssuerService issuerService,
                                    Cnab240Generator cnab240Generator,
                                    FileUploaderService fileUploaderService) {
        this.repository = repository;
        this.batchClosingService = batchClosingService;
        this.paymentRemittanceItemService = paymentRemittanceItemService;
        this.issuerService = issuerService;
        this.cnab240Generator = cnab240Generator;
        this.fileUploaderService = fileUploaderService;
    }

    public PaymentRemittance findById(String id) {
        return repository.findOne(id);
    }

    public PaymentRemittance save(PaymentRemittance paymentRemittance) {
        return repository.save(paymentRemittance);
    }

    @Transactional
    public PaymentRemittance create(String issuer) {
        Issuer currentIssuer = issuerService.findById(issuer);
        Set<BatchClosing> batchByEstablishment = batchClosingService.findFinalizedByIssuerAndPaymentBeforeToday(issuer);
        Set<PaymentRemittanceItem> remittanceItems = processItems(batchByEstablishment);
        PaymentRemittance remittance = createRemittance(currentIssuer, remittanceItems);
        String generate = cnab240Generator.generate(remittance, new Date());
        uploadCnab240(generate, remittance.getFileUri());
        return remittance;
    }

    @SneakyThrows
    private void uploadCnab240(String generate, String fileUri) {
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Stream.of(generate.split(FilledRecord.SEPARATOR)).forEach(line -> write(outputStream, line));
            fileUploaderService.uploadBytes(fileUri, outputStream.toByteArray());
        }
    }

    @SneakyThrows
    private void write(ByteArrayOutputStream outputStream, String line) {
        outputStream.write(line.concat("\n").getBytes());
        outputStream.flush();
    }

    private PaymentRemittance createRemittance(Issuer currentIssuer, Set<PaymentRemittanceItem> remittanceItems) {
        PaymentRemittance paymentRemittance = new PaymentRemittance(currentIssuer, getTotal());
        paymentRemittance.setRemittanceItems(remittanceItems);
        return save(paymentRemittance);
    }

    private Set<PaymentRemittanceItem> processItems(Set<BatchClosing> batchByEstablishment) {
        return batchByEstablishment.stream().map(batchClosing -> {
                PaymentRemittanceItem currentItem = getCurrentItem(batchClosing.establishmentId(), batchClosing);
                currentItem.updateValue(batchClosing.getValue());
                return paymentRemittanceItemService.save(currentItem);
            }).collect(Collectors.toSet());
    }

    public Set<PaymentRemittance> findByIssuer(String issuerId){
        return repository.findByIssuerId(issuerId);
    }

    private Long getTotal() {
        return repository.count();
    }

    private PaymentRemittanceItem getCurrentItem(String id,BatchClosing batchClosing){
        Optional<PaymentRemittanceItem> current = paymentRemittanceItemService.findProcessingByEstablishment(id);
        return current.orElse(new PaymentRemittanceItem(batchClosing));
    }
}
