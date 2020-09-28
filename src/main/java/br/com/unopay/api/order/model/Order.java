package br.com.unopay.api.order.model;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.billing.boleto.model.TicketPaymentSource;
import br.com.unopay.api.billing.creditcard.model.CreditCard;
import br.com.unopay.api.billing.creditcard.model.PaymentMethod;
import br.com.unopay.api.billing.creditcard.model.PaymentRequest;
import br.com.unopay.api.billing.creditcard.model.TransactionStatus;
import br.com.unopay.api.market.model.AuthorizedMemberCandidate;
import br.com.unopay.api.model.Billable;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.exception.UnprocessableEntityException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.billing.creditcard.model.TransactionStatus.CANCELED;
import static br.com.unopay.api.billing.creditcard.model.TransactionStatus.CANCEL_PENDING;
import static br.com.unopay.api.billing.creditcard.model.TransactionStatus.CAPTURED;
import static br.com.unopay.api.billing.creditcard.model.TransactionStatus.CAPTURE_RECEIVED;
import static br.com.unopay.api.billing.creditcard.model.TransactionStatus.DENIED;
import static br.com.unopay.api.billing.creditcard.model.TransactionStatus.REFUND;
import static br.com.unopay.api.uaa.exception.Errors.PERSON_EMAIL_IS_REQUIRED;

