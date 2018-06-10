package br.com.unopay.api.order.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.billing.creditcard.service.UserCreditCardService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.infra.Notifier;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.model.OrderType;
import br.com.unopay.api.order.model.OrderValidator;
import br.com.unopay.api.order.model.filter.OrderFilter;
import br.com.unopay.api.order.repository.OrderRepository;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.api.service.PersonService;
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
    private UserCreditCardService userCreditCardService;
    private OrderValidator orderValidator;

    public OrderService(){}

    @Autowired
    public OrderService(OrderRepository repository,
                        PersonService personService,
                        ContractService contractService,
                        UserDetailService userDetailService,
                        Notifier notifier,
                        UserCreditCardService userCreditCardService,
                        OrderValidator orderValidator){
        this.repository = repository;
        this.personService = personService;
        this.contractService = contractService;
        this.userDetailService = userDetailService;
        this.notifier = notifier;
        this.userCreditCardService = userCreditCardService;
        this.orderValidator = orderValidator;
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

    @Cacheable(value = CONTRACTOR_ORDERS, key = "#email")
    public Set<String> findIdsByPersonEmail(String email) {
        Set<Order> orders = repository
                .findTop20ByPersonPhysicalPersonDetailEmailIgnoreCaseOrderByCreateDateTimeDesc(email);
        return orders.stream().map(Order::getId).collect(Collectors.toSet());
    }

    @Cacheable(value = CONTRACTOR_ORDERS, key = "#email + '_' + T(java.util.Objects).hash(#ordersIds)")
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
        orderValidator.checkCreditCardWhenRequired(currentUser, order);
        return create(order);
    }

    @Transactional
    public Order create(Order order) {
        orderValidator.validateProduct(order);
        return createOrder(order);
    }

    @Transactional
    public Order createForIssuer(Issuer issuer, Order order) {
        orderValidator.validateProductForIssuer(issuer, order);
        return createOrder(order);
    }

    private Order createOrder(Order order) {
        orderValidator.validateReferences(order);
        order.normalize();
        order.setPerson(getOrCreatePerson(order));
        incrementNumber(order);
        orderValidator.checkContractorRules(order);
        definePaymentValueWhenRequired(order);
        order.setMeUp();
        order.validateMe();
        orderValidator.checkHirerWhenRequired(order);
        Order created = repository.save(order);
        created.getPaymentRequest().setOrderId(order.getId());
        notifier.notify(Queues.ORDER_CREATED, created);
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

    private void storeCreditCardWhenRequired(UserDetail userDetail, Order order) {
        if (order.shouldStoreCard()) {
            userCreditCardService.storeForUser(userDetail, order.getPaymentRequest().getCreditCard());
        }
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



}
