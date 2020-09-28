package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.billing.creditcard.model.PaymentMethod;
import br.com.unopay.api.network.model.AccreditedNetwork;
import br.com.unopay.api.network.model.Establishment;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.network.model.ServiceType;
import br.com.unopay.api.credit.model.CreditInsertionType;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.util.Time;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.Comparator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static br.com.unopay.api.model.ContractOrigin.APPLICATION;
import static br.com.unopay.api.model.ContractSituation.ACTIVE;
import static br.com.unopay.api.uaa.exception.Errors.INVALID_CONTRACTOR;
import static br.com.unopay.api.uaa.exception.Errors.PRODUCT_REQUIRED;

@Data
@Entity
@Table(name = "contract")
@EqualsAndHashCode(exclude = {"contractEstablishments", "establishments", "contractInstallments"})
public class Contract implements Serializable {

    public static final long serialVersionUID = 1L;

    public Contract(){}

    public Contract(Product product){
        this(product, 0);
    }

    public Contract(Product product, Integer memberTotal){
        this.product = product;
        this.memberTotal = memberTotal;
        this.creditInsertionTypes = Collections.unmodifiableSet(product.getCreditInsertionTypes());
        this.code = Long.valueOf(RandomStringUtils.randomNumeric(10));
        this.name = product.getName();
        this.paymentInstrumentType = PaymentInstrumentType.DIGITAL_WALLET;
        this.serviceTypes = Collections.unmodifiableSet(product.getServiceTypes());
        this.begin = Time.create();
        this.end = Time.createDateTime().plusYears(1).toDate();
    }

