package br.com.unopay.api.order.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService;
import br.com.unopay.api.infra.Notifier;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.Person;
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
    @Setter private Notifier notifier;
    @Setter private NotificationService notificationService;

    public OrderService(){}

    @Autowired
    public OrderService(OrderRepository repository,
                        PersonService personService,
                        ProductService productService,
                        ContractorService contractorService,
                        ContractService contractService,
                        PaymentInstrumentService paymentInstrumentService,
                        ContractorInstrumentCreditService instrumentCreditService,
                        UserDetailService userDetailService, Notifier notifier,
                        NotificationService notificationService){
        this.repository = repository;
        this.personService = personService;
        this.productService = productService;
        this.contractorService = contractorService;
        this.contractService = contractService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.instrumentCreditService = instrumentCreditService;
        this.userDetailService = userDetailService;
        this.notifier = notifier;
        this.notificationService = notificationService;
    }

    public Order save(Order order) {
        return repository.save(order);
    }

    public Order findById(String id) {
        Optional<Order> order = repository.findById(id);
        return order.orElseThrow(()-> UnovationExceptions.notFound().withErrors(ORDER_NOT_FOUND));
    }

    public List<String> findIdsByPersonEmail(String email) {
        List<Order> orders = repository.findTop20ByPersonPhysicalPersonDetailEmailOrderByCreateDateTimeDesc(email);
        return orders.stream().map(Order::getId).collect(Collectors.toList());
    }

    public Order create(String userEmail, Order order){
        UserDetail currentUser = userDetailService.getByEmail(userEmail);
        if(!currentUser.isContractorType()) {
            log.warn("INCONSISTENT USER={}", currentUser);
        }
        if(currentUser.isContractorType()) {
            order.setPerson(currentUser.getContractor().getPerson());
        }
        return create(order);
    }

    @Transactional
    public Order create(Order order) {
        validateReferences(order);
        order.normalize();
        order.setPerson(getOrCreatePerson(order));
        incrementNumber(order);
        checkContractorRules(order);
        definePaymentValueWhenRequired(order);
        order.setCreateDateTime(new Date());
        Order created = repository.save(order);
        order.getPaymentRequest().setOrderId(order.getId());
        order.getPaymentRequest().setValue(order.getValue());
        notifier.notify(Queues.ORDER_CREATED, created);
        return created;
    }

    private void definePaymentValueWhenRequired(Order order) {
        processAdhesionWhenRequired(order);
        processContractRuleWhenRequired(order);
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
                contractService.dealClose(order.getPerson(), order.getProductCode());
                log.info("adhesion paid for order={} type={} of value={}",
                        order.getId(),order.getType(), order.getValue());
                return;
            }
        }
        notificationService.sendPaymentEmail(order,  EventType.PAYMENT_DENIED);
    }

    private void processContractRuleWhenRequired(Order order) {
        if(!order.isType(OrderType.ADHESION)){
            Contract contract = contractService.findById(order.getContractId());
            if(order.isType(OrderType.INSTALLMENT_PAYMENT)) {
                order.setValue(contract.installmentValue());
            }
        }
    }

    private void processAdhesionWhenRequired(Order order) {
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
        Optional<UserDetail> existingUser = userDetailService.getByEmailOptional(order.getPersonEmail());
        if(order.isType(OrderType.ADHESION) && existingUser.isPresent()){
            throw UnovationExceptions.conflict().withErrors(USER_ALREADY_EXISTS);
        }
        if(order.isType(OrderType.ADHESION) && contractor.isPresent()){
            throw UnovationExceptions.conflict().withErrors(EXISTING_CONTRACTOR);
        }
        List<PaymentInstrument> instruments = paymentInstrumentService.findByContractorDocument(order.getDocumentNumber());
        if (!contractor.isPresent()) {
            order.setPaymentInstrument(null);
        }
        if(order.isType(OrderType.CREDIT)) {
            contractor.ifPresent(contractor1 -> checkCreditRules(order, instruments));
        }
        contractor.ifPresent(c -> order.setContract(contractService.findById(order.getContractId())));
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
        if(order.getProduct() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(PRODUCT_REQUIRED);
        }
        if(order.getPaymentRequest() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_REQUEST_REQUIRED);
        }
        if((order.isType(OrderType.INSTALLMENT_PAYMENT) ||
                order.isType(OrderType.CREDIT)) &&
                order.getContract() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(CONTRACT_REQUIRED);
        }
        order.setProduct(productService.findById(order.getProduct().getId()));
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
        current.validateUpdate();
        current.updateOnly(order,"status");
        if (current.paid()) {
            process(current);
        }
        repository.save(current);
    }
}
