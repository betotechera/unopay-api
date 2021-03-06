package br.com.unopay.api.billing.boleto.service;

import br.com.caelum.stella.boleto.transformer.GeradorDeBoleto;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.PaymentBankAccount;
import br.com.unopay.api.billing.boleto.model.BoletoStellaBuilder;
import br.com.unopay.api.billing.boleto.model.Ticket;
import br.com.unopay.api.billing.boleto.model.filter.TicketFilter;
import br.com.unopay.api.billing.boleto.registry.TicketRegistry;
import br.com.unopay.api.billing.boleto.repository.TicketRepository;
import br.com.unopay.api.billing.remittance.cnab240.LayoutExtractorSelector;
import br.com.unopay.api.billing.remittance.cnab240.RemittanceExtractor;
import br.com.unopay.api.credit.model.Credit;
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService;
import br.com.unopay.api.credit.service.CreditService;
import br.com.unopay.api.fileuploader.service.FileUploaderService;
import br.com.unopay.api.infra.NumberGenerator;
import br.com.unopay.api.market.model.BonusBilling;
import br.com.unopay.api.market.model.NegotiationBilling;
import br.com.unopay.api.market.service.BonusBillingService;
import br.com.unopay.api.market.service.NegotiationBillingService;
import br.com.unopay.api.model.Billable;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.notification.service.NotificationService;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.service.OrderProcessor;
import br.com.unopay.api.order.service.OrderService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import javax.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static br.com.unopay.api.billing.remittance.cnab240.filler.BradescoRemittanceLayout.getBatchSegmentT;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CODIGO_OCORRENCIA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.IDENTIFICACAO_TITULO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceRecord.SEPARATOR;
import static br.com.unopay.api.uaa.exception.Errors.TICKET_NUMBER_ALREADY_EXISTS;
import static java.lang.String.format;

@Slf4j
@Service
public class TicketService {

    private static final int SIZE = 10;
    private static final String PDF_PATH = "%s/%s/%s.pdf";

    public static final String PAID = "06";
    public static final int FIRST_TICKET_LINE = 3;
    public static final int NEXT_TICKET_LINE = 2;
    public static final int TRAILER = 2;

    @Getter private TicketRepository repository;
    @Setter private OrderService orderService;
    @Setter private OrderProcessor orderProcessor;
    @Setter private CreditService creditService;
    @Setter private Set<TicketRegistry> ticketRegistries;
    @Setter private FileUploaderService fileUploaderService;
    @Setter private LayoutExtractorSelector layoutExtractorSelector;
    @Setter private NotificationService notificationService;
    @Setter private NumberGenerator numberGenerator;
    @Setter private NegotiationBillingService negotiationBillingService;
    @Setter private BonusBillingService bonusBillingService;
    @Setter private ContractorInstrumentCreditService contractorInstrumentCreditService;


    @Value("${unopay.boleto.deadline_in_days}")
    private Integer deadlineInDays;

    @Value("${unopay.boleto.folder}")
    private String folder;

    public TicketService(){}

    @Autowired
    public TicketService(TicketRepository repository,
                         OrderService orderService,
                         OrderProcessor orderProcessor, CreditService creditService,
                         Set<TicketRegistry> ticketRegistries, FileUploaderService fileUploaderService,
                         LayoutExtractorSelector layoutExtractorSelector,
                         NotificationService notificationService,
                         NegotiationBillingService negotiationBillingService,
                         BonusBillingService bonusBillingService,
                         ContractorInstrumentCreditService contractorInstrumentCreditService) {
        this.repository = repository;
        this.orderService = orderService;
        this.orderProcessor = orderProcessor;
        this.creditService = creditService;
        this.ticketRegistries = ticketRegistries;
        this.fileUploaderService = fileUploaderService;
        this.layoutExtractorSelector = layoutExtractorSelector;
        this.notificationService = notificationService;
        this.negotiationBillingService = negotiationBillingService;
        this.numberGenerator = new NumberGenerator(this.repository);
        this.bonusBillingService = bonusBillingService;
        this.contractorInstrumentCreditService = contractorInstrumentCreditService;
    }

    public Ticket save(Ticket ticket) {
        return repository.save(ticket);
    }

    public Ticket findById(String id) {
        Optional<Ticket> ticket = repository.findById(id);
        return ticket.orElseThrow(() -> UnovationExceptions.notFound().withErrors(Errors.TICKET_NOT_FOUND));
    }

