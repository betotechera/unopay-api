package br.com.unopay.api.billing.boleto.service;

import br.com.caelum.stella.boleto.transformer.GeradorDeBoleto;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.PaymentBankAccount;
import br.com.unopay.api.billing.boleto.model.BoletoStellaBuilder;
import br.com.unopay.api.billing.boleto.model.Ticket;
import br.com.unopay.api.billing.boleto.model.filter.TicketFilter;
import br.com.unopay.api.billing.boleto.repository.TicketRepository;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl.TicketRequest;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.TituloDto;
import br.com.unopay.api.billing.boleto.santander.service.CobrancaOnlineService;
import br.com.unopay.api.billing.boleto.santander.translate.CobrancaOlnineBuilder;
import br.com.unopay.api.billing.remittance.cnab240.LayoutExtractorSelector;
import br.com.unopay.api.billing.remittance.cnab240.RemittanceExtractor;
import br.com.unopay.api.credit.model.Credit;
import br.com.unopay.api.credit.service.CreditService;
import br.com.unopay.api.fileuploader.service.FileUploaderService;
import br.com.unopay.api.infra.NumberGenerator;
import br.com.unopay.api.market.model.NegotiationBilling;
import br.com.unopay.api.market.service.NegotiationBillingService;
import br.com.unopay.api.model.Billable;
import br.com.unopay.api.notification.service.NotificationService;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.service.OrderProcessor;
import br.com.unopay.api.order.service.OrderService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayout.getBatchSegmentT;
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
    @Setter private CobrancaOnlineService cobrancaOnlineService;
    @Setter private FileUploaderService fileUploaderService;
    @Setter private LayoutExtractorSelector layoutExtractorSelector;
    @Setter private NotificationService notificationService;
    @Setter private NumberGenerator numberGenerator;
    @Setter private NegotiationBillingService negotiationBillingService;

    @Value("${unopay.boleto.deadline_in_days}")
    private Integer deadlineInDays;

    @Value("${unopay.boleto.folder}")
    private String folder;

    public TicketService(){}

    @Autowired
    public TicketService(TicketRepository repository,
                         OrderService orderService,
                         OrderProcessor orderProcessor, CreditService creditService,
                         CobrancaOnlineService cobrancaOnlineService,
                         FileUploaderService fileUploaderService,
                         LayoutExtractorSelector layoutExtractorSelector,
                         NotificationService notificationService,
                         NegotiationBillingService negotiationBillingService) {
        this.repository = repository;
        this.orderService = orderService;
        this.orderProcessor = orderProcessor;
        this.creditService = creditService;
        this.cobrancaOnlineService = cobrancaOnlineService;
        this.fileUploaderService = fileUploaderService;
        this.layoutExtractorSelector = layoutExtractorSelector;
        this.notificationService = notificationService;
        this.negotiationBillingService = negotiationBillingService;
        this.numberGenerator = new NumberGenerator(this.repository);
    }

    public Ticket save(Ticket ticket) {
        return repository.save(ticket);
    }

    public Ticket findById(String id) {
        return repository.findOne(id);
    }

    @Transactional
    public Ticket createForOrder(String orderId) {
        Order order = orderService.findById(orderId);
        Ticket ticket = create(order);
        notificationService.sendBoletoIssued(order, ticket);
        return ticket;
    }

    @Transactional
    public Ticket createForCredit(Credit credit) {
        Credit current = creditService.findById(credit.getId());
        Ticket ticket = create(current);
        notificationService.sendBoletoIssued(current, ticket);
        return ticket;
    }

    @Transactional
    public Ticket createForBilling(NegotiationBilling billing) {
        NegotiationBilling current = negotiationBillingService.findById(billing.getId());
        Ticket ticket = create(current);
        notificationService.sendBoletoIssued(current, ticket);
        return ticket;
    }

    private Ticket create(Billable order) {
        PaymentBankAccount paymentBankAccount = order.getIssuer().getPaymentAccount();
        String number = getValidNumber();
        List<TicketRequest.Dados.Entry> entries = new CobrancaOlnineBuilder()
                .payer(order.getPayer()).expirationDays(deadlineInDays)
                .paymentBankAccount(paymentBankAccount)
                .value(order.getValue())
                .yourNumber(number).build();

        TituloDto tituloDto = cobrancaOnlineService.getTicket(entries, paymentBankAccount.getStation());
        String clearOurNumber = Integer.valueOf(tituloDto.getNossoNumero()).toString();
        br.com.caelum.stella.boleto.Boleto boletoStella = new BoletoStellaBuilder()
                .issuer(order.getIssuer())
                .number(number)
                .expirationDays(deadlineInDays)
                .payer(order.getPayer())
                .value(order.getValue())
                .ourNumber(clearOurNumber)
                .build();
        return createTicketModel(order, boletoStella, clearOurNumber);
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
            if(PAID.equals(occurrenceCode)){
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
            }else{
                if(!PAID.equals(ticket.getOccurrenceCode())) {
                    defineOccurrence(ticket, occurrenceCode);
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
        defineOccurrence(ticket, PAID);
        orderProcessor.processAsPaid(ticket.getSourceId());
    }

    private void processCreditAsPaid(Ticket ticket) {
        defineOccurrence(ticket, PAID);
        creditService.processAsPaid(ticket.getSourceId());
    }

    private void processHirerBillingAsPaid(Ticket ticket){
        defineOccurrence(ticket, PAID);
        negotiationBillingService.processAsPaid(ticket.getSourceId());
        NegotiationBilling billing = negotiationBillingService.findById(ticket.getSourceId());
        if(billing.getBillingWithCredits()) {
            creditService.processAsPaid(billing.creditId());
        }
    }

    private void defineOccurrence(Ticket ticket, String occurrenceCode) {
        ticket.setProcessedAt(new Date());
        ticket.setOccurrenceCode(occurrenceCode);
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
        ticket.setSourceId(billable.getId());
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
