package br.com.unopay.api.order.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService;
import br.com.unopay.api.infra.Notifier;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.notification.engine.MailValidator;
import br.com.unopay.api.notification.model.EventType;
import br.com.unopay.api.notification.service.NotificationService;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.model.OrderType;
import br.com.unopay.api.order.model.filter.OrderFilter;
import br.com.unopay.api.order.repository.OrderRepository;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.api.service.PaymentInstrumentService;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.service.ProductService;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.order.model.OrderType.ADHESION;
import static br.com.unopay.api.order.model.OrderType.CREDIT;
import static br.com.unopay.api.order.model.OrderType.INSTALLMENT_PAYMENT;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.EXISTING_CONTRACTOR;
import static br.com.unopay.api.uaa.exception.Errors.INSTRUMENT_IS_NOT_FOR_PRODUCT;
import static br.com.unopay.api.uaa.exception.Errors.INSTRUMENT_NOT_BELONGS_TO_CONTRACTOR;
import static br.com.unopay.api.uaa.exception.Errors.ORDER_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_INSTRUMENT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_REQUEST_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PRODUCT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.USER_ALREADY_EXISTS;
import static br.com.unopay.api.uaa.exception.Errors.VALUE_REQUIRED;

@Slf4j
@Service
public class OrderService {

    private OrderRepository repository;
    private PersonService personService;
    private ProductService productService;
    private ContractorService contractorService;
    private ContractService contractService;
    private PaymentInstrumentService paymentInstrumentService;
    private ContractorInstrumentCreditService instrumentCreditService;
    private UserDetailService userDetailService;
    private HirerService hirerService;
    @Setter private Notifier notifier;
    @Setter private NotificationService notificationService;
    private MailValidator mailValidator;

    public OrderService(){}

    @Autowired
    public OrderService(OrderRepository repository,
                        PersonService personService,
                        ProductService productService,
                        ContractorService contractorService,
                        ContractService contractService,
                        PaymentInstrumentService paymentInstrumentService,
                        ContractorInstrumentCreditService instrumentCreditService,
                        UserDetailService userDetailService, HirerService hirerService, Notifier notifier,
                        NotificationService notificationService, MailValidator mailValidator){
        this.repository = repository;
        this.personService = personService;
        this.productService = productService;
        this.contractorService = contractorService;
        this.contractService = contractService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.instrumentCreditService = instrumentCreditService;
        this.userDetailService = userDetailService;
        this.hirerService = hirerService;
        this.notifier = notifier;
        this.notificationService = notificationService;
        this.mailValidator = mailValidator;
    }

    public Order save(Order order) {
        return repository.save(order);
    }

    public Order findById(String id) {
        Optional<Order> order = repository.findById(id);
        return order.orElseThrow(()-> UnovationExceptions.notFound().withErrors(ORDER_NOT_FOUND));
    }

    public Order findByIdForIssuer(String id, Issuer issuer) {
        Optional<Order> order = repository.findByIdAndProductIssuerId(id, issuer.getId());
        return order.orElseThrow(()-> UnovationExceptions.notFound().withErrors(ORDER_NOT_FOUND));
    }

    public List<String> findIdsByPersonEmail(String email) {
        List<Order> orders = repository
                .findTop20ByPersonPhysicalPersonDetailEmailIgnoreCaseOrderByCreateDateTimeDesc(email);
        return orders.stream().map(Order::getId).collect(Collectors.toList());
    }

    public Order create(String userEmail, Order order){
        UserDetail currentUser = userDetailService.getByEmail(userEmail);
        if(currentUser.isContractorType()) {
            order.setPerson(currentUser.getContractor().getPerson());
        }
        return create(order);
    }

    @Transactional
    public Order create(Order order) {
        validateProduct(order);
        return createOrder(order);
    }

    @Transactional
    public Order createForIssuer(Issuer issuer, Order order) {
        validateProductForIssuer(issuer, order);
        return createOrder(order);
    }

    private Order createOrder(Order order) {
        validateReferences(order);
        order.normalize();
        order.setPerson(getOrCreatePerson(order));
        incrementNumber(order);
        checkContractorRules(order);
        definePaymentValueWhenRequired(order);
        order.setCreateDateTime(new Date());
        hirerService.findByDocumentNumber(order.issuerDocumentNumber());
        Order created = repository.save(order);
        created.getPaymentRequest().setOrderId(order.getId());
        notifyOrder(created);
        return created;
    }

    private void notifyOrder(Order created) {
        notifier.notify(Queues.ORDER_CREATED, created);
    }

    private void definePaymentValueWhenRequired(Order order) {
        defineValueWithAdhesionValueWhenRequired(order);
        defineValueWithInstallmentValueWhenRequired(order);
    }

