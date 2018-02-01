package br.com.unopay.api.billing.boleto.service;

import br.com.caelum.stella.boleto.transformer.GeradorDeBoleto;
import br.com.unopay.api.bacen.model.PaymentBankAccount;
import br.com.unopay.api.billing.boleto.model.Ticket;
import br.com.unopay.api.billing.boleto.model.BoletoStellaBuilder;
import br.com.unopay.api.billing.boleto.model.filter.BoletoFilter;
import br.com.unopay.api.billing.boleto.repository.BoletoRepository;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl.TicketRequest;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.TituloDto;
import br.com.unopay.api.billing.boleto.santander.service.CobrancaOnlineService;
import br.com.unopay.api.billing.boleto.santander.translate.CobrancaOlnineBuilder;
import br.com.unopay.api.billing.remittance.cnab240.LayoutExtractorSelector;
import br.com.unopay.api.billing.remittance.cnab240.RemittanceExtractor;
import br.com.unopay.api.credit.model.Credit;
import br.com.unopay.api.credit.service.CreditService;
import br.com.unopay.api.fileuploader.service.FileUploaderService;
import br.com.unopay.api.model.Billable;
import br.com.unopay.api.notification.service.NotificationService;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.service.OrderService;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.Setter;
import lombok.SneakyThrows;
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
import static java.lang.String.format;
import static java.lang.String.valueOf;

@Service
public class BoletoService {

    private static final int SIZE = 4;
    private static final String PDF_PATH = "%s/%s/%s.pdf";
    public static final String ZERO = "0";
    public static final String EMPTY = "";
    public static final String PAID = "06";
    public static final int FIRST_TICKET_LINE = 3;
    public static final int NEXT_TICKET_LINE = 2;
    public static final int TRAILER = 2;

    private BoletoRepository repository;
    @Setter private OrderService orderService;
    @Setter private CreditService creditService;
    @Setter private CobrancaOnlineService cobrancaOnlineService;
    @Setter private FileUploaderService fileUploaderService;
    @Setter private LayoutExtractorSelector layoutExtractorSelector;
    @Setter private NotificationService notificationService;

    @Value("${unopay.boleto.deadline_in_days}")
    private Integer deadlineInDays;

    @Value("${unopay.boleto.folder}")
    private String folder;

    public BoletoService(){}

    @Autowired
    public BoletoService(BoletoRepository repository,
                         OrderService orderService,
                         CreditService creditService,
                         CobrancaOnlineService cobrancaOnlineService,
                         FileUploaderService fileUploaderService,
                         LayoutExtractorSelector layoutExtractorSelector,
                         NotificationService notificationService) {
        this.repository = repository;
        this.orderService = orderService;
        this.creditService = creditService;
        this.cobrancaOnlineService = cobrancaOnlineService;
        this.fileUploaderService = fileUploaderService;
        this.layoutExtractorSelector = layoutExtractorSelector;
        this.notificationService = notificationService;
    }

    public Ticket save(Ticket ticket) {
        return repository.save(ticket);
    }

    public Ticket findById(String id) {
        return repository.findOne(id);
    }

    public Ticket createForOrder(String orderId) {
        Order order = orderService.findById(orderId);
        Ticket ticket = create(order);
        notificationService.sendBoletoIssued(order, ticket);
        return ticket;
    }

    public Ticket createForCredit(Credit credit) {
        Credit current = creditService.findById(credit.getId());
        Ticket ticket = create(current);
        notificationService.sendBoletoIssued(current, ticket);
        return ticket;
    }

    private Ticket create(Billable order) {
        PaymentBankAccount paymentBankAccount = order.getIssuer().getPaymentAccount();
        String number = createNumber(order);
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
        return createBoletoModel(order, boletoStella, clearOurNumber);
    }


    @Transactional
    @SneakyThrows
    public void processTicketReturn(MultipartFile multipartFile) {
        String cnab240 = new String(multipartFile.getBytes());
        for (int currentLine = FIRST_TICKET_LINE; currentLine < cnab240.split(SEPARATOR).length - TRAILER;
                                                                currentLine += NEXT_TICKET_LINE) {
            String ticketNumber = getTicketNumber(cnab240, currentLine);
            String occurrenceCode = getOccurrenceCode(cnab240, currentLine);
            Optional<Ticket> current = repository.findByNumber(ticketNumber);
            current.ifPresent(ticket -> {
                if(PAID.equals(occurrenceCode)){
                    if(ticket.fromContractor()){
                        processOrderAsPaid(ticket);
                    }
                    if(ticket.fromHirer()){
                        processCreditAsPaid(ticket);
                    }
                }else{
                    defineOccurrence(ticket, occurrenceCode);
                }
            });
        }
    }

    private String getOccurrenceCode(String cnab240, int currentLine) {
        RemittanceExtractor remittanceExtractor = layoutExtractorSelector.define(getBatchSegmentT(), cnab240);
        return remittanceExtractor.extractOnLine(CODIGO_OCORRENCIA, currentLine);
    }

    private void processOrderAsPaid(Ticket ticket) {
        defineOccurrence(ticket, PAID);
        orderService.processAsPaid(ticket.getSourceId());
    }

    private void processCreditAsPaid(Ticket ticket) {
        defineOccurrence(ticket, PAID);
        creditService.processAsPaid(ticket.getSourceId());
    }

    private void defineOccurrence(Ticket ticket, String occurrenceCode) {
        ticket.setProcessedAt(new Date());
        ticket.setOccurrenceCode(occurrenceCode);
        repository.save(ticket);
    }


    private String getTicketNumber(String cnab240, int currentLine) {
        RemittanceExtractor remittanceExtractor = layoutExtractorSelector.define(getBatchSegmentT(), cnab240);
        String ticketNumber = remittanceExtractor.extractOnLine(IDENTIFICACAO_TITULO,currentLine);
        return getNumberWithoutLeftPad(ticketNumber);
    }

    private String getNumberWithoutLeftPad(String remittanceNumber) {
        return Integer.valueOf(remittanceNumber.replaceAll(" ", "")).toString();
    }

    public Page<Ticket> findMyByFilter(String email, BoletoFilter filter, UnovationPageRequest pageable) {
        List<String> ids = orderService.findIdsByPersonEmail(email);
        List<String> intersection = filter.getOrderId().stream().filter(ids::contains).collect(Collectors.toList());
        ids = filter.getOrderId().isEmpty() ? ids : intersection;
        filter.setOrderId(ids);
        return findByFilter(filter, pageable);
    }

    public Page<Ticket> findByFilter(BoletoFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private String createFile(Billable billable, br.com.caelum.stella.boleto.Boleto boletoStella) {
        GeradorDeBoleto geradorDeBoleto = new GeradorDeBoleto(boletoStella);
        byte[] bytes = geradorDeBoleto.geraPDF();
        final String path = format(PDF_PATH, this.folder, billable.getPayer().documentNumber(), billable.getNumber());
        return fileUploaderService.uploadBytes(path, bytes);
    }

    private Ticket createBoletoModel(Billable billable, br.com.caelum.stella.boleto.Boleto boletoStella,
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

    private String createNumber(Billable order) {
        long count = repository.count();
        String number = format("%s%s%s", order.getNumber().replace(ZERO, EMPTY),
                valueOf(count),
                valueOf(order.getCreateDateTime().getTime()));
        return number.substring(0, Math.min(number.length(), SIZE));
    }

}
