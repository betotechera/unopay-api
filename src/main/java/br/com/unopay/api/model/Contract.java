package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static br.com.unopay.api.model.ContractOrigin.UNOPAY;
import static br.com.unopay.api.model.ContractSituation.ACTIVE;

@Data
@Entity
@Table(name = "contract")
public class Contract implements Serializable {

    public static final long serialVersionUID = 1L;

    public Contract(){}

    @Id
    @Column(name="id")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

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
    @JsonView({Views.Public.class})
    private Product product;

    @ManyToOne
    @JoinColumn(name="hirer_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Public.class})
    private Hirer hirer;

    @ManyToOne
    @NotNull(groups = {Create.class})
    @JoinColumn(name="contractor_id")
    @JsonView({Views.Public.class})
    private Contractor contractor;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_instrument_type")
    @JsonView({Views.Public.class,Views.List.class})
    private PaymentInstrumentType paymentInstrumentType;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ServiceType.class)
    @JsonView({Views.Public.class})
    @CollectionTable(name = "contract_service_type", joinColumns = @JoinColumn(name = "contract_id"))
    private List<ServiceType> serviceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_insertion_type")
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
    @JsonView({Views.Public.class,Views.List.class})
    private boolean issueInvoice;

    @Column(name = "document_number_invoice")
    @JsonView({Views.Public.class,Views.List.class})
    private String documentNumberInvoice;

    @Column(name = "situation")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private ContractSituation situation = ACTIVE;

    @Column(name = "origin")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private ContractOrigin origin = UNOPAY;


    @Column(name="rntrc")
    @NotNull(groups = {Create.class,Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    @Size(max = 20, groups = {Create.class, Update.class})
    private String rntrc;

    @Version
    @JsonIgnore
    private Integer version;

    public void validate(){
        if(begin != null && end != null && begin.after(end)){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.CONTRACT_END_IS_BEFORE_BEGIN);
        }
        if(product != null &&!allServicesContainsInProduct()){
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(Errors.CONTRACT_SERVICES_NOT_IN_PRODUCT_SERVICES);
        }
    }

    private boolean allServicesContainsInProduct() {
        return this.serviceType.stream().allMatch(s -> product.containsServiceType(s));
    }

    public void checkDocumentNumberInvoice() {
        documentNumberInvoice = (documentNumberInvoice == null) ? hirer.getDocumentNumber(): documentNumberInvoice;
    }

    public void updateMe(Contract contract) {
        code = contract.getCode();
        name = contract.getName();
        rntrc = contract.getRntrc();
        situation = contract.getSituation();
        issueInvoice = contract.isIssueInvoice();

        if(contract.getProduct() != null && contract.getProduct().getId() != null) {
            product = contract.getProduct();
        }
        if(contract.getHirer() != null && contract.getHirer().getId() != null) {
            hirer = contract.getHirer();
        }
        if(contract.getContractor() != null && contract.getContractor().getId() != null) {
            contractor = contract.getContractor();
        }
    }
}