    public void process(Order order){
        if(order.paid()) {
            if(order.isType(CREDIT)) {
                instrumentCreditService.processOrder(order);
                log.info("credit processed for order={} type={} of value={}",
                        order.getId(),order.getType(), order.getValue());
                notificationService.sendPaymentEmail(order,  EventType.PAYMENT_APPROVED);
                return;
            }
            if(order.isType(INSTALLMENT_PAYMENT)){
                contractService.markInstallmentAsPaidFrom(order);
                log.info("contract paid for order={} type={} of value={}",
                        order.getId(),order.getType(), order.getValue());
                notificationService.sendPaymentEmail(order,  EventType.PAYMENT_APPROVED);
                return;
            }
            if(order.isType(ADHESION)){
                contractService.dealCloseWithIssuerAsHirer(order.getPerson(), order.getProductCode());
                log.info("adhesion paid for order={} type={} of value={}",
                        order.getId(),order.getType(), order.getValue());
                notificationService.sendPaymentEmail(order,  EventType.PAYMENT_APPROVED);
                return;
            }
        }
        notificationService.sendPaymentEmail(order,  EventType.PAYMENT_DENIED);
    }

    private void defineValueWithInstallmentValueWhenRequired(Order order) {
        if(!order.isType(OrderType.ADHESION)){
            Contract contract = contractService.findById(order.getContractId());
            if(order.isType(OrderType.INSTALLMENT_PAYMENT)) {
                order.setValue(contract.installmentValue());
            }
        }
    }

    private void defineValueWithAdhesionValueWhenRequired(Order order) {
        if(order.isType(OrderType.ADHESION)) {
            order.setContract(null);
            if(order.productWithMembershipFee()){
                order.setValue(order.getProductMembershipFee());
                return;
            }
            order.setValue(order.getProductInstallmentValue());
        }
    }

    private void checkContractorRules(Order order) {
        Optional<Contractor> contractor = contractorService.getOptionalByDocument(order.getDocumentNumber());
        Optional<UserDetail> existingUser = userDetailService.getByEmailOptional(order.getBillingMail());
        if(order.isType(OrderType.ADHESION) && existingUser.isPresent()){
            throw UnovationExceptions.conflict().withErrors(USER_ALREADY_EXISTS);
        }
        if(order.isType(OrderType.ADHESION) && contractor.isPresent()){
            throw UnovationExceptions.conflict().withErrors(EXISTING_CONTRACTOR);
        }
        List<PaymentInstrument> instruments = paymentInstrumentService
                                                                .findByContractorDocument(order.getDocumentNumber());
        if (!contractor.isPresent()) {
            order.setPaymentInstrument(null);
        }
        if(order.isType(OrderType.CREDIT)) {
            contractor.ifPresent(it -> checkCreditRules(order, instruments));
        }
        contractor.ifPresent(c -> order.setContract(contractService.findById(order.getContractId())));
        if(order.isType(OrderType.ADHESION)) {
            this.mailValidator.check(order.getBillingMail());
        }
    }

    private void checkCreditRules(Order order, List<PaymentInstrument> instruments) {
        if(order.getValue() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(VALUE_REQUIRED);
        }
        if (order.getPaymentInstrument() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_INSTRUMENT_REQUIRED);
        }
        Optional<PaymentInstrument> instrumentOptional = instruments.stream()
                                    .filter(instrument ->
                                            instrument.getId()
                                                    .equals(order.getPaymentInstrument().getId())).findFirst();
        if (!instrumentOptional.isPresent()) {
            throw UnovationExceptions.unprocessableEntity().withErrors(INSTRUMENT_NOT_BELONGS_TO_CONTRACTOR);
        }
        if (instruments.stream().noneMatch(instrument -> instrument.getProduct().equals(order.getProduct()))) {
            throw UnovationExceptions.unprocessableEntity().withErrors(INSTRUMENT_IS_NOT_FOR_PRODUCT);
        }
        instrumentOptional.ifPresent(order::setPaymentInstrument);
    }

    private void validateReferences(Order order) {
        if(order.getPaymentRequest() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_REQUEST_REQUIRED);
        }
        if((order.isType(OrderType.INSTALLMENT_PAYMENT) ||
                order.isType(OrderType.CREDIT)) &&
                order.getContract() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(CONTRACT_REQUIRED);
        }
    }

    private void validateProduct(Order order) {
        if(order.getProduct() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(PRODUCT_REQUIRED);
        }
        order.setProduct(productService.findById(order.getProduct().getId()));
    }

    private void validateProductForIssuer(Issuer issuer, Order order) {
        if(order.getProduct() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(PRODUCT_REQUIRED);
        }
        order.setProduct(productService.findByIdForIssuer(order.getProduct().getId(), issuer));
    }

    private void incrementNumber(Order order) {
        Optional<Order> last = repository.findFirstByOrderByCreateDateTimeDesc();
        order.incrementNumber(last.map(Order::getNumber).orElse(null));
    }

    private Person getOrCreatePerson(Order order) {
        Optional<Person> person = personService.findOptionalByDocument(order.getDocumentNumber());
        return person.orElseGet(()-> personService.save(order.getPerson()));
    }

    public Page<Order> findByFilter(OrderFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    public List<Order> findAll(){
        return repository.findAllByOrderByCreateDateTimeDesc();
    }

    @Transactional
    public void update(String id, Order order) {
        Order current = findById(id);
        update(order, current);
    }

    @Transactional
    public void updateForIssuer(String id,Issuer issuer, Order order) {
        Order current = findByIdForIssuer(id, issuer);
        update(order, current);
    }

    private void update(Order order, Order current) {
        current.validateUpdate();
        current.updateOnly(order,"status");
        if (current.paid()) {
            process(current);
        }
        repository.save(current);
    }
}
