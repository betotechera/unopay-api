package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.uaa.exception.Errors.BANK_ACCOUNT_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.BANK_ACCOUNT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.CANNOT_CHANGE_HEAD_OFFICE;
import static br.com.unopay.api.uaa.exception.Errors.HEAD_OFFICE_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PERSON_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PERSON_REQUIRED;
import static javax.persistence.EnumType.STRING;

@Data
@Entity
@Table(name = "branch")
public class Branch implements Serializable {

    public static final long serialVersionUID = 1L;

    public Branch(){}

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @Column(name="id")
    private String id;

    @ManyToOne
    @JoinColumn(name="person_id")
    @NotNull(groups = {Create.class, Update.class})
    @Valid
    private Person person;

    @ManyToOne
    @JoinColumn(name="head_office_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Branch.Detail.class})
    private Establishment headOffice;

    @Column(name="contact_mail")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Branch.Detail.class})
    private String contactMail;

    @Column(name="invoice_mail")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Branch.Detail.class})
    private String invoiceMail;

    @JsonView({Views.Branch.Detail.class})
    @Column(name="alternative_mail")
    private String alternativeMail;

    @Column(name="cancellation_tolerance")
    @JsonView({Views.Branch.Detail.class})
    @Max(value = 60, groups = {Create.class, Update.class})
    private Integer cancellationTolerance;

    @Column(name = "fee")
    @JsonView({Views.Branch.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private Double fee;

    @JsonView({Views.Branch.Detail.class})
    @Column(name = "technical_contact")
    private String technicalContact;

    @JsonView({Views.Branch.Detail.class})
    @Column(name = "branch_photo_uri")
    private String branchPhotoUri;

    @JsonView({Views.Branch.Detail.class})
    @Column(name = "contract_uri")
    private String contractUri;

    @Valid
    @Enumerated(STRING)
    @Column(name="gathering_channel")
    @JsonView({Views.Branch.Detail.class})
    private GatheringChannel gatheringChannel;

    @Valid
    @ManyToOne
    @JoinColumn(name="movement_account_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.BankAccount.class})
    private BankAccount bankAccount;

    @Valid
    @Embedded
    @JsonView({Views.Branch.Detail.class})
    private Checkout checkout;

    @Valid
    @Embedded
    @JsonView({Views.Branch.Detail.class})
    private InvoiceReceipt invoiceReceipt;


    public void validateCreate(){
        if(getHeadOffice() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(HEAD_OFFICE_REQUIRED);
        }
        if(getPerson() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PERSON_REQUIRED);
        }
        if(getBankAccount() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(BANK_ACCOUNT_REQUIRED);
        }
    }

    public void validateUpdate(Branch current) {
        validateCreate();
        if(getBankAccount().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(BANK_ACCOUNT_ID_REQUIRED);
        }
        if(getHeadOffice().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(CANNOT_CHANGE_HEAD_OFFICE);
        }
        if(!Objects.equals(getHeadOffice().getId(), current.getHeadOffice().getId())) {
            throw UnovationExceptions.unprocessableEntity().withErrors(CANNOT_CHANGE_HEAD_OFFICE);
        }
        if(getPerson().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PERSON_ID_REQUIRED);
        }
    }

    public void updateMe(Branch other) {
        headOffice = other.getHeadOffice();
        alternativeMail = other.getAlternativeMail();
        bankAccount = other.getBankAccount();
        checkout = other.getCheckout();
        contactMail = other.getContactMail();
        contractUri = other.getContractUri();
        branchPhotoUri = other.getBranchPhotoUri();
        cancellationTolerance = other.getCancellationTolerance();
        gatheringChannel = other.getGatheringChannel();
        invoiceMail = other.getInvoiceMail();
        person = other.getPerson();
        fee = other.getFee();
        technicalContact = other.getTechnicalContact();

    }
}
