package br.com.unopay.api.payment.service;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.fileuploader.service.FileUploaderService;
import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.payment.cnab240.Cnab240Generator;
import br.com.unopay.api.payment.cnab240.LayoutExtractorSelector;
import br.com.unopay.api.payment.cnab240.RemittanceExtractor;
import br.com.unopay.api.payment.model.PaymentRemittance;
import br.com.unopay.api.payment.model.PaymentRemittanceItem;
import br.com.unopay.api.payment.repository.PaymentRemittanceRepository;
import br.com.unopay.api.service.BatchClosingService;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchSegmentA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchSegmentB;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getRemittanceHeader;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.OCORRENCIAS;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.SEQUENCIAL_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceRecord.SEPARATOR;
import static br.com.unopay.api.payment.model.RemittanceSituation.PROCESSING;
import static br.com.unopay.api.payment.model.RemittanceSituation.REMITTANCE_FILE_GENERATED;
import static br.com.unopay.api.uaa.exception.Errors.REMITTANCE_ALREADY_RUNNING;
import static br.com.unopay.bootcommons.exception.UnovationExceptions.unprocessableEntity;

@Service
public class PaymentRemittanceService {

    private PaymentRemittanceRepository repository;
    private BatchClosingService batchClosingService;
    private PaymentRemittanceItemService paymentRemittanceItemService;
    private IssuerService issuerService;
    @Setter private Cnab240Generator cnab240Generator;
    @Setter private FileUploaderService fileUploaderService;
    @Setter private LayoutExtractorSelector layoutExtractorSelector;

    public PaymentRemittanceService(){}

    @Autowired
    public PaymentRemittanceService(PaymentRemittanceRepository repository,
                                    BatchClosingService batchClosingService,
                                    PaymentRemittanceItemService paymentRemittanceItemService,
                                    IssuerService issuerService,
                                    Cnab240Generator cnab240Generator,
                                    FileUploaderService fileUploaderService,
                                    LayoutExtractorSelector layoutExtractorSelector) {
        this.repository = repository;
        this.batchClosingService = batchClosingService;
        this.paymentRemittanceItemService = paymentRemittanceItemService;
        this.issuerService = issuerService;
        this.cnab240Generator = cnab240Generator;
        this.fileUploaderService = fileUploaderService;
        this.layoutExtractorSelector = layoutExtractorSelector;
    }

    public PaymentRemittance findById(String id) {
        return repository.findOne(id);
    }

    public PaymentRemittance save(PaymentRemittance paymentRemittance) {
        return repository.save(paymentRemittance);
    }

    @Transactional
    @SneakyThrows
    public void processReturn(MultipartFile multipartFile) {
        String cnab240 = new String(multipartFile.getBytes());
        RemittanceExtractor remittanceExtractor = new RemittanceExtractor(getRemittanceHeader(), cnab240);
        String remittanceNumber = remittanceExtractor.extractOnFirstLine(SEQUENCIAL_ARQUIVO);
        Optional<PaymentRemittance> current = repository.findByNumber(getNumberWithoutLeftPad(remittanceNumber));
        current.ifPresent(paymentRemittance -> {
            updateItemsSituation(cnab240, paymentRemittance.getRemittanceItems());
            paymentRemittance.setSubmissionReturnDateTime(new Date());
            repository.save(paymentRemittance);
        });
    }

    @Transactional
    public void create(String issuer) {
        checkAlreadyRunning(issuer);
        Issuer currentIssuer = issuerService.findById(issuer);
        Set<BatchClosing> batchByEstablishment = batchClosingService.findFinalizedByIssuerAndPaymentBeforeToday(issuer);
        if(!batchByEstablishment.isEmpty()) {
            createRemittanceAndItems(currentIssuer, batchByEstablishment);
        }
    }

    private void createRemittanceAndItems(Issuer currentIssuer, Set<BatchClosing> batchByEstablishment) {
        Set<PaymentRemittanceItem> remittanceItems = processItems(batchByEstablishment);
        PaymentRemittance remittance = createRemittance(currentIssuer, remittanceItems);
        String generate = cnab240Generator.generate(remittance, new Date());
        fileUploaderService.uploadCnab240(generate, remittance.getFileUri());
        updateSituation(remittanceItems, remittance);
    }

    public Set<PaymentRemittance> findByIssuer(String issuerId){
        return repository.findByIssuerId(issuerId);
    }

    private void checkAlreadyRunning(String issuer) {
        Optional<PaymentRemittance> current = repository.findByIssuerIdAndSituation(issuer, PROCESSING);
        current.ifPresent((ThrowingConsumer)-> { throw unprocessableEntity().withErrors(REMITTANCE_ALREADY_RUNNING);});
    }

    private void updateSituation(Set<PaymentRemittanceItem> remittanceItems, PaymentRemittance remittance) {
        remittanceItems.forEach(paymentRemittanceItem ->  {
            paymentRemittanceItem.setSituation(REMITTANCE_FILE_GENERATED);
            paymentRemittanceItemService.save(paymentRemittanceItem);
        });
        remittance.setSituation(REMITTANCE_FILE_GENERATED);
        save(remittance);
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

    private Long getTotal() {
        return repository.count();
    }

    private PaymentRemittanceItem getCurrentItem(String id,BatchClosing batchClosing){
        Optional<PaymentRemittanceItem> current = paymentRemittanceItemService.findProcessingByEstablishment(id);
        return current.orElse(new PaymentRemittanceItem(batchClosing));
    }

    private void updateItemsSituation(String cnab240, Set<PaymentRemittanceItem> items){
        for(int currentLine = 4; currentLine < cnab240.split(SEPARATOR).length; currentLine += 4){
            String document = getDocumentNumber(cnab240, currentLine);
            Optional<PaymentRemittanceItem> remittanceItem = remittanceItemByDocument(items, document);
            final int previousLine = currentLine -1;
            remittanceItem.ifPresent(item -> updateItemSituation(cnab240, previousLine, item));
        }
    }

    private String getDocumentNumber(String cnab240, int line) {
        RemittanceExtractor segmentB = new RemittanceExtractor(getBatchSegmentB(), cnab240);
        return segmentB.extractOnLine(NUMERO_INSCRICAO_FAVORECIDO, line);
    }

    private void updateItemSituation(String cnab240, int line, PaymentRemittanceItem item) {
        RemittanceExtractor segmentA = getRemittanceExtractor(cnab240);
        String occurrenceCode = segmentA.extractOnLine(OCORRENCIAS, line);
        item.updateOccurrenceFields(occurrenceCode);
        paymentRemittanceItemService.save(item);
    }

    private RemittanceExtractor getRemittanceExtractor(String cnab240) {
        return layoutExtractorSelector.define(getBatchSegmentA(), cnab240);
    }

    private Optional<PaymentRemittanceItem> remittanceItemByDocument(Set<PaymentRemittanceItem> items, String document){
        return items.stream().filter(item -> item.establishmentDocumentIs(document)).findFirst();
    }

    private String getNumberWithoutLeftPad(String remittanceNumber) {
        return Integer.valueOf(remittanceNumber).toString();
    }
}
