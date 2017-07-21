package br.com.unopay.api.payment.service;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.fileuploader.service.FileUploaderService;
import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.payment.cnab240.Cnab240Generator;
import br.com.unopay.api.payment.cnab240.RemittanceExtractor;
import br.com.unopay.api.payment.model.PaymentRemittance;
import br.com.unopay.api.payment.model.PaymentRemittanceItem;
import br.com.unopay.api.payment.repository.PaymentRemittanceRepository;
import br.com.unopay.api.service.BatchClosingService;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

    public PaymentRemittanceService(){}

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

    @SneakyThrows
    public void processReturn(MultipartFile multipartFile) {
        String cnab240 = new String(multipartFile.getBytes());
        RemittanceExtractor remittanceExtractor = new RemittanceExtractor(getRemittanceHeader(), cnab240);
        String remittanceNumber = remittanceExtractor.extractOnLineFirstLine(SEQUENCIAL_ARQUIVO);
        Optional<PaymentRemittance> byNumber = repository.findByNumber(Integer.valueOf(remittanceNumber).toString());
        byNumber.ifPresent(paymentRemittance -> updateItems(cnab240, paymentRemittance.getRemittanceItems()));
    }

    @Transactional
    public void create(String issuer) {
        checkAlreadyRunning(issuer);
        Issuer currentIssuer = issuerService.findById(issuer);
        Set<BatchClosing> batchByEstablishment = batchClosingService.findFinalizedByIssuerAndPaymentBeforeToday(issuer);
        Set<BatchClosing> sameIssuerBank = filter(batchByEstablishment, batch ->
                                                        batch.establishmentBankCodeIs(currentIssuer.paymentBankCode()));
        Set<BatchClosing> withOthersBanks = filter(batchByEstablishment,
                                              batch -> !batch.establishmentBankCodeIs(currentIssuer.paymentBankCode()));
        createRemittanceAndItems(currentIssuer, sameIssuerBank);
        createRemittanceAndItems(currentIssuer, withOthersBanks);
    }

    private void checkAlreadyRunning(String issuer) {
        Optional<PaymentRemittance> current = repository.findByIssuerIdAndSituation(issuer, PROCESSING);
        current.ifPresent((ThrowingConsumer)-> { throw unprocessableEntity().withErrors(REMITTANCE_ALREADY_RUNNING);});
    }

    private void createRemittanceAndItems(Issuer currentIssuer, Set<BatchClosing> batchByEstablishment) {
        if(!batchByEstablishment.isEmpty()) {
            Set<PaymentRemittanceItem> remittanceItems = processItems(batchByEstablishment);
            PaymentRemittance remittance = createRemittance(currentIssuer, remittanceItems);
            String generate = cnab240Generator.generate(remittance, new Date());
            uploadCnab240(generate, remittance.getFileUri());
            updateSituation(remittanceItems, remittance);
        }
    }

    private void updateSituation(Set<PaymentRemittanceItem> remittanceItems, PaymentRemittance remittance) {
        remittanceItems.forEach(paymentRemittanceItem ->  {
            paymentRemittanceItem.setSituation(REMITTANCE_FILE_GENERATED);
            paymentRemittanceItemService.save(paymentRemittanceItem);
        });
        remittance.setSituation(REMITTANCE_FILE_GENERATED);
        save(remittance);
    }

    @SneakyThrows
    private void uploadCnab240(String generate, String fileUri) {
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Stream.of(generate.split(SEPARATOR)).forEach(line -> write(outputStream, line));
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
        Integer bank = getAnyEstablishmentBankCode(remittanceItems);
        if(currentIssuer.bankCodeIs(bank)){
            paymentRemittance.defineCurrentAccountTransferOption();
        }
        paymentRemittance.setRemittanceItems(remittanceItems);
        return save(paymentRemittance);
    }

    private Integer getAnyEstablishmentBankCode(Set<PaymentRemittanceItem> remittanceItems) {
        return remittanceItems.stream().map(PaymentRemittanceItem::getEstablishmentBankCode).findFirst().orElse(null);
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

    private void updateItems(String cnab240, Set<PaymentRemittanceItem> items){
        String[] cnabLines = cnab240.split(SEPARATOR);
        for(int line = 4; line < cnabLines.length; line+=4){
            RemittanceExtractor segmentB = new RemittanceExtractor(getBatchSegmentB(), cnab240);
            String document = segmentB.extractOnLine(NUMERO_INSCRICAO_FAVORECIDO, line);
            Optional<PaymentRemittanceItem> remittanceItem = remittanceItemByDocument(items, document);
            final int previousLine = line -1;
            remittanceItem.ifPresent(item ->
                    updateItem(cnab240, previousLine, item)
            );
        }
    }

    private void updateItem(String cnab240, int previousLine, PaymentRemittanceItem item) {
        RemittanceExtractor segmentA = new RemittanceExtractor(getBatchSegmentA(), cnab240);
        String occurrences = segmentA.extractOnLine(OCORRENCIAS, previousLine);
        item.setOccurrenceCode(occurrences);
        paymentRemittanceItemService.save(item);
    }

    private Optional<PaymentRemittanceItem> remittanceItemByDocument(Set<PaymentRemittanceItem> items, String document){
        return items.stream().filter(item -> item.establishmentDocumentIs(document)).findFirst();
    }

    private <T> Set<T> filter(Collection<T> collection, Predicate<T> consumer) {
        return collection.stream()
                .filter(consumer)
                .collect(Collectors.toSet());
    }
}
