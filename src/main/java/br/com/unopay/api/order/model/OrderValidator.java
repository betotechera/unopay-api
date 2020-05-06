package br.com.unopay.api.order.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.billing.creditcard.model.PaymentRequest;
import br.com.unopay.api.billing.creditcard.service.UserCreditCardService;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.notification.engine.MailValidator;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.api.service.PaymentInstrumentService;
import br.com.unopay.api.service.ProductService;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.BadRequestException;
import br.com.unopay.bootcommons.exception.UnovationError;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.springframework.stereotype.Component;

import static br.com.unopay.api.order.model.OrderType.INSTALLMENT_PAYMENT;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.EXISTING_CONTRACTOR;
import static br.com.unopay.api.uaa.exception.Errors.INSTRUMENT_IS_NOT_FOR_PRODUCT;
import static br.com.unopay.api.uaa.exception.Errors.INSTRUMENT_NOT_BELONGS_TO_CONTRACTOR;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_INSTRUMENT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_REQUEST_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PRODUCT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.USER_ALREADY_EXISTS;
import static br.com.unopay.api.uaa.exception.Errors.VALUE_REQUIRED;

@Component
public class OrderValidator {

    private ProductService productService;
    private ContractorService contractorService;
    private ContractService contractService;
    private PaymentInstrumentService paymentInstrumentService;
    private UserDetailService userDetailService;
    private HirerService hirerService;
    private MailValidator mailValidator;
    private Validator validator;
    private UserCreditCardService userCreditCardService;

    public OrderValidator(ProductService productService,
                          ContractorService contractorService,
                          ContractService contractService,
                          PaymentInstrumentService paymentInstrumentService,
                          UserDetailService userDetailService,
                          HirerService hirerService,
                          MailValidator mailValidator,
                          Validator validator,
                          UserCreditCardService userCreditCardService) {
        this.productService = productService;
        this.contractorService = contractorService;
        this.contractService = contractService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.userDetailService = userDetailService;
        this.hirerService = hirerService;
        this.mailValidator = mailValidator;
        this.validator = validator;
        this.userCreditCardService = userCreditCardService;
    }

    public void checkCreditCardWhenRequired(UserDetail user, Order order) {
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

    public void validateReferences(Order order) {
        if(order.getPaymentRequest() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_REQUEST_REQUIRED);
        }
        if((order.isType(OrderType.INSTALLMENT_PAYMENT) ||
                order.isType(OrderType.CREDIT)) &&
                order.getContract() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(CONTRACT_REQUIRED);
        }
    }

    public void checkHirerWhenRequired(Order order) {
        if(order.isType(OrderType.ADHESION)) {
            if (order.hasHirer()) {
                order.setHirer(hirerService.findByDocumentNumber(order.hirerDocumentNumber()));
                return;
            }

            hirerService.findByDocumentNumber(order.issuerDocumentNumber());
        }
    }

    public void checkContractorRules(Order order) {
        Optional<Contractor> contractor = contractorService.getOptionalByDocument(order.getDocumentNumber());
        checkAdhesionWhenRequired(order);
        if (!contractor.isPresent() || order.isType(INSTALLMENT_PAYMENT)) {
            order.setPaymentInstrument(null);
        }
        if(order.isType(OrderType.CREDIT)) {
            contractor.ifPresent(it -> checkCreditRules(order));
        }
        if (!order.isType(OrderType.ADHESION) && contractor.isPresent()){
            order.setContract(contractService.findById(order.getContractId()));
        }
    }

    private void checkAdhesionWhenRequired(Order order) {
        Optional<UserDetail> existingUser = userDetailService.getByEmailOptional(order.getBillingMail());
        if(order.isType(OrderType.ADHESION)) {
            if (existingUser.isPresent() && order.mustCreateUser()) {
                throw UnovationExceptions.conflict().withErrors(USER_ALREADY_EXISTS);
            }
            if(order.hasBillingMail()) {
                this.mailValidator.check(order.getBillingMail());
            }
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

    public void validateProduct(Order order) {
        if(order.getProduct() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(PRODUCT_REQUIRED);
        }
        order.setProduct(productService.findById(order.getProduct().getId()));
    }

    public void validateProductForIssuer(Issuer issuer, Order order) {
        if(order.getProduct() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(PRODUCT_REQUIRED);
        }
        order.setProduct(productService.findByIdForIssuer(order.getProduct().getId(), issuer));
    }

    private Optional<PaymentInstrument> getFirstInstrument(Order order, List<PaymentInstrument> instruments) {
        return instruments.stream().filter(instrument -> order.hasPaymentInstrument(instrument.getId())).findFirst();
    }
}
