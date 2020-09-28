package br.com.unopay.api.order.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.billing.creditcard.model.CreditCard;
import br.com.unopay.api.billing.creditcard.model.PaymentMethod;
import br.com.unopay.api.billing.creditcard.model.PaymentRequest;
import br.com.unopay.api.billing.creditcard.service.PersonCreditCardService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.infra.Notifier;
import br.com.unopay.api.infra.NumberGenerator;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.model.OrderSummary;
import br.com.unopay.api.order.model.OrderType;
import br.com.unopay.api.order.model.OrderValidator;
import br.com.unopay.api.order.model.PaymentStatus;
import br.com.unopay.api.order.model.filter.OrderFilter;
import br.com.unopay.api.order.repository.OrderRepository;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.config.CacheConfig.CONTRACTOR_ORDERS;
import static br.com.unopay.api.uaa.exception.Errors.ORDER_NOT_FOUND;

@Slf4j
@Service
public class OrderService {

    private OrderRepository repository;
    private PersonService personService;
    private ContractService contractService;
    private UserDetailService userDetailService;
    @Setter private Notifier notifier;
    private PersonCreditCardService personCreditCardService;
    private OrderValidator orderValidator;
    private NumberGenerator numberGenerator;

    public OrderService(){}

    @Autowired
    public OrderService(OrderRepository repository,
                        PersonService personService,
                        ContractService contractService,
                        UserDetailService userDetailService,
                        Notifier notifier,
                        PersonCreditCardService personCreditCardService,
                        OrderValidator orderValidator){
        this.repository = repository;
        this.personService = personService;
        this.contractService = contractService;
        this.userDetailService = userDetailService;
        this.notifier = notifier;
        this.personCreditCardService = personCreditCardService;
        this.orderValidator = orderValidator;
        this.numberGenerator = new NumberGenerator(repository);
    }

    public Order save(Order order) {
        return repository.save(order);
    }

    public Order findById(String id) {
        Optional<Order> order = repository.findById(id);
        return order.orElseThrow(()-> UnovationExceptions.notFound().withErrors(ORDER_NOT_FOUND.withOnlyArgument(id)));
    }

    public Order findByIdForContractor(String id, Contractor contractor) {
        Optional<Order> order = repository.findByIdAndPersonDocumentNumber(id, contractor.getDocumentNumber());
        return order.orElseThrow(()-> UnovationExceptions.notFound().withErrors(ORDER_NOT_FOUND.withOnlyArgument(id)));
    }

    public Order findByNumber(String number) {
        Optional<Order> order = repository.findByNumber(number);
        return order.orElseThrow(()-> UnovationExceptions.notFound().withErrors(ORDER_NOT_FOUND.withOnlyArgument(number)));
    }

    public Order findByIdForIssuer(String id, Issuer issuer) {
        Optional<Order> order = repository.findByIdAndProductIssuerId(id, issuer.getId());
        return order.orElseThrow(()-> UnovationExceptions.notFound().withErrors(ORDER_NOT_FOUND.withOnlyArgument(id)));
    }

    public Set<String> findIdsByPersonEmail(String email) {
        Set<Order> orders = getLastOrders(email);
        return orders.stream().map(Order::getId).collect(Collectors.toSet());
    }

    public Set<OrderSummary> findSummaryByPersonDocument(String document) {
        Set<Order> orders = getLastOrdersByDocument(document);
        return orders.stream().map(OrderSummary::new).collect(Collectors.toSet());
    }

    public Set<String> findNumbersByPersonEmail(String email) {
        Set<Order> orders = getLastOrders(email);
        return orders.stream().map(Order::getNumber).collect(Collectors.toSet());
    }

    private Set<Order> getLastOrders(String email) {
        return repository
                .findTop20ByPersonPhysicalPersonDetailEmailIgnoreCaseOrderByCreateDateTimeDesc(email);
    }

    private Set<Order> getLastOrdersByDocument(String email) {
        return repository
                .findTop20ByPersonDocumentNumberOrderByCreateDateTimeDesc(email);
    }

    @Cacheable(value = CONTRACTOR_ORDERS, key = "#email + '_' + T(java.util.Objects).hash(#ordersIds)")
    public Set<String> getMyOrderIds(String email, Set<String> ordersIds) {
        Set<String> ids = findIdsByPersonEmail(email);
        Set<String> intersection = ordersIds.stream().filter(ids::contains).collect(Collectors.toSet());
        return ordersIds.isEmpty() ? ids : intersection;
    }

    @Cacheable(value = CONTRACTOR_ORDERS, key = "#email + '_' + T(java.util.Objects).hash(#orderNumbers)")
    public Set<String> getMyOrderNumbers(String email, Set<String> orderNumbers) {
        Set<String> numbers = findNumbersByPersonEmail(email);
        Set<String> intersection = orderNumbers.stream().filter(numbers::contains).collect(Collectors.toSet());
        return orderNumbers.isEmpty() ? numbers : intersection;
    }

