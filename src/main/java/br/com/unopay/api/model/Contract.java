package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
    @Size(min=1, max = 4, groups = {Create.class, Update.class})
    private String code;

    @Column(name="name")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    @Size(min=2, max = 100, groups = {Create.class, Update.class})
    private String name;

    @Valid
    @ManyToOne
    @JoinColumn(name="product_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Product product;

    @Valid
    @ManyToOne
    @JoinColumn(name="hirer_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Hirer hirer;

    @Valid
    @ManyToOne
    @NotNull(groups = {Create.class})
    @JoinColumn(name="contractor_id")
    private Contractor contractor;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_instrument_type")
    private PaymentInstrumentType paymentInstrumentType;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ServiceType.class)
    @JsonView({Views.Public.class,Views.List.class})
    @CollectionTable(name = "contract_service", joinColumns = @JoinColumn(name = "contract_id"))
    private List<ServiceType> serviceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_insertion_type")
    private CreditInsertionType creditInsertionType;

    @Column(name = "begin_date")
    @Temporal(TemporalType.DATE)
    private Date begin;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date end;

    @Column(name = "issue_invoice")
    private Boolean issueInvoice;

    @Column(name = "document_number_invoice")
    private String documentNumberInvoice;

    @Column(name = "situation")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    private ContractSituation situation;

    @Column(name="rntrc")
    @JsonView({Views.Public.class,Views.List.class})
    private String rntrc;

    @Version
    @JsonIgnore
    private Integer version;

    public void validate(){
    }

    public void updateMe(Contract contract) {
        code = contract.getCode();
        name = contract.getName();
        issueInvoice = contract.getIssueInvoice();

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
