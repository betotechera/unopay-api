package br.com.unopay.api.billing.remittance.service;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.billing.remittance.cnab240.ItauCnab240Generator;
import br.com.unopay.api.billing.remittance.cnab240.LayoutExtractorSelector;
import br.com.unopay.api.billing.remittance.cnab240.RemittanceExtractor;
import br.com.unopay.api.billing.remittance.cnab240.filler.BradescoRemittanceLayout;
import br.com.unopay.api.billing.remittance.model.PaymentOperationType;
import br.com.unopay.api.billing.remittance.model.PaymentRemittance;
import br.com.unopay.api.billing.remittance.model.PaymentRemittanceItem;
import br.com.unopay.api.billing.remittance.model.RemittancePayee;
import br.com.unopay.api.billing.remittance.model.filter.PaymentRemittanceFilter;
import br.com.unopay.api.billing.remittance.model.filter.RemittanceFilter;
import br.com.unopay.api.billing.remittance.repository.PaymentRemittanceRepository;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.credit.model.Credit;
import br.com.unopay.api.credit.model.CreditProcessed;
import br.com.unopay.api.credit.service.CreditService;
import br.com.unopay.api.fileuploader.service.FileUploaderService;
import br.com.unopay.api.infra.Notifier;
import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.model.BatchClosingSituation;
import br.com.unopay.api.notification.service.NotificationService;
import br.com.unopay.api.service.BatchClosingService;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.api.util.Time;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static br.com.unopay.api.billing.remittance.cnab240.filler.BradescoRemittanceLayout.getBatchSegmentB;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.AGENCIA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_CONTA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_FAVORECIDO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.OCORRENCIAS;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.SEQUENCIAL_ARQUIVO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceRecord.SEPARATOR;
import static br.com.unopay.api.billing.remittance.model.RemittanceSituation.PROCESSING;
import static br.com.unopay.api.billing.remittance.model.RemittanceSituation.REMITTANCE_FILE_GENERATED;
import static br.com.unopay.api.credit.model.CreditInsertionType.DIRECT_DEBIT;
import static br.com.unopay.api.credit.model.CreditTarget.HIRER;
import static br.com.unopay.api.uaa.exception.Errors.REMITTANCE_ALREADY_RUNNING;
import static br.com.unopay.api.uaa.exception.Errors.REMITTANCE_WITH_INVALID_DATA;
import static br.com.unopay.bootcommons.exception.UnovationExceptions.unprocessableEntity;

@Slf4j
@Service
public class PaymentRemittanceService {

    public static final int LINE_POSITION = 4;
    private PaymentRemittanceRepository repository;
    private BatchClosingService batchClosingService;
    private PaymentRemittanceItemService paymentRemittanceItemService;
    private IssuerService issuerService;
    @Setter private ItauCnab240Generator bradescoCnab240Generator;
    @Setter private FileUploaderService fileUploaderService;
    @Setter private LayoutExtractorSelector layoutExtractorSelector;
    private UserDetailService userDetailService;
    @Setter private Notifier notifier;
    private CreditService creditService;
    @Setter private NotificationService notificationService;

    public PaymentRemittanceService(){}

    @Autowired
    public PaymentRemittanceService(PaymentRemittanceRepository repository,
                                    BatchClosingService batchClosingService,
                                    PaymentRemittanceItemService paymentRemittanceItemService,
                                    IssuerService issuerService,
                                    ItauCnab240Generator bradescoCnab240Generator,
                                    FileUploaderService fileUploaderService,
                                    LayoutExtractorSelector layoutExtractorSelector,
                                    UserDetailService userDetailService, Notifier notifier,
                                    CreditService creditService,
                                    NotificationService notificationService) {
        this.repository = repository;
        this.batchClosingService = batchClosingService;
        this.paymentRemittanceItemService = paymentRemittanceItemService;
        this.issuerService = issuerService;
        this.bradescoCnab240Generator = bradescoCnab240Generator;
        this.fileUploaderService = fileUploaderService;
        this.layoutExtractorSelector = layoutExtractorSelector;
        this.userDetailService = userDetailService;
        this.notifier = notifier;
        this.creditService = creditService;
        this.notificationService = notificationService;
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
        String remittanceNumber = getRemittanceNumber(cnab240);
        Optional<PaymentRemittance> current = repository.findByNumber(remittanceNumber);
        current.ifPresent(paymentRemittance -> {
            checkRemittanceInformation(cnab240, paymentRemittance);
            updateItemsSituation(cnab240, paymentRemittance.getRemittanceItems());
            paymentRemittance.setSubmissionReturnDateTime(new Date());
            repository.save(paymentRemittance);
        });
    }

    @Transactional
    public void createForBatch(String issuer) {
        createForBatch(issuer, today());
    }

    @Transactional
    public void createForBatch(String issuer, Date at) {
        Issuer currentIssuer = issuerService.findById(issuer);
        Set<BatchClosing> byEstablishment = batchClosingService.findFinalizedByIssuerAndPaymentBefore(issuer, at);
        if(!byEstablishment.isEmpty()) {
            createFromBatchClosing(currentIssuer, byEstablishment);
        }
    }

    public void execute(RemittanceFilter filter) {
        Issuer currentIssuer = issuerService.findById(filter.getId());
        checkAlreadyRunning(currentIssuer.documentNumber());
        notifier.notify(Queues.PAYMENT_REMITTANCE, filter);
    }

    @Transactional
    public void createForCredit(String issuerId) {
        Issuer currentIssuer = issuerService.findById(issuerId);
        Set<Credit> credits = creditService
                .findProcessingByIssuerAndInsertionType(currentIssuer.getId(), DIRECT_DEBIT);
        if(!credits.isEmpty()) {
            createFromCredit(currentIssuer, credits);
        }
    }

    private void createFromBatchClosing(Issuer currentIssuer, Set<BatchClosing> batchByEstablishment){
        List<RemittancePayee> payees = batchByEstablishment.stream()
                .map(batch ->
                        new RemittancePayee(batch.getEstablishment(),
                        currentIssuer.paymentBankCode(),
                        batch.getValue()))
                .collect(Collectors.toList());
        createRemittanceAndItems(currentIssuer, payees, PaymentOperationType.CREDIT);
        changeTheBatchesSituation(batchByEstablishment);
    }

    private void changeTheBatchesSituation(Set<BatchClosing> batchByEstablishment) {
        batchByEstablishment.stream().map(batch -> batchClosingService.findById(batch.getId())).forEach(current -> {
            current.setSituation(BatchClosingSituation.REMITTANCE_FILE_GENERATED);
            batchClosingService.save(current);
        });
    }

    private void createFromCredit(Issuer currentIssuer, Set<Credit> credits){
        List<RemittancePayee> payees = credits.stream()
                .map(credit ->
                        new RemittancePayee(credit.getHirer(),
                                currentIssuer.paymentBankCode(),
                                credit.getValue()))
                .collect(Collectors.toList());
        createRemittanceAndItems(currentIssuer, payees, PaymentOperationType.DEBIT);
    }

    private void createRemittanceAndItems(Issuer currentIssuer, Collection<RemittancePayee> payees,
                                          PaymentOperationType operationType) {
        Set<PaymentRemittanceItem> remittanceItems = paymentRemittanceItemService.processItems(payees);
        PaymentRemittance remittance = createRemittance(currentIssuer, remittanceItems);
        remittance.setOperationType(operationType);
        String generate = bradescoCnab240Generator.generate(remittance, new Date());
        String cnabUri = fileUploaderService.uploadCnab(generate, remittance.getFileUri());
        remittance.setCnabUri(cnabUri);
        updateSituation(remittanceItems, remittance);
        notificationService.sendRemittanceCreatedMail(currentIssuer.getFinancierMailForRemittance(), remittance);
    }

    public List<PaymentRemittance> findByPayerDocument(String payerDocument){
        return repository.findByPayerDocumentNumberOrderByCreatedDateTime(payerDocument);
    }

    private void checkAlreadyRunning(String issuerDoc) {
        Optional<PaymentRemittance> current = repository.findByPayerDocumentNumberAndSituation(issuerDoc, PROCESSING);
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
        remittanceItems.forEach(i-> i.setPaymentRemittance(paymentRemittance));
        return save(paymentRemittance);
    }



    private Long getTotal() {
        return repository.count();
    }



    private void updateItemsSituation(String cnab240, Set<PaymentRemittanceItem> items){
        for(int currentLine = LINE_POSITION; currentLine < cnab240.split(SEPARATOR).length; currentLine+=LINE_POSITION){
            String establishmentDocument = getEstablishmentDocumentNumber(cnab240, currentLine);
            Optional<PaymentRemittanceItem> remittanceItem = remittanceItemByDocument(items, establishmentDocument);
            final int previousLine = currentLine - 1;
            remittanceItem.ifPresent(item -> updateItemSituationAndSave(cnab240, previousLine, item));
        }
    }

    private String getEstablishmentDocumentNumber(String cnab240, int line) {
        RemittanceExtractor segmentB = new RemittanceExtractor(getBatchSegmentB(), cnab240);
        return segmentB.extractOnLine(NUMERO_INSCRICAO_FAVORECIDO, line);
    }

    private String getRemittanceNumber(String cnab240) {
        RemittanceExtractor remittanceExtractor = new RemittanceExtractor(BradescoRemittanceLayout.getRemittanceHeader(), cnab240);
        String remittanceNumber = remittanceExtractor.extractOnFirstLine(SEQUENCIAL_ARQUIVO);
        return getNumberWithoutLeftPad(remittanceNumber);
    }

    private void updateItemSituationAndSave(String cnab240, int line, PaymentRemittanceItem item) {
        RemittanceExtractor segmentA = getRemittanceExtractor(cnab240);
        String occurrenceCode = segmentA.extractOnLine(OCORRENCIAS, line);
        item.updateOccurrenceFields(occurrenceCode);
        paymentRemittanceItemService.save(item);
        if(!item.processedWithError()) {
            CreditProcessed processed = new CreditProcessed(item.payerDocumentNumber(),
                                                            item.getValue(), DIRECT_DEBIT, HIRER);
            notifier.notify(Queues.CREDIT_PROCESSED, processed);
        }
    }

    private void checkRemittanceInformation(String cnab240, PaymentRemittance remittance) {
        RemittanceExtractor remittanceHeader = new RemittanceExtractor(BradescoRemittanceLayout.getRemittanceHeader(), cnab240);
        String document = remittanceHeader.extractOnFirstLine(NUMERO_INSCRICAO_EMPRESA);
        String agency = remittanceHeader.extractOnFirstLine(AGENCIA);
        String accountNumber = remittanceHeader.extractOnFirstLine(NUMERO_CONTA);
        if(!remittance.payerDocumentNumberIs(document) || !remittance.payerAgency(agency) ||
                !remittance.payerAccountNumber(accountNumber)){
            throw UnovationExceptions.unprocessableEntity().withErrors(REMITTANCE_WITH_INVALID_DATA);
        }
    }

    private RemittanceExtractor getRemittanceExtractor(String cnab240) {
        return layoutExtractorSelector.define(BradescoRemittanceLayout.getBatchSegmentA(), cnab240);
    }

    private Optional<PaymentRemittanceItem> remittanceItemByDocument(Set<PaymentRemittanceItem> items, String document){
        return items.stream().filter(item -> item.payeeDocumentIs(document)).findFirst();
    }

    private UserDetail getUserByEmail(String userEmail) {
        return userDetailService.getByEmail(userEmail);
    }

    public Page<PaymentRemittance> findByFilter(PaymentRemittanceFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private String getNumberWithoutLeftPad(String remittanceNumber) {
        return Integer.valueOf(remittanceNumber.replaceAll(" ", "")).toString();
    }

    private Date today() {
        return Time.create();
    }


}
