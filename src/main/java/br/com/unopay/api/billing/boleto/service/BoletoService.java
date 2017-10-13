package br.com.unopay.api.billing.boleto.service;

import br.com.caelum.stella.boleto.transformer.GeradorDeBoleto;
import br.com.unopay.api.billing.boleto.model.Boleto;
import br.com.unopay.api.billing.boleto.model.BoletoStellaBuilder;
import br.com.unopay.api.billing.boleto.model.filter.BoletoFilter;
import br.com.unopay.api.billing.boleto.repository.BoletoRepository;
import br.com.unopay.api.fileuploader.service.FileUploaderService;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.service.OrderService;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
    private static final String EXTENTION_PDF = "%s.pdf";

    private BoletoRepository repository;
    private OrderService orderService;
    private UserDetailService userDetailService;
    @Setter private FileUploaderService fileUploaderService;

    @Value("${unopay.boleto.deadline_in_days}")
    private Integer deadlineInDays;

    public BoletoService(){}

    @Autowired
    public BoletoService(BoletoRepository repository,
                         OrderService orderService,
                         UserDetailService userDetailService,
                         FileUploaderService fileUploaderService) {
        this.repository = repository;
        this.orderService = orderService;
        this.userDetailService = userDetailService;
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

        createFile(order, boletoStella);

        return save(createBoletoModel(order, boletoStella));
    }


    public Page<Boleto> findMyByFilter(String email, BoletoFilter filter, UnovationPageRequest pageable) {
        List<String> ids = orderService.findIdsByPersonEmail(email);
        filter.setOrderId(ids);
        return findByFilter(filter, pageable);
    }

    public Page<Boleto> findByFilter(BoletoFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private void createFile(Order order, br.com.caelum.stella.boleto.Boleto boletoStella) {
        GeradorDeBoleto geradorDeBoleto = new GeradorDeBoleto(boletoStella);
        byte[] bytes = geradorDeBoleto.geraPDF();
        final String path = String.format(EXTENTION_PDF, order.getDocumentNumber());
        fileUploaderService.uploadBytes(path, bytes);
    }

    private Boleto createBoletoModel(Order order, br.com.caelum.stella.boleto.Boleto boletoStella) {
        final String path = String.format(EXTENTION_PDF, order.getDocumentNumber());
        Boleto boleto = new Boleto();
        boleto.setValue(order.getValue());
        boleto.setIssuerDocument(order.getProduct().getIssuer().documentNumber());
        boleto.setClientDocument(order.getDocumentNumber());
        boleto.setOrderId(order.getId());
        boleto.setUri(fileUploaderService.getRelativePath(path));
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