    public Ticket getByIdForIssuer(String id, Issuer issuer) {
        Optional<Ticket> ticket = repository.findByIdAndIssuerDocument(id, issuer.documentNumber());
        return ticket.orElseThrow(()->UnovationExceptions.notFound().withErrors(Errors.TICKET_NOT_FOUND));
    }

    public Ticket getByIdForPayer(String id, Person payer) {
        Optional<Ticket> ticket = repository.findByIdAndPayerDocument(id, payer.documentNumber());
        return ticket.orElseThrow(()->UnovationExceptions.notFound().withErrors(Errors.TICKET_NOT_FOUND));
    }

    @Transactional
    public Ticket createForOrder(String orderId) {
        Order order = orderService.findById(orderId);
        return create(order);
    }

    @Transactional
    public Ticket createForCredit(Credit credit) {
        Credit current = creditService.findById(credit.getId());
        return create(current);
    }

    @Transactional
    public Ticket createForNegotiationBilling(NegotiationBilling billing) {
        NegotiationBilling current = negotiationBillingService.findById(billing.getId());
        return create(current);
    }

    @Transactional
    public Ticket createForBonusBilling(BonusBilling billing) {
        BonusBilling current = bonusBillingService.findById(billing.getId());
        return create(current);
    }

    private Ticket create(Billable billable) {
        PaymentBankAccount paymentBankAccount = billable.getIssuer().getPaymentAccount();
        String number = getValidNumber();
        TicketRegistry ticketRegistry = getTicketRegistry(paymentBankAccount);
        String clearOurNumber = ticketRegistry.registryTicket(billable, paymentBankAccount, number);
        br.com.caelum.stella.boleto.Boleto boletoStella = new BoletoStellaBuilder()
                .issuer(billable.getIssuer())
                .number(number)
                .expirationDays(deadlineInDays)
                .payer(billable.getPayer())
                .value(billable.getValue())
                .ourNumber(clearOurNumber)
                .build(ticketRegistry.getBank());
        Ticket ticket = createTicketModel(billable, boletoStella, clearOurNumber);
        if(billable.hasBillingMail()) {
            notificationService.sendTicketIssued(billable, ticket);
        }
        return ticket;
    }

    private TicketRegistry getTicketRegistry(PaymentBankAccount paymentBankAccount) {
        return ticketRegistries.stream().filter(registry -> registry.hasBacenCode(paymentBankAccount.backBacenCode())).findFirst().orElseThrow(UnovationExceptions.failedDependency());
    }

    private String getValidNumber() {
        String number = numberGenerator.createNumber(SIZE);
        if(repository.countByNumber(number) > 0){
            throw UnovationExceptions.conflict().withErrors(TICKET_NUMBER_ALREADY_EXISTS.withOnlyArgument(number));
        }
        return number;
    }

    @SneakyThrows
    public void processTicketReturnForIssuer(Issuer issuer, MultipartFile multipartFile) {
        String cnab240 = new String(multipartFile.getBytes());
        for (int currentLine = FIRST_TICKET_LINE; currentLine < cnab240.split(SEPARATOR).length - TRAILER;
             currentLine += NEXT_TICKET_LINE) {
            String ticketNumber = getTicketNumber(cnab240, currentLine);
            String occurrenceCode = getOccurrenceCode(cnab240, currentLine);
            log.info("ticket={} issuer={}  occurrenceCode={}", ticketNumber, issuer.documentNumber(), occurrenceCode);
            processTicket(occurrenceCode, () ->
                    repository.findByNumberAndIssuerDocumentAndProcessedAtIsNull(ticketNumber,issuer.documentNumber()));
        }
    }

    @SneakyThrows
    public void processTicketReturn(MultipartFile multipartFile) {
        String cnab240 = new String(multipartFile.getBytes());
        for (int currentLine = FIRST_TICKET_LINE; currentLine < cnab240.split(SEPARATOR).length - TRAILER;
                                                                currentLine += NEXT_TICKET_LINE) {
            String ticketNumber = getTicketNumber(cnab240, currentLine);
            String occurrenceCode = getOccurrenceCode(cnab240, currentLine);
            log.info("ticket={} occurrenceCode={}", ticketNumber, occurrenceCode);
            processTicket(occurrenceCode, () -> repository.findByNumberAndProcessedAtIsNull(ticketNumber));
        }
    }