@Data
@Entity
@Table(name = "\"order\"")
@ToString(exclude = { "product", "candidates"})
@EqualsAndHashCode(of = {"id"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order implements Updatable, Billable, Serializable {

    private static final long serialVersionUID = 2732233885546623588L;

    public Order() {}

    public Order(Person person, Product product, Boolean createUser) {
        this.person = person;
        this.product = product;
        this.createUser = createUser;
    }

    public Order(Person person, Product product, Boolean createUser, String userPassword) {
        this.person = person;
        this.product = product;
        this.createUser = createUser;
        this.userPassword = userPassword;
    }


    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @JsonView({Views.Order.List.class})
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @ManyToOne
    @NotNull(groups = {Reference.class, Create.Order.Adhesion.class})
    @JoinColumn(name = "product_id")
    @JsonView({Views.Order.List.class})
    private Product product;

    @Valid
    @ManyToOne
    @JoinColumn(name = "person_id")
    @JsonView({Views.Order.List.class})
    @NotNull(groups = {Create.Order.Adhesion.class, Update.class})
    private Person person;

    @ManyToOne
    @JoinColumn(name = "payment_instrument_id")
    @JsonView({Views.Order.Detail.class})
    private PaymentInstrument paymentInstrument;

    @Column(name = "order_number")
    @JsonView({Views.Order.List.class})
    private String number;

    @JsonView({Views.Order.Private.class})
    @Column(name = "partner_id")
    private String partnerId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Order.Detail.class, Views.Order.List.class})
    private PaymentStatus status = PaymentStatus.WAITING_PAYMENT;

    @Column(name = "value")
    @JsonView({Views.Order.Detail.class, Views.Order.List.class})
    private BigDecimal value;

    @Column(name = "fee")
    private BigDecimal fee = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    @JsonView({Views.Order.Detail.class})
    private Contract contract;

    @Column(name = "create_date_time")
    @JsonView({Views.Order.List.class})
    private Date createDateTime;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Order.Detail.class, Views.Order.List.class})
    private OrderType type;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Set<AuthorizedMemberCandidate> candidates = new HashSet<>();

    @Column(name = "create_user")
    @JsonView({Views.Order.Detail.class})
    private Boolean createUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    @JsonView({Views.Order.Detail.class})
    private PaymentMethod paymentMethod;

    @Transient
    private PaymentRequest paymentRequest;

    @Column(name = "user_password")
    @JsonView({Views.Order.Detail.class})
    private String userPassword;

    @Valid
    @Embedded
    @JsonView({Views.Order.Detail.class})
    private RecurrencePaymentInformation recurrencePaymentInformation;

    @ManyToOne
    @JoinColumn(name = "hirer_id")
    @JsonView({Views.Order.List.class})
    private Hirer hirer;

    @JsonIgnore
    @Version
    private Integer version;

    public void validateUpdate() {
        if (this.status == PaymentStatus.CANCELED)
            throw UnovationExceptions.unauthorized().withErrors(Errors.UNABLE_TO_UPDATE_ORDER_STATUS);
    }

    public void definePaymentValue(BigDecimal value){
        if(getPaymentRequest() != null) {
            getPaymentRequest().setValue(value);
        }
    }

    public Order defineCardToken(String token){
        if(getPaymentRequest() == null){
            setPaymentRequest(new PaymentRequest());
        }
        if(getPaymentRequest().getCreditCard() != null) {
            getPaymentRequest().setMethod(PaymentMethod.CARD);
            getPaymentRequest().getCreditCard().setToken(token);
            return this;
        }
        if(getPaymentRequest().getCreditCard() == null) {
            getPaymentRequest().setMethod(PaymentMethod.CARD);
            getPaymentRequest().setCreditCard(new CreditCard());
            getPaymentRequest().getCreditCard().setToken(token);
        }
        if(getRecurrencePaymentInformation() != null){
            getRecurrencePaymentInformation().setCreditCardToken(token);
        }
        return this;
    }

    public void defineStatus(TransactionStatus transactionStatus) {
        if (Arrays.asList(CANCELED, CANCEL_PENDING, REFUND).contains(transactionStatus)) {
            this.status = PaymentStatus.CANCELED;
            return;
        }
        if (Arrays.asList(CAPTURED, CAPTURE_RECEIVED).contains(transactionStatus)) {
            this.status = PaymentStatus.PAID;
            return;
        }
        if (DENIED.equals(transactionStatus)) {
            this.status = PaymentStatus.PAYMENT_DENIED;
            return;
        }
        this.status = PaymentStatus.WAITING_PAYMENT;

    }

    public boolean hasPaymentInstrument(String id) {
        if (id != null && getPaymentInstrument() != null) {
            return id.equals(getPaymentInstrument().getId());
        }
        return false;
    }

    public boolean paid() {
        return PaymentStatus.PAID.equals(status);
    }

    @JsonIgnore
    public String getDocumentNumber() {
        if (this.person != null) {
            return person.getDocument().getNumber();
        }
        return null;
    }

    @JsonIgnore
    public String getProductId() {
        if (this.product != null) {
            return product.getId();
        }
        return null;
    }

    public boolean mustCreateUser(){
        return hasBillingMail() || (getCreateUser() == null || getCreateUser());
    }

    @JsonIgnore
    public String getContractId() {
        if (this.contract != null) {
            return contract.getId();
        }
        return null;
    }

    @JsonIgnore
    public String getProductCode() {
        if (this.product != null) {
            return product.getCode();
        }
        return null;
    }

    public String instrumentId() {
        if (this.getPaymentInstrument() != null) {
            return this.getPaymentInstrument().getId();
        }
        return null;
    }

    public boolean is(PaymentMethod method) {
        if(paymentRequest != null && paymentRequest.getMethod() != null) {
            return paymentRequest.getMethod().equals(method);
        }
        if(paymentMethod != null) {
            return paymentMethod.equals(method);
        }
        if (recurrencePaymentInformation != null && recurrencePaymentInformation.getPaymentMethod() != null){
            return recurrencePaymentInformation.getPaymentMethod().equals(method);
        }
        return false;
    }

    public boolean isType(OrderType type) {
        return this.type.equals(type);
    }

    @JsonProperty
    public BigDecimal getProductInstallmentValue() {
        if (getProduct() != null) {
            return getProduct().getInstallmentValue();
        }
        return null;
    }

    public String personEmail() {
        if (getPerson() != null){
            return getPerson().getPhysicalPersonEmail();
        }
        return null;
    }

    @JsonProperty
    public BigDecimal getProductInstallmentTotal(Integer memberTotal) {
        if (getProduct() != null) {
            return getProduct().installmentTotal(memberTotal);
        }
        return null;
    }

    public void setMeUp() {
        checkType();
        setCreateDateTime(new Date());
        if (!isType(OrderType.ADHESION)) {
            setCandidates(new HashSet<>());
        }
        if(shouldApplyFee()){
            this.fee = getIssuer().getCreditCardFee();
        }else{
            this.fee = BigDecimal.ZERO;
        }
        if(hasPaymentRequest()) {
            this.paymentMethod = this.paymentRequest.getMethod();
        }
        this.status = PaymentStatus.WAITING_PAYMENT;

        if(isType(OrderType.ADHESION)){
            if(this.paymentRequest != null &&
               this.paymentRequest.isMethod(PaymentMethod.CARD)){
                this.recurrencePaymentInformation = this.paymentRequest.toRecurrencePaymentInformation();
                return;
            }
            this.recurrencePaymentInformation = new RecurrencePaymentInformation();
            this.recurrencePaymentInformation.setPaymentMethod(PaymentMethod.BOLETO);
        }

    }

    private boolean shouldApplyFee() {
        return type.shouldApplyFee() &&
                this.is(PaymentMethod.CARD) &&
                (getIssuer() != null && getIssuer().getCreditCardFee() != null);
    }

    public void validateMe() {
        checkType();
        setCreateDateTime(new Date());
        if (isType(OrderType.ADHESION)) {
            checkCandidates();
            checkProduct();
            checkPaymentRecurrenceInformation(recurrencePaymentInformation.isCardPayment(), "the payment method is card");
            if(personEmail() == null){
                throw UnovationExceptions.unprocessableEntity().withErrors(PERSON_EMAIL_IS_REQUIRED);
            }
        }
    }

    private void checkPaymentRecurrenceInformation(boolean cardPayment, String s) {
        if (cardPayment &&
                !recurrencePaymentInformation.isValid()) {
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(Errors.RECURRENCE_PAYMENT_INFORMATION_REQUIRED
                            .withOnlyArguments(s, "all the following fields are required", recurrencePaymentInformation.fieldsStatus()));
        }
    }

    private void checkProduct() {
        if(this.product == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.PRODUCT_REQUIRED);
        }
        checkPaymentRecurrenceInformation(this.product.acceptOnlyCard(), "the product accepts only card");
    }

    private void checkCandidates() {
        if(this.candidates!= null) {
            this.candidates.forEach(candidate -> {
                candidate.validateMe();
                candidate.setMeUp();
            });
        }
    }

    private void checkType() {
        if(this.type == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.TYPE_REQUIRED);
        }
    }

    @JsonIgnore
    public String getBillingMail() {
        if (this.person != null && this.person.getPhysicalPersonDetail() != null) {
            return this.person.getPhysicalPersonDetail().getEmail();
        }
        return null;
    }

    public void checkAlreadyPaid() {
        if(paid()){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.ALREADY_PAID_ORDER);
        }
    }

    @Override
    public TicketPaymentSource getPaymentSource() {
        return TicketPaymentSource.CONTRACTOR_CREDIT;
    }

    public void normalize() {
        if (this.person != null) {
            this.person.normalize();
        }
        if (this.paymentRequest != null && this.paymentRequest.getCreditCard() != null) {
            this.paymentRequest.getCreditCard().normalize();
        }
    }

    public String issuerDocumentNumber() {
        if (this.getProduct() != null && this.getProduct().getIssuer() != null) {
            return getProduct().getIssuer().documentNumber();
        }
        return null;
    }

    public String hirerDocumentNumber() {
        if (hasHirer()) {
            return getHirer().getDocumentNumber();
        }
        return null;
    }

    public boolean productWithMembershipFee() {
        return this.product != null && this.product.getMembershipFee() != null
                && this.product.getMembershipFee().compareTo(BigDecimal.ZERO) != 0;
    }

    public BigDecimal getProductMembershipFee() {
        if (this.product != null) {
            return this.product.getMembershipFee();
        }
        return null;
    }

    @Override
    public Person getPayer() {
        return this.getPerson();
    }

    @Override
    public Issuer getIssuer() {
        if (this.getProduct() != null) {
            return this.getProduct().getIssuer();
        }
        return null;
    }

    public boolean hasPaymentRequest() {
        return paymentRequest != null;
    }

    public boolean hasHirer() {
        return hirer != null;
    }

    public boolean shouldStoreCard() {
        return (hasPaymentRequest()
                && paymentRequest.shouldStoreCard()) ||
                (PaymentMethod.CARD.equals(paymentMethod) && isType(OrderType.ADHESION));
    }

    public boolean hasCardToken() {
        return hasCard() &&
                getPaymentRequest().getCreditCard().getToken() != null;
    }

    public boolean hasCard() {
        return hasPaymentRequest() &&
                getPaymentRequest().getCreditCard() != null;
    }

    public String creditCardToken() {
        if (hasPaymentRequest() && getPaymentRequest().getCreditCard() != null) {
            return getPaymentRequest().getCreditCard().getToken();
        }
        return null;
    }

    public CreditCard creditCard() {
        if (hasPaymentRequest()) {
            return getPaymentRequest().getCreditCard();
        }
        return null;
    }

    public BigDecimal paymentValue() {
        return value.add(fee);
    }

    public Integer candidatesSize(){
        if(this.candidates != null){
            return this.candidates.size();
        }
        return 0;
    }

    public void defineRecurrencePaymentMethod(PaymentMethod recurrencePaymentMethod) {
        if(this.recurrencePaymentInformation == null){
            this.recurrencePaymentInformation = new RecurrencePaymentInformation();
        }
        this.recurrencePaymentInformation.setPaymentMethod(recurrencePaymentMethod);
    }

    public PaymentMethod getRecurrencePaymentMethod() {
        if(recurrencePaymentInformation != null){
            return this.recurrencePaymentInformation.getPaymentMethod();
        }
        return null;
    }

    public String getRecurrenceCreditCardToken() {
        if(recurrencePaymentInformation != null){
            return this.recurrencePaymentInformation.getCreditCardToken();
        }
        if(this.hasCard()){
            return creditCardToken();
        }
        return null;
    }

    public Order definePaymentIssuer(String issuerDocumentNumber) {
        if(this.paymentRequest != null){
            this.paymentRequest.setIssuerDocument(issuerDocumentNumber);
        }
        return this;
    }

    public Order definePaymentMethod(PaymentMethod method) {
        this.paymentMethod = method;
        if(hasPaymentRequest()){
            this.paymentRequest.setMethod(method);
        }
        if(hasPaymentInformation()){
            this.getRecurrencePaymentInformation().setPaymentMethod(method);
        }
        return this;
    }

    private boolean hasPaymentInformation() {
        return this.recurrencePaymentInformation != null;
    }
}