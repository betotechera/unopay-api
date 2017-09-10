package br.com.unopay.api.order.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.infra.Notifier;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.order.model.CreditOrder;
import br.com.unopay.api.order.repository.CreditOrderRepository;
import br.com.unopay.api.service.PaymentInstrumentService;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.service.ProductService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.List;
import java.util.Optional;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.INSTRUMENT_IS_NOT_FOR_PRODUCT;
import static br.com.unopay.api.uaa.exception.Errors.INSTRUMENT_NOT_BELONGS_TO_CONTRACTOR;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_INSTRUMENT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_REQUEST_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PRODUCT_REQUIRED;

@Service
public class CreditOrderService {

    private CreditOrderRepository repository;
    private PersonService personService;
    private ProductService productService;
    private ContractorService contractorService;
    private PaymentInstrumentService paymentInstrumentService;
    @Setter private Notifier notifier;

    public CreditOrderService(){}

    @Autowired
    public CreditOrderService(CreditOrderRepository repository,
                              PersonService personService,
                              ProductService productService,
                              ContractorService contractorService,
                              PaymentInstrumentService paymentInstrumentService,
                              Notifier notifier){
        this.repository = repository;
        this.personService = personService;
        this.productService = productService;
        this.contractorService = contractorService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.notifier = notifier;
    }

    public CreditOrder save(CreditOrder creditOrder) {
        return repository.save(creditOrder);
    }

    public CreditOrder findById(String id) {
        return repository.findOne(id);
    }

    public CreditOrder create(CreditOrder order) {
        validateReferences(order);
        Optional<Person> person = personService.findByDocument(order.documentNumber());
        order.setPerson(person.orElseGet(()-> personService.save(order.getPerson())));
        incrementNumber(order);
        checkPaymentInstrument(order);
        CreditOrder created = repository.save(order);
        order.getPaymentRequest().setOrderId(order.getId());
        notifier.notify(Queues.UNOPAY_ORDER_CREATED, created);
        return created;
    }

    private void checkPaymentInstrument(CreditOrder order) {
        Optional<Contractor> contractor = contractorService.getByDocument(order.documentNumber());
        List<PaymentInstrument> instruments = paymentInstrumentService.findByContractorDocument(order.documentNumber());
        if(!contractor.isPresent()) order.setPaymentInstrument(null);
        if(contractor.isPresent()){
            if(order.getPaymentInstrument() == null){
                throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_INSTRUMENT_REQUIRED);
            }
            if(instruments.stream().noneMatch(instrument -> instrument.equals(order.getPaymentInstrument()))){
                throw UnovationExceptions.unprocessableEntity().withErrors(INSTRUMENT_NOT_BELONGS_TO_CONTRACTOR);
            }
            if(instruments.stream().noneMatch(instrument -> instrument.getProduct().equals(order.getProduct()))){
                throw UnovationExceptions.unprocessableEntity().withErrors(INSTRUMENT_IS_NOT_FOR_PRODUCT);
            }
        }
    }

    private void validateReferences(CreditOrder order) {
        if(order.getProduct() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(PRODUCT_REQUIRED);
        }
        if(order.getPaymentRequest() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_REQUEST_REQUIRED);
        }
        order.setProduct(productService.findById(order.getProduct().getId()));
    }

    private void incrementNumber(CreditOrder creditOrder) {
        Optional<CreditOrder> last = repository.findFirstByOrderByCreateDateTimeDesc();
        creditOrder.incrementNumber(last.map(CreditOrder::getNumber).orElse(null));
    }

    public List<CreditOrder> findAll(){
        return repository.findAllByOrderByCreateDateTimeDesc();
    }
}