    private void processTicket(String occurrenceCode, Supplier<Optional<Ticket>> ticketSupplier) {
        Optional<Ticket> current = ticketSupplier.get();
        current.ifPresent(ticket -> {
            if(PAID.equals(occurrenceCode) && !PAID.equals(ticket.getOccurrenceCode())){
                log.info("ticket from={}", ticket.getPaymentSource());
                if(ticket.fromContractor()){
                    processOrderAsPaid(ticket);
                }
                if(ticket.fromCreditHirer()){
                    processCreditAsPaid(ticket);
                }
                if(ticket.fromBillingHirer()){
                    processHirerBillingAsPaid(ticket);
                }
                if(ticket.fromBonusBilling()) {
                    processBonusBillingAsPaid(ticket);
                }
            }else{
                if(!PAID.equals(ticket.getOccurrenceCode())) {
                    ticket.setOccurrenceCode(occurrenceCode);
                    repository.save(ticket);
                }
            }
        });
        log.info("found={} paymentSource={}", current.isPresent(), current.map(Ticket::getPaymentSource).orElse(null));
    }

    private String getOccurrenceCode(String cnab240, int currentLine) {
        RemittanceExtractor remittanceExtractor = layoutExtractorSelector.define(getBatchSegmentT(), cnab240);
        return remittanceExtractor.extractOnLine(CODIGO_OCORRENCIA, currentLine);
    }

    private void processOrderAsPaid(Ticket ticket) {
        orderProcessor.processAsPaid(ticket.getSourceId());
        defineAsPaid(ticket);
    }

    private void processCreditAsPaid(Ticket ticket) {
        creditService.processAsPaid(ticket.getSourceId());
        defineAsPaid(ticket);
    }

    private void processHirerBillingAsPaid(Ticket ticket){
        negotiationBillingService.processAsPaid(ticket.getSourceId());
        NegotiationBilling billing = negotiationBillingService.findByNumber(ticket.getSourceId());
        if(billing.getBillingWithCredits()) {
            creditService.processAsPaid(billing.creditId());
        }
        defineAsPaid(ticket);
    }

    private void processBonusBillingAsPaid(Ticket ticket) {
        bonusBillingService.processAsPaid(ticket.getSourceId());
        BonusBilling bonusBilling = bonusBillingService.findByNumber(ticket.getSourceId());
        contractorInstrumentCreditService.processBonusBilling(bonusBilling);
        defineAsPaid(ticket);
    }

    private void defineAsPaid(Ticket ticket) {
        ticket.setProcessedAt(new Date());
        ticket.setOccurrenceCode(PAID);
        repository.save(ticket);
    }

    private String getTicketNumber(String cnab240, int currentLine) {
        RemittanceExtractor remittanceExtractor = layoutExtractorSelector.define(getBatchSegmentT(), cnab240);
        String ticketNumber = remittanceExtractor.extractOnLine(IDENTIFICACAO_TITULO,currentLine);
        return numberGenerator.getNumberWithoutLeftPad(ticketNumber);
    }

    public Page<Ticket> findByFilter(TicketFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private String createFile(Billable billable, br.com.caelum.stella.boleto.Boleto boletoStella) {
        GeradorDeBoleto geradorDeBoleto = new GeradorDeBoleto(boletoStella);
        byte[] bytes = geradorDeBoleto.geraPDF();
        final String path = format(PDF_PATH, this.folder, billable.getPayer().documentNumber(), billable.getNumber());
        return fileUploaderService.uploadBytes(path, bytes);
    }

    private Ticket createTicketModel(Billable billable, br.com.caelum.stella.boleto.Boleto boletoStella,
                                     String ourNumber) {
        final String path = createFile(billable, boletoStella);
        Ticket ticket = new Ticket();
        ticket.setValue(billable.getValue());
        ticket.setIssuerDocument(billable.getIssuer().documentNumber());
        ticket.setPayerDocument(billable.getPayer().documentNumber());
        ticket.setSourceId(billable.getNumber());
        ticket.setUri(path);
        ticket.setTypingCode(boletoStella.getLinhaDigitavel());
        ticket.setNumber(boletoStella.getNumeroDoDocumento());
        ticket.setOurNumber(ourNumber);
        ticket.setCreateDateTime(new Date());
        ticket.setExpirationDateTime(boletoStella.getDatas().getVencimento().getTime());
        ticket.setPaymentSource(billable.getPaymentSource());
        return save(ticket);
    }

}