    public Order create(String userEmail, Order order){
        UserDetail currentUser = userDetailService.getByEmail(userEmail);
        order.setPerson(currentUser.myContractor()
                .map(Contractor::getPerson).orElseThrow(UnovationExceptions::unauthorized));
        storeCreditCardForUserWhenRequired(order.getPerson(), order);
        orderValidator.checkCreditCardWhenRequired(order.getPerson(), order);
        return create(order);
    }

    @Transactional
    public Order create(Order order) {
        log.info("Creating order={}", order);
        orderValidator.validateProduct(order);
        defineCardTokenWhenRequired(order);
        return createOrder(order);
    }

    private void defineCardTokenWhenRequired(Order order) {
        if(order.is(PaymentMethod.CARD) && !order.hasCardToken()){
            String token = personCreditCardService.getLastActiveTokenByUser(order.personEmail());
            if(token == null && !order.hasCardToken()) {
                log.info("The credit card token was not found and the store flag is={}", order.shouldStoreCard());
                if(order.shouldStoreCard()){
                    generatorCardTokenAndStoreWhenRequired(order);
                    return;
                }
                order.definePaymentMethod(PaymentMethod.BOLETO);
                return;
            }
            order.defineCardToken(token);
        }
    }

    @Transactional
    public Order createForIssuer(Issuer issuer, Order order) {
        orderValidator.validateProductForIssuer(issuer, order);
        return createOrder(order);
    }

    private Order createOrder(Order order) {
        order.setMeUp();
        order.validateMe();
        orderValidator.validateReferences(order);
        order.normalize();
        order.setPerson(getOrCreatePerson(order));
        incrementNumber(order);
        orderValidator.checkContractorRules(order);
        definePaymentValueWhenRequired(order);
        orderValidator.checkHirerWhenRequired(order);
        Order created = repository.save(order);
        created.getPaymentRequest().setOrderId(order.getId());
        log.info("The created order is={}", order.toString());
        notifier.notify(Queues.ORDER_CREATED, created);
        return created;
    }

    public Order requestPayment(Contractor contractor, String orderId, PaymentRequest paymentRequest){
        Order current = findByIdForContractor(orderId, contractor);
        paymentRequest.setOrderId(orderId);
        current.setPaymentRequest(paymentRequest);
        return requestPayment(current);
    }
    public Order requestPayment(String orderId, PaymentRequest paymentRequest){
        Order current = findById(orderId);
        if(!current.isType(OrderType.ADHESION)){
            throw UnovationExceptions.badRequest().withErrors(Errors.INVALID_ORDER_TYPE.withOnlyArgument(current.getType()));
        }
        paymentRequest.setOrderId(orderId);
        current.setPaymentRequest(paymentRequest);
        return requestPayment(current);
    }
    private Order requestPayment(Order current){
        current.checkAlreadyPaid();
        current.setStatus(PaymentStatus.WAITING_PAYMENT);
        save(current);
        notifier.notify(Queues.ORDER_CREATED, current);
        return current;
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
            notifier.notify(Queues.ORDER_UPDATED, current);
        }
        repository.save(current);
    }

    private void definePaymentValueWhenRequired(Order order) {
        defineValueWithAdhesionValueWhenRequired(order);
        defineValueWithInstallmentValueWhenRequired(order);
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
            order.setValue(order.getProductInstallmentTotal(order.candidatesSize()));
        }
    }

    private void storeCreditCardForUserWhenRequired(Person person, Order order) {
        if (order.shouldStoreCard()) {
            person.setIssuerDocument(order.issuerDocumentNumber());
            personCreditCardService.storeForUser(person, order.getPaymentRequest().getCreditCard());
        }
    }

    private void generatorCardTokenAndStoreWhenRequired(Order order) {
        if (order.shouldStoreCard() && order.hasCard()) {
            Person person = order.getPerson();
            person.setIssuerDocument(order.issuerDocumentNumber());
            CreditCard creditCard = personCreditCardService.storeCard(person, order.creditCard());
            if(creditCard != null) {
                personCreditCardService.storeForUser(person, creditCard);
                order.defineCardToken(creditCard.getToken());
            }
        }
    }

    private void incrementNumber(Order order) {
        order.setNumber(numberGenerator.createNumber());
    }

    private Person getOrCreatePerson(Order order) {
        Optional<Person> person = personService.findOptionalByDocument(order.getDocumentNumber());
        return person.orElseGet(()-> personService.createOrUpdate(order.getPerson()));
    }

    public Page<Order> findByFilter(OrderFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    public List<Order> findAll(){
        return repository.findAllByOrderByCreateDateTimeDesc();
    }



}
