package br.com.unopay.api.billing.boleto.service;

import br.com.caelum.stella.boleto.transformer.GeradorDeBoleto;
import br.com.unopay.api.bacen.model.PaymentBankAccount;
import br.com.unopay.api.billing.boleto.model.Boleto;
import br.com.unopay.api.billing.boleto.model.BoletoStellaBuilder;
import br.com.unopay.api.billing.boleto.model.filter.BoletoFilter;
import br.com.unopay.api.billing.boleto.repository.BoletoRepository;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl.TicketRequest;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.TituloDto;
import br.com.unopay.api.billing.boleto.santander.service.CobrancaOnlineService;
import br.com.unopay.api.billing.boleto.santander.translate.CobrancaOlnineBuilder;
import br.com.unopay.api.fileuploader.service.FileUploaderService;
import br.com.unopay.api.model.Billable;
import br.com.unopay.api.notification.service.NotificationService;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.service.OrderService;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static java.lang.String.format;
import static java.lang.String.valueOf;

@Service
public class BoletoService {

    private static final int SIZE = 4;
    private static final String PDF_PATH = "%s/%s/%s.pdf";
    public static final String ZERO = "0";
    public static final String EMPTY = "";

    private BoletoRepository repository;
    private OrderService orderService;
    @Setter private CobrancaOnlineService cobrancaOnlineService;
    @Setter private FileUploaderService fileUploaderService;
    private NotificationService notificationService;

    @Value("${unopay.boleto.deadline_in_days}")
    private Integer deadlineInDays;

    @Value("${unopay.boleto.folder}")
    private String folder;

    public BoletoService(){}

    @Autowired
    public BoletoService(BoletoRepository repository,
                         OrderService orderService,
                         CobrancaOnlineService cobrancaOnlineService,
                         FileUploaderService fileUploaderService, NotificationService notificationService) {
        this.repository = repository;
        this.orderService = orderService;
        this.cobrancaOnlineService = cobrancaOnlineService;
        this.fileUploaderService = fileUploaderService;
        this.notificationService = notificationService;
    }

    public Boleto save(Boleto boleto) {
        return repository.save(boleto);
    }

    public Boleto findById(String id) {
        return repository.findOne(id);
    }

    public Boleto createForOrder(String orderId) {
        Order order = orderService.findById(orderId);
        Boleto boleto = create(order);
        notificationService.sendBoletoIssued(order, boleto);
        return boleto;
    }

    private Boleto create(Billable order) {
        PaymentBankAccount paymentBankAccount = order.getIssuer().getPaymentAccount();
        String number = createNumber(order);
        List<TicketRequest.Dados.Entry> entries = new CobrancaOlnineBuilder()
                .payer(order.getPayer()).expirationDays(deadlineInDays)
                .paymentBankAccount(paymentBankAccount)
                .value(order.getValue())
                .yourNumber(number).build();

        TituloDto tituloDto = cobrancaOnlineService.getTicket(entries, paymentBankAccount.getStation());
        String clearOurNumber = tituloDto.getNossoNumero().replace(ZERO, EMPTY);
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

    public Page<Boleto> findMyByFilter(String email, BoletoFilter filter, UnovationPageRequest pageable) {
        List<String> ids = orderService.findIdsByPersonEmail(email);
        List<String> intersection = filter.getOrderId().stream().filter(ids::contains).collect(Collectors.toList());
        ids = filter.getOrderId().isEmpty() ? ids : intersection;
        filter.setOrderId(ids);
        return findByFilter(filter, pageable);
    }

    public Page<Boleto> findByFilter(BoletoFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private String createFile(Billable billable, br.com.caelum.stella.boleto.Boleto boletoStella) {
        GeradorDeBoleto geradorDeBoleto = new GeradorDeBoleto(boletoStella);
        byte[] bytes = geradorDeBoleto.geraPDF();
        final String path = format(PDF_PATH, this.folder, billable.getPayer().documentNumber(), billable.getNumber());
        return fileUploaderService.uploadBytes(path, bytes);
    }

    private Boleto createBoletoModel(Billable billable, br.com.caelum.stella.boleto.Boleto boletoStella,
                                     String ourNumber) {
        final String path = createFile(billable, boletoStella);
        Boleto boleto = new Boleto();
        boleto.setValue(billable.getValue());
        boleto.setIssuerDocument(billable.getIssuer().documentNumber());
        boleto.setClientDocument(billable.getPayer().documentNumber());
        boleto.setOrderId(billable.getId());
        boleto.setUri(path);
        boleto.setTypingCode(boletoStella.getLinhaDigitavel());
        boleto.setNumber(boletoStella.getNumeroDoDocumento());
        boleto.setOurNumber(ourNumber);
        boleto.setCreateDateTime(new Date());
        boleto.setProcessedAt(new Date());
        boleto.setExpirationDateTime(boletoStella.getDatas().getVencimento().getTime());
        return save(boleto);
    }

    private String createNumber(Billable order) {
        long count = repository.count();
        String number = format("%s%s%s", order.getNumber().replace(ZERO, EMPTY),
                valueOf(count),
                valueOf(order.getCreateDateTime().getTime()));
        return number.substring(0, Math.min(number.length(), SIZE));
    }
}
