package br.com.unopay.api.order.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.billing.creditcard.model.PaymentRequest;
import br.com.unopay.api.billing.creditcard.model.TransactionStatus;
import br.com.unopay.api.billing.creditcard.service.UserCreditCardService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService;
import br.com.unopay.api.infra.Notifier;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.notification.engine.MailValidator;
import br.com.unopay.api.notification.model.EventType;
import br.com.unopay.api.notification.service.NotificationService;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.model.OrderType;
import br.com.unopay.api.order.model.PaymentStatus;
import br.com.unopay.api.order.model.filter.OrderFilter;
import br.com.unopay.api.order.repository.OrderRepository;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.api.service.PaymentInstrumentService;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.service.ProductService;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.BadRequestException;
import br.com.unopay.bootcommons.exception.UnovationError;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
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
    private Validator validator;
    private UserCreditCardService userCreditCardService;

    public OrderService(){}

    @Autowired
    public OrderService(OrderRepository repository,
                        PersonService personService,
                        ProductService productService,
                        ContractorService contractorService,
                        ContractService contractService,
                        PaymentInstrumentService paymentInstrumentService,
                        ContractorInstrumentCreditService instrumentCreditService,
                        UserDetailService userDetailService,
                        HirerService hirerService,
                        Notifier notifier,
                        NotificationService notificationService,
                        MailValidator mailValidator,
                        Validator validator,
                        UserCreditCardService userCreditCardService){
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
        this.validator = validator;
        this.userCreditCardService = userCreditCardService;
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

    public Set<String> findIdsByPersonEmail(String email) {
        Set<Order> orders = repository
                .findTop20ByPersonPhysicalPersonDetailEmailIgnoreCaseOrderByCreateDateTimeDesc(email);
        return orders.stream().map(Order::getId).collect(Collectors.toSet());
    }

    public Set<String> getMyOrderIds(String email, Set<String> ordersIds) {
        Set<String> ids = findIdsByPersonEmail(email);
        Set<String> intersection = ordersIds.stream().filter(ids::contains).collect(Collectors.toSet());
        return ordersIds.isEmpty() ? ids : intersection;
    }

    @Transactional
    public Order create(String userEmail, Order order){
        UserDetail currentUser = userDetailService.getByEmail(userEmail);
        order.setPerson(currentUser.myContractor()
                .map(Contractor::getPerson).orElseThrow(UnovationExceptions::unauthorized));
        storeCreditCardWhenRequired(currentUser, order);
        checkCreditCardWhenRequired(currentUser, order);
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
        checkHirerWhenRequired(order);
        Order created = repository.save(order);
        created.getPaymentRequest().setOrderId(order.getId());
        notifyOrder(created);
        return created;
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

    private void checkHirerWhenRequired(Order order) {
        if(order.isType(OrderType.ADHESION)) {
            hirerService.findByDocumentNumber(order.issuerDocumentNumber());
        }
    }

    private void notifyOrder(Order created) {
        notifier.notify(Queues.ORDER_CREATED, created);
    }

    private void definePaymentValueWhenRequired(Order order) {
        defineValueWithAdhesionValueWhenRequired(order);
        defineValueWithInstallmentValueWhenRequired(order);
    }

    public void processAsPaid(String orderId){
        Order order = findById(orderId);
        order.setStatus(PaymentStatus.PAID);
        save(order);
        process(order);
    }

    public void processWithStatus(String id, TransactionStatus status){
        Order current = findById(id);
        current.defineStatus(status);
        save(current);
        process(current);
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

    private void storeCreditCardWhenRequired(UserDetail userDetail, Order order) {
        if (order.shouldStoreCard()) {
            userCreditCardService.storeForUser(userDetail, order.getPaymentRequest().getCreditCard());
        }
    }

    private void checkContractorRules(Order order) {
        Optional<Contractor> contractor = contractorService.getOptionalByDocument(order.getDocumentNumber());
        checkAdhesionWhenRequired(order, contractor.orElse(null));

        if (!contractor.isPresent() || order.isType(INSTALLMENT_PAYMENT)) {
            order.setPaymentInstrument(null);
        }
        if(order.isType(OrderType.CREDIT)) {
            contractor.ifPresent(it -> checkCreditRules(order));
        }
        contractor.ifPresent(c -> order.setContract(contractService.findById(order.getContractId())));

    }

    private void checkAdhesionWhenRequired(Order order, Contractor contractor) {
        Optional<UserDetail> existingUser = userDetailService.getByEmailOptional(order.getBillingMail());
        if(order.isType(OrderType.ADHESION)) {
            if (existingUser.isPresent()) {
                throw UnovationExceptions.conflict().withErrors(USER_ALREADY_EXISTS);
            }
            if (contractor!=null) {
                throw UnovationExceptions.conflict().withErrors(EXISTING_CONTRACTOR);
            }
            this.mailValidator.check(order.getBillingMail());
        }

    }

    private void checkCreditRules(Order order) {
        if(order.getValue() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(VALUE_REQUIRED);
        }
        checkPaymentInstrument(order);
    }

    private void checkPaymentInstrument(Order order) {
        List<PaymentInstrument> contractorInstruments = paymentInstrumentService
                .findByContractorDocument(order.getDocumentNumber());
        if (order.getPaymentInstrument() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_INSTRUMENT_REQUIRED);
        }
        Optional<PaymentInstrument> firstInstrument = getFirstInstrument(order, contractorInstruments);
        if (!firstInstrument.isPresent()) {
            throw UnovationExceptions.unprocessableEntity().withErrors(INSTRUMENT_NOT_BELONGS_TO_CONTRACTOR);
        }
        if (contractorInstruments.stream().noneMatch(instrument -> instrument.hasProduct(order.getProduct()))){
            throw UnovationExceptions.unprocessableEntity().withErrors(INSTRUMENT_IS_NOT_FOR_PRODUCT);
        }
        firstInstrument.ifPresent(order::setPaymentInstrument);
    }

    private Optional<PaymentInstrument> getFirstInstrument(Order order, List<PaymentInstrument> instruments) {
        return instruments.stream().filter(instrument -> order.hasPaymentInstrument(instrument.getId())).findFirst();
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


    private void checkCreditCardWhenRequired(UserDetail user, Order order) {
        if(!order.hasCardToken()){
            Set<ConstraintViolation<PaymentRequest>> violations = validator
                                                                    .validate(order.getPaymentRequest(),Create.class);
            if(!violations.isEmpty()){
                BadRequestException badRequestException = new BadRequestException();
                List<UnovationError> errors = violations.stream()
                        .map(constraint ->
                                new UnovationError(constraint.getPropertyPath().toString(), constraint.getMessage()))
                        .collect(Collectors.toList());
                throw badRequestException.withErrors(errors);
            }
            return;
        }
        userCreditCardService.findByTokenForUser(order.creditCardToken(), user);
    }
}
