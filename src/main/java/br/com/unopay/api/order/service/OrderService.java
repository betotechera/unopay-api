package br.com.unopay.api.order.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.infra.Notifier;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.model.OrderType;
import br.com.unopay.api.order.model.filter.OrderFilter;
import br.com.unopay.api.order.repository.OrderRepository;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.api.service.PaymentInstrumentService;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.service.ProductService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.EXISTING_CONTRACTOR;
import static br.com.unopay.api.uaa.exception.Errors.INSTRUMENT_IS_NOT_FOR_PRODUCT;
import static br.com.unopay.api.uaa.exception.Errors.INSTRUMENT_NOT_BELONGS_TO_CONTRACTOR;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_INSTRUMENT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_REQUEST_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PRODUCT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.VALUE_REQUIRED;

@Service
public class OrderService {

    private OrderRepository repository;
    private PersonService personService;
    private ProductService productService;
    private ContractorService contractorService;
    private ContractService contractService;
    private PaymentInstrumentService paymentInstrumentService;
    @Setter private Notifier notifier;

    public OrderService(){}

    @Autowired
    public OrderService(OrderRepository repository,
                        PersonService personService,
                        ProductService productService,
                        ContractorService contractorService,
                        ContractService contractService,
                        PaymentInstrumentService paymentInstrumentService,
                        Notifier notifier){
        this.repository = repository;
        this.personService = personService;
        this.productService = productService;
        this.contractorService = contractorService;
        this.contractService = contractService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.notifier = notifier;
    }

    public Order save(Order order) {
        return repository.save(order);
    }

    public Order findById(String id) {
        return repository.findOne(id);
    }

    public Order create(Order order) {
        validateReferences(order);
        Optional<Person> person = personService.findByDocument(order.documentNumber());
        order.setPerson(person.orElseGet(()-> personService.save(order.getPerson())));
        incrementNumber(order);
        checkContractorRules(order);
        processAdhesionWhenRequired(order);
        processContractRuleWhenRequired(order);
        order.setCreateDateTime(new Date());
        Order created = repository.save(order);
        order.getPaymentRequest().setOrderId(order.getId());
        order.getPaymentRequest().setValue(order.getValue());
        notifier.notify(Queues.UNOPAY_ORDER_CREATED, created);
        return created;
    }

    private void processContractRuleWhenRequired(Order order) {
        if(!order.isType(OrderType.ADHESION)){
            Contract contract = contractService.findById(order.contractId());
            if(order.isType(OrderType.INSTALLMENT_PAYMENT)) {
                order.setValue(contract.installmentValue());
            }
        }
    }

    private void processAdhesionWhenRequired(Order order) {
        if(order.isType(OrderType.ADHESION)) {
            order.setContract(null);
            order.setValue(order.productInstallmentValue());
        }
    }

    private void checkContractorRules(Order order) {
        Optional<Contractor> contractor = contractorService.getByDocument(order.documentNumber());
        contractService.findByContractorAndProduct(order.documentNumber(),order.productCode());
        if(order.isType(OrderType.ADHESION) && contractor.isPresent()){
            throw UnovationExceptions.conflict().withErrors(EXISTING_CONTRACTOR);
        }
        List<PaymentInstrument> instruments = paymentInstrumentService.findByContractorDocument(order.documentNumber());
        if (!contractor.isPresent()) {
            order.setPaymentInstrument(null);
        }
        if(order.isType(OrderType.CREDIT)) {
            contractor.ifPresent(contractor1 -> checkCreditRules(order, instruments));
        }
        contractor.ifPresent(c -> order.setContract(contractService.findById(order.contractId())));
    }

    private void checkCreditRules(Order order, List<PaymentInstrument> instruments) {
        if(order.getValue() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(VALUE_REQUIRED);
        }
        if (order.getPaymentInstrument() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_INSTRUMENT_REQUIRED);
        }
        Optional<PaymentInstrument> instrumentOptional = instruments.stream()
                                    .filter(instrument -> instrument.equals(order.getPaymentInstrument())).findFirst();
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

    public Page<Order> findByFilter(OrderFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    public List<Order> findAll(){
        return repository.findAllByOrderByCreateDateTimeDesc();
    }
}
