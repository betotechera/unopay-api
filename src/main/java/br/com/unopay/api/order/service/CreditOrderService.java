package br.com.unopay.api.order.service;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.order.model.CreditOrder;
import br.com.unopay.api.order.repository.CreditOrderRepository;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.service.ProductService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_REQUEST_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PRODUCT_REQUIRED;

@Service
public class CreditOrderService {

    private CreditOrderRepository repository;
    private PersonService personService;
    private ProductService productService;
    public CreditOrderService(){}

    @Autowired
    public CreditOrderService(CreditOrderRepository repository,
                              PersonService personService,
                              ProductService productService){
        this.repository = repository;
        this.personService = personService;
        this.productService = productService;
    }

    public CreditOrder save(CreditOrder creditOrder) {
        return repository.save(creditOrder);
    }

    public CreditOrder findById(String id) {
        return repository.findOne(id);
    }

    public CreditOrder create(CreditOrder order) {
        validateReferences(order);
        Optional<Person> person = personService.findByIdOptional(order.getPerson().getId());
        order.setPerson(person.orElseGet(()-> personService.save(order.getPerson())));
        return repository.save(order);
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
}
