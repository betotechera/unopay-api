package br.com.unopay.api.billing.boleto.service;

import br.com.caelum.stella.boleto.transformer.GeradorDeBoleto;
import br.com.unopay.api.billing.boleto.model.Boleto;
import br.com.unopay.api.billing.boleto.model.BoletoStellaBuilder;
import br.com.unopay.api.billing.boleto.model.filter.BoletoFilter;
import br.com.unopay.api.billing.boleto.repository.BoletoRepository;
import br.com.unopay.api.fileuploader.service.FileUploaderService;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.service.OrderService;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class BoletoService {

    private static final String ZERO = "0";
    private static final int SIZE = 8;
    private static final String PDF_PATH = "%s/%s/%s.pdf";

    private BoletoRepository repository;
    private OrderService orderService;
    @Setter private FileUploaderService fileUploaderService;

    @Value("${unopay.boleto.deadline_in_days}")
    private Integer deadlineInDays;

    @Value("${unopay.boleto.folder}")
    private String folder;

    public BoletoService(){}

    @Autowired
    public BoletoService(BoletoRepository repository,
                         OrderService orderService,
                         FileUploaderService fileUploaderService) {
        this.repository = repository;
        this.orderService = orderService;
        this.fileUploaderService = fileUploaderService;
    }

    public Boleto save(Boleto boleto) {
        return repository.save(boleto);
    }

    public Boleto findById(String id) {
        return repository.findOne(id);
    }

    public Boleto create(String orderId) {
        Order order = orderService.findById(orderId);
        String number = createNumber();
        br.com.caelum.stella.boleto.Boleto boletoStella = new BoletoStellaBuilder()
                .issuer(order.getProduct().getIssuer())
                .number(number)
                .expirationDays(deadlineInDays)
                .client(order.getPerson())
                .value(order.getValue())
                .build();

        Boleto boleto = createBoletoModel(order, boletoStella);

        return save(boleto);
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

    private String createFile(Order order, br.com.caelum.stella.boleto.Boleto boletoStella) {
        GeradorDeBoleto geradorDeBoleto = new GeradorDeBoleto(boletoStella);
        byte[] bytes = geradorDeBoleto.geraPDF();
        final String path = String.format(PDF_PATH, this.folder, order.getDocumentNumber(), order.getNumber());
        return fileUploaderService.uploadBytes(path, bytes);
    }

    private Boleto createBoletoModel(Order order, br.com.caelum.stella.boleto.Boleto boletoStella) {
        final String path = createFile(order, boletoStella);
        Boleto boleto = new Boleto();
        boleto.setValue(order.getValue());
        boleto.setIssuerDocument(order.getProduct().getIssuer().documentNumber());
        boleto.setClientDocument(order.getDocumentNumber());
        boleto.setOrderId(order.getId());
        boleto.setUri(path);
        boleto.setTypingCode(boletoStella.getLinhaDigitavel());
        boleto.setNumber(boletoStella.getNumeroDoDocumento());
        boleto.setCreateDateTime(new Date());
        boleto.setProcessedAt(new Date());
        boleto.setExpirationDateTime(boletoStella.getDatas().getVencimento().getTime());
        return boleto;
    }

    private String createNumber() {
        Optional<Boleto> last = repository.findFirstByOrderByCreateDateTimeDesc();
        String lastNumber = last.map(Boleto::getNumber).orElse(ZERO);
        Long number = Long.valueOf(lastNumber);
        number++;
        return StringUtils.leftPad(String.valueOf(number), SIZE, ZERO);
    }
}
