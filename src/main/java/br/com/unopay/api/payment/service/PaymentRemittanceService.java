package br.com.unopay.api.payment.service;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.fileuploader.service.FileUploaderService;
import br.com.unopay.api.infra.Notifier;
import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.model.Credit;
import br.com.unopay.api.payment.cnab240.Cnab240Generator;
import br.com.unopay.api.payment.cnab240.LayoutExtractorSelector;
import br.com.unopay.api.payment.cnab240.RemittanceExtractor;
import br.com.unopay.api.payment.model.PaymentRemittance;
import br.com.unopay.api.payment.model.PaymentRemittanceItem;
import br.com.unopay.api.payment.model.RemittancePayee;
import br.com.unopay.api.payment.model.filter.PaymentRemittanceFilter;
import br.com.unopay.api.payment.model.filter.RemittanceFilter;
import br.com.unopay.api.payment.repository.PaymentRemittanceRepository;
import br.com.unopay.api.service.BatchClosingService;
import br.com.unopay.api.service.CreditService;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.api.util.GenericObjectMapper;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchSegmentA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchSegmentB;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getRemittanceHeader;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CONVEIO_BANCO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.OCORRENCIAS;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.SEQUENCIAL_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceRecord.SEPARATOR;
import static br.com.unopay.api.payment.model.RemittanceSituation.PROCESSING;
import static br.com.unopay.api.payment.model.RemittanceSituation.REMITTANCE_FILE_GENERATED;
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
    @Setter private Cnab240Generator cnab240Generator;
    @Setter private FileUploaderService fileUploaderService;
    @Setter private LayoutExtractorSelector layoutExtractorSelector;
    private UserDetailService userDetailService;
    @Setter private Notifier notifier;
    private GenericObjectMapper genericObjectMapper;
    private HirerService hirerService;
    private CreditService creditService;

    public PaymentRemittanceService(){}

    @Autowired
    public PaymentRemittanceService(PaymentRemittanceRepository repository,
                                    BatchClosingService batchClosingService,
                                    PaymentRemittanceItemService paymentRemittanceItemService,
                                    IssuerService issuerService,
                                    Cnab240Generator cnab240Generator,
                                    FileUploaderService fileUploaderService,
                                    LayoutExtractorSelector layoutExtractorSelector,
                                    UserDetailService userDetailService, Notifier notifier,
                                    GenericObjectMapper genericObjectMapper,
                                    HirerService hirerService,
                                    CreditService creditService) {
        this.repository = repository;
        this.batchClosingService = batchClosingService;
        this.paymentRemittanceItemService = paymentRemittanceItemService;
        this.issuerService = issuerService;
        this.cnab240Generator = cnab240Generator;
        this.fileUploaderService = fileUploaderService;
        this.layoutExtractorSelector = layoutExtractorSelector;
        this.userDetailService = userDetailService;
        this.notifier = notifier;
        this.genericObjectMapper = genericObjectMapper;
        this.hirerService = hirerService;
        this.creditService = creditService;
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
    public void createFortBatch(String issuer) {
        createFortBatch(issuer, today());
    }

    @Transactional
    public void createFortBatch(String issuer, Date at) {
        Issuer currentIssuer = issuerService.findById(issuer);
        Set<BatchClosing> byEstablishment = batchClosingService.findFinalizedByIssuerAndPaymentBefore(issuer, at);
        if(!byEstablishment.isEmpty()) {
            createFromBatchClosing(currentIssuer, byEstablishment);
        }
    }

    public void execute(RemittanceFilter filter) {
        Issuer currentIssuer = issuerService.findById(filter.getId());
        checkAlreadyRunning(currentIssuer.documentNumber());
        notifier.notify(Queues.UNOPAY_PAYMENT_REMITTANCE, filter);
    }

    @Transactional
    public void createForCredit(String issuerId) {
        Issuer currentIssuer = issuerService.findById(issuerId);
        Set<Credit> credits = creditService.findByIssuerDocument(currentIssuer.documentNumber());
        createFromCredit(currentIssuer, credits);
    }

    @Transactional
    @RabbitListener(queues = Queues.UNOPAY_PAYMENT_REMITTANCE)
    public void remittanceReceiptNotify(String objectAsString) {
        RemittanceFilter filter = genericObjectMapper.getAsObject(objectAsString, RemittanceFilter.class);
        log.info("processing remittance for issuer={}", filter.getId());
        createFortBatch(filter.getId(), filter.getAt());
        log.info("processed remittance for issuer={}", filter.getId());
    }

    private void createFromBatchClosing(Issuer currentIssuer, Set<BatchClosing> batchByEstablishment){
        Set<RemittancePayee> payees = batchByEstablishment.stream()
                .map(batch ->
                        new RemittancePayee(batch.getEstablishment(),
                        currentIssuer.paymentBankCode(),
                        batch.getValue()))
                .collect(Collectors.toSet());
        createRemittanceAndItems(currentIssuer, payees);
    }

    private void createFromCredit(Issuer currentIssuer, Set<Credit> credits){
        Set<RemittancePayee> payees = credits.stream()
                .map(credit ->
                        new RemittancePayee(hirerService.findByDocumentNumber(credit.getHirerDocument()),
                                currentIssuer.paymentBankCode(),
                                credit.getValue()))
                .collect(Collectors.toSet());
        createRemittanceAndItems(currentIssuer, payees);
    }

    private void createRemittanceAndItems(Issuer currentIssuer, Set<RemittancePayee> batchByEstablishment) {
        Set<PaymentRemittanceItem> remittanceItems = processItems(batchByEstablishment);
        PaymentRemittance remittance = createRemittance(currentIssuer, remittanceItems);
        String generate = cnab240Generator.generate(remittance, new Date());
        String cnabUri = fileUploaderService.uploadCnab240(generate, remittance.getFileUri());
        remittance.setCnabUri(cnabUri);
        updateSituation(remittanceItems, remittance);
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
        return save(paymentRemittance);
    }

    private Set<PaymentRemittanceItem> processItems(Set<RemittancePayee> payees) {
        return payees.stream().map(payee -> {
                PaymentRemittanceItem currentItem = getCurrentItem(payee.getDocumentNumber(), payee);
                currentItem.updateValue(payee.getReceivable());
                return paymentRemittanceItemService.save(currentItem);
            }).collect(Collectors.toSet());
    }

    private Long getTotal() {
        return repository.count();
    }

    private PaymentRemittanceItem getCurrentItem(String id,RemittancePayee payee){
        Optional<PaymentRemittanceItem> current = paymentRemittanceItemService.findProcessingByEstablishment(id);
        return current.orElse(new PaymentRemittanceItem(payee));
    }

    private void updateItemsSituation(String cnab240, Set<PaymentRemittanceItem> items){
        for(int currentLine = LINE_POSITION; currentLine < cnab240.split(SEPARATOR).length; currentLine+=LINE_POSITION){
            String establishmentDocument = getEstablishmentDocumentNumber(cnab240, currentLine);
            Optional<PaymentRemittanceItem> remittanceItem = remittanceItemByDocument(items, establishmentDocument);
            final int previousLine = currentLine - 1;
            remittanceItem.ifPresent(item -> updateItemSituation(cnab240, previousLine, item));
        }
    }

    private String getEstablishmentDocumentNumber(String cnab240, int line) {
        RemittanceExtractor segmentB = new RemittanceExtractor(getBatchSegmentB(), cnab240);
        return segmentB.extractOnLine(NUMERO_INSCRICAO_FAVORECIDO, line);
    }

    private String getRemittanceNumber(String cnab240) {
        RemittanceExtractor remittanceExtractor = new RemittanceExtractor(getRemittanceHeader(), cnab240);
        String remittanceNumber = remittanceExtractor.extractOnFirstLine(SEQUENCIAL_ARQUIVO);
        return getNumberWithoutLeftPad(remittanceNumber);
    }

    private void updateItemSituation(String cnab240, int line, PaymentRemittanceItem item) {
        RemittanceExtractor segmentA = getRemittanceExtractor(cnab240);
        String occurrenceCode = segmentA.extractOnLine(OCORRENCIAS, line);
        item.updateOccurrenceFields(occurrenceCode);
        paymentRemittanceItemService.save(item);
    }

    private void checkRemittanceInformation(String cnab240, PaymentRemittance remittance) {
        RemittanceExtractor remittanceHeader = new RemittanceExtractor(getRemittanceHeader(), cnab240);
        String document = remittanceHeader.extractOnFirstLine(NUMERO_INSCRICAO_EMPRESA);
        String agreementNumber = remittanceHeader.extractOnFirstLine(CONVEIO_BANCO);
        if(!remittance.payerDocumentNumberIs(document) || !remittance.payerBankAgreementNumberIs(agreementNumber)){
            throw UnovationExceptions.unprocessableEntity().withErrors(REMITTANCE_WITH_INVALID_DATA);
        }
    }

    private RemittanceExtractor getRemittanceExtractor(String cnab240) {
        return layoutExtractorSelector.define(getBatchSegmentA(), cnab240);
    }

    private Optional<PaymentRemittanceItem> remittanceItemByDocument(Set<PaymentRemittanceItem> items, String document){
        return items.stream().filter(item -> item.payeeDocumentIs(document)).findFirst();
    }

    public Page<PaymentRemittance> findMyByFilter(String userEmail, PaymentRemittanceFilter filter,
                                             UnovationPageRequest pageable) {
        return findByFilter(buildFilterBy(filter,getUserByEmail(userEmail)),pageable);
    }

    private PaymentRemittanceFilter buildFilterBy(PaymentRemittanceFilter filter, UserDetail currentUser) {
        if(currentUser.isEstablishmentType()) {
            filter.setEstablishment(currentUser.establishmentId());
        }
        if(currentUser.isIssuerType()) {
            filter.setIssuer(currentUser.issuerId());
        }
        if(currentUser.isAccreditedNetworkType()) {
            filter.setAccreditedNetwork(currentUser.accreditedNetworkId());
        }
        return filter;
    }

    private UserDetail getUserByEmail(String userEmail) {
        return userDetailService.getByEmail(userEmail);
    }

    public Page<PaymentRemittance> findByFilter(PaymentRemittanceFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private String getNumberWithoutLeftPad(String remittanceNumber) {
        return Integer.valueOf(remittanceNumber).toString();
    }

    private Date today() {
        return new DateTime().withMillisOfDay(0).toDate();
    }


}