    @Id
    @Column(name="id")
    @NotNull(groups = {Reference.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @JsonView({Views.Contract.Establishment.class})
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "contract")
    private Set<ContractEstablishment> contractEstablishments;

    @BatchSize(size = 10)
    @OneToMany(fetch = FetchType.EAGER)
    @JsonView({Views.Contract.Establishment.class})
    @JoinTable(name = "contract_establishment",
            joinColumns = { @JoinColumn(name = "contract_id") },
            inverseJoinColumns = { @JoinColumn(name = "establishment_id") })
    private List<Establishment> establishments;

    @Column(name="code")
    @NotNull(groups = {Create.class, Update.class})
    private Long code;

    @Column(name="name")
    @NotNull(groups = {Create.class, Update.class})
    @Size(min=2, max = 100, groups = {Create.class, Update.class})
    private String name;

    @ManyToOne
    @JoinColumn(name="product_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Contract.List.class, Views.ServiceAuthorize.List.class, Views.AuthorizedMember.Detail.class})
    private Product product;

    @ManyToOne
    @JoinColumn(name="hirer_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Contract.List.class, Views.AuthorizedMember.List.class})
    private Hirer hirer;

    @ManyToOne
    @NotNull(groups = {Create.class})
    @JoinColumn(name="contractor_id")
    @JsonView({Views.Contract.List.class, Views.AuthorizedMember.List.class})
    private Contractor contractor;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_instrument_type")
    @JsonView(Views.Contract.List.class)
    private PaymentInstrumentType paymentInstrumentType;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER, targetClass = ServiceType.class)
    @Column(name = "service_type", nullable = false)
    @JsonView({Views.Contract.Detail.class})
    @CollectionTable(name = "contract_service_type", joinColumns = @JoinColumn(name = "contract_id"))
    private Set<ServiceType> serviceTypes;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER, targetClass = CreditInsertionType.class)
    @Column(name = "credit_insertion_type", nullable = false)
    @JsonView({Views.Contract.List.class})
    @CollectionTable(name = "contract_credit_insertion_type", joinColumns = @JoinColumn(name = "contract_id"))
    private Set<CreditInsertionType> creditInsertionTypes;


    @Column(name = "begin_date")
    @JsonView({Views.Contract.Detail.class})
    private Date begin;

    @Column(name = "end_date")
    @JsonView({Views.Contract.Detail.class})
    private Date end;

    @Column(name = "issue_invoice")
    @JsonView({Views.Contract.Detail.class})
    private boolean issueInvoice;

    @Column(name = "document_number_invoice")
    @JsonView({Views.Contract.Detail.class})
    private String documentNumberInvoice;

    @Column(name = "situation")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView(Views.Contract.List.class)
    private ContractSituation situation = ACTIVE;

    @Column(name = "origin")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Contract.Detail.class})
    private ContractOrigin origin =  APPLICATION;

    @Column(name = "annuity")
    @JsonView({Views.Contract.Detail.class})
    private BigDecimal annuity;

    @Column(name = "member_annuity")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Product.Detail.class, Views.Product.List.class})
    private BigDecimal memberAnnuity;

    @Column(name = "member_total")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Product.Detail.class, Views.Product.List.class})
    private Integer memberTotal = 0;

    @Column(name = "membership_fee")
    @JsonView({Views.Contract.Detail.class})
    private BigDecimal membershipFee;

    @Column(name = "payment_installments")
    @JsonView({Views.Contract.Detail.class})
    private Integer paymentInstallments;

    @JsonManagedReference
    @JsonView({Views.Contract.Establishment.class})
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "contract")
    private Set<ContractInstallment> contractInstallments;

    @Column(name = "created_date_time")
    @JsonView({Views.ContractorInstrumentCredit.List.class})
    private Date createdDateTime;

    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @Column(name = "recurrence_payment_method")
    @JsonView({Views.Contract.Detail.class})
    private PaymentMethod recurrencePaymentMethod;

    @JsonIgnore
    @Version
    private Integer version;

    public void validate(){
        if(begin != null && end != null && begin.after(end)){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.CONTRACT_END_IS_BEFORE_BEGIN);
        }
        if(this.product.getId() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(PRODUCT_REQUIRED);
        }
        this.product.validateCreditInsertionType(this.creditInsertionTypes);

    }

    public void validateActive(){
        if(!active()){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.CONTRACT_NOT_ACTIVATED);
        }
        if(!inProgress()){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.CONTRACT_NOT_IN_PROGRESS);
        }
    }

    public boolean active() {
        return ContractSituation.ACTIVE.equals(situation);
    }

    public boolean inProgress(){
        if(begin != null && begin.after(new Date())) {
            return false;
        }
        if(end != null && end.before(new Date())) {
            return false;
        }

        return true;
    }

    public void checkFields() {
        documentNumberInvoice = (documentNumberInvoice == null) ? hirer.getDocumentNumber(): documentNumberInvoice;
        origin = (origin == null) ? APPLICATION : origin;
        situation = (situation == null) ? ACTIVE : situation;
    }

    public void updateMe(Contract contract) {
        name = contract.getName();
        if(contract.getSituation() != ContractSituation.CANCELLED) {
            situation = contract.getSituation();
        }
        issueInvoice = contract.isIssueInvoice();
        if(contract.getDocumentNumberInvoice() != null) {
            documentNumberInvoice = contract.getDocumentNumberInvoice();
        }
    }

    public void addContractEstablishment(ContractEstablishment contractEstablishment) {
        if(contractEstablishment == null){
            contractEstablishments = new HashSet<>();
        }
        contractEstablishments.add(contractEstablishment);
    }

    public void removeContractEstablishmentBy(String contractEstablishmentId) {
        if(contractEstablishments != null){
            contractEstablishments.removeIf(item -> item.getId().equals(contractEstablishmentId));
        }
    }

    public boolean containsEstablishment(ContractEstablishment contractEstablishment) {
        return contractEstablishments != null &&
                contractEstablishments.stream().filter(item ->
                        item.getEstablishment().getId().equals(contractEstablishment.getEstablishmentId()))
                        .count() > 0;
    }

    public String hirerDocumentNumber(){
        if(getHirer() != null){
            return  getHirer().getDocumentNumber();
        }
        return null;
    }

    public String contractorDocumentNumber(){
        if(getContractor() != null){
            return  getContractor().getDocumentNumber();
        }
        return null;
    }

    public String productNetworkId(){
        if(getProduct() != null){
            return  getProduct().networkId();
        }
        return null;
    }

    public AccreditedNetwork productNetwork(){
        if(getProduct() != null){
            return  getProduct().getAccreditedNetwork();
        }
        return null;
    }

    public String productCode(){
        if(getProduct() != null){
            return  getProduct().getCode();
        }
        return null;
    }

    public String hirerId(){
        if(getHirer() != null){
            return  getHirer().getId();
        }
        return null;
    }

    public String contractorId(){
        if(getContractor() != null){
            return  getContractor().getId();
        }
        return null;
    }

    public Product returnProduct() {
        if (getProduct() != null) {
            return getProduct();
        }
        return null;
    }

    public boolean isProductCodeEquals(String code){
        if(getProduct() != null){
            return Objects.equals(getProduct().getCode(), code);
        }
        return false;
    }

    public boolean containsService(ServiceType serviceType){
        return serviceTypes.stream().anyMatch(t -> t == serviceType);
    }

    public BigDecimal productCreditInsertFee(){
        if(getProduct() !=null){
            return getProduct().getCreditInsertionFee();
        }
        return null;
    }

    public String productId(){
        if(getProduct() != null){
            return getProduct().getId();
        }
        return null;
    }

    public boolean containsContractor(Contractor contractor) {
        return Objects.equals(this.contractor.getId(), contractor.getId());
    }

    public boolean valid(){
        return inProgress() && active();
    }

    public void checkValidFor(Contractor contractor){
        validateActive();
        validateContractor(contractor);
    }

    public void validateContractor(Contractor contractor){
        if(!containsContractor(contractor)){
            throw UnovationExceptions.unprocessableEntity().withErrors(INVALID_CONTRACTOR);
        }
    }

    public void setupMeUp() {
        this.annuity = product.getAnnuity();
        this.memberAnnuity = product.getMemberAnnuity();
        this.membershipFee = product.getMembershipFee();
        this.paymentInstallments = product.getPaymentInstallments();
        this.createdDateTime = new Date();
    }

    public BigDecimal installmentValue(){
        return getContractInstallments().stream()
                .filter(i -> i.getPaymentValue() == null)
                .min(Comparator.comparing(ContractInstallment::getInstallmentNumber))
                .map(ContractInstallment::getValue).orElse(null);
    }

    public boolean withMembershipFee() {
        return this.membershipFee != null && this.membershipFee.compareTo(BigDecimal.ZERO) == 1;
    }

    public boolean containsHirer(Hirer hirer) {
        if(getHirer() != null && hirer != null){
            return Objects.equals(getHirer().getId(), hirer.getId());
        }
        return false;
    }

    public boolean hirerDocumentEquals(String hirerDocument) {
        return hirerDocumentNumber().equals(hirerDocument);
    }

    public BigDecimal annuityTotal() {
        return this.getMemberAnnuity() == null || this.memberTotal <= 0 ? this.annuity :
                this.annuity.add(this.getMemberAnnuity().multiply(new BigDecimal(this.memberTotal)));
    }

    public boolean withIssuerAsHirer() {
        return this.product.getIssuer().documentNumber().equals(this.hirerDocumentNumber());
    }


    public boolean canAuthorizeServiceWithoutContractorPassword() {
        Boolean canAuthorize = product.getIssuer().getAuthorizeServiceWithoutContractorPassword();
        return  canAuthorize != null ? canAuthorize : false;
    }

    public Person contractorPerson() {
        return contractor.getPerson();
    }

    public String getId() {
        return id;
    }
}

