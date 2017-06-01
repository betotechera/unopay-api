package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.ServiceType;
import static br.com.unopay.api.model.ContractOrigin.UNOPAY;
import static br.com.unopay.api.model.ContractSituation.ACTIVE;
import static br.com.unopay.api.uaa.exception.Errors.INVALID_CONTRACTOR;

import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "contract")
@EqualsAndHashCode(exclude = {"contractEstablishments", "establishments"})
public class Contract implements Serializable {

    public static final long serialVersionUID = 1L;

    public Contract(){}

    @Id
    @Column(name="id")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @JsonView({Views.Public.class})
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "contract")
    private Set<ContractEstablishment> contractEstablishments;

    @BatchSize(size = 10)
    @OneToMany(fetch = FetchType.EAGER)
    @JsonView({Views.Public.class,Views.List.class})
    @JoinTable(name = "contract_establishment",
            joinColumns = { @JoinColumn(name = "contract_id") },
            inverseJoinColumns = { @JoinColumn(name = "establishment_id") })
    private List<Establishment> establishments;

    @Column(name="code")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Integer code;

    @Column(name="name")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    @Size(min=2, max = 100, groups = {Create.class, Update.class})
    private String name;

    @ManyToOne
    @JoinColumn(name="product_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Product product;

    @ManyToOne
    @JoinColumn(name="hirer_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Hirer hirer;

    @ManyToOne
    @NotNull(groups = {Create.class})
    @JoinColumn(name="contractor_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Contractor contractor;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_instrument_type")
    @JsonView({Views.Public.class,Views.List.class})
    private PaymentInstrumentType paymentInstrumentType;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER, targetClass = ServiceType.class)
    @JsonView({Views.Public.class,Views.List.class})
    @CollectionTable(name = "contract_service_type", joinColumns = @JoinColumn(name = "contract_id"))
    private Set<ServiceType> serviceType;

    @Column(name = "credit_insertion_type")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Public.class,Views.List.class})
    private CreditInsertionType creditInsertionType;

    @Column(name = "begin_date")
    @Temporal(TemporalType.DATE)
    @JsonView({Views.Public.class,Views.List.class})
    private Date begin;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    @JsonView({Views.Public.class,Views.List.class})
    private Date end;

    @Column(name = "issue_invoice")
    @JsonView({Views.Public.class})
    private boolean issueInvoice;

    @Column(name = "document_number_invoice")
    @JsonView({Views.Public.class})
    private String documentNumberInvoice;

    @Column(name = "situation")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private ContractSituation situation = ACTIVE;

    @Column(name = "origin")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Public.class})
    private ContractOrigin origin = UNOPAY;

    @Column(name="rntrc")
    @NotNull(groups = {Create.class,Update.class})
    @JsonView({Views.Public.class})
    @Size(max = 20, groups = {Create.class, Update.class})
    private String rntrc;

    @Version
    @JsonIgnore
    private Integer version;

    public void validate(){
        if(begin != null && end != null && begin.after(end)){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.CONTRACT_END_IS_BEFORE_BEGIN);
        }
        this.product.validateCreditInsertionType(this.creditInsertionType);

    }

    public void validateActive(){
        if(!isActive()){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.CONTRACT_NOT_ACTIVATED);
        }
         if(!inProgress()){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.CONTRACT_NOT_IN_PROGRESS);
        }
    }

    private boolean isActive() {
        return ContractSituation.ACTIVE.equals(situation);
    }

    public boolean inProgress(){
        return end.after(new Date()) && begin.before(new Date());
    }

    public void checkFields() {
        documentNumberInvoice = (documentNumberInvoice == null) ? hirer.getDocumentNumber(): documentNumberInvoice;
        origin = (origin == null) ? UNOPAY : origin;
        situation = (situation == null) ? ACTIVE : situation;
    }

    public void updateMe(Contract contract) {
        name = contract.getName();
        rntrc = contract.getRntrc();
        situation = contract.getSituation();
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

    public String getHirerDocumentNumber(){
        if(getHirer() != null){
            return  getHirer().getDocumentNumber();
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
        return getServiceType().stream().anyMatch(t -> t == serviceType);
    }

    public BigDecimal productInstrumentIssuerFee(){
        if(getProduct() !=null){
            return getProduct().getPaymentInstrumentEmissionFee();
        }
        return null;
    }

    public boolean withEstablishmentRestriction(){
        return getEstablishments() != null && !getEstablishments().isEmpty();
    }


    public boolean containsContractor(Contractor contractor) {
        return Objects.equals(this.contractor.getDocumentNumber(), contractor.getDocumentNumber());
    }

    public void validateContractor(Contractor contractor){
        if(!containsContractor(contractor)){
            throw UnovationExceptions.unprocessableEntity().withErrors(INVALID_CONTRACTOR);
        }
    }

    public boolean validToEstablishment(String establishmentId){
        return meetsEstablishmentRestrictions(establishmentId) && inProgress() && isActive();
    }

    public boolean meetsEstablishmentRestrictions(String establishmentId) {
        return !withEstablishmentRestriction() || containsEstablishment(establishmentId);
    }

    public boolean containsEstablishment(String establishmentId) {
       return establishments.stream()
                .filter(e -> Objects.equals(e.getId(),establishmentId))
                .reduce((first, last) -> last).isPresent();
    }
}
