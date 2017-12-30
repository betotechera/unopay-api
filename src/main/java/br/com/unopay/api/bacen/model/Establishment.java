package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Contact;
import br.com.unopay.api.model.IssueInvoiceType;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import com.opencsv.bean.CsvBindByName;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.uaa.exception.Errors.ACCREDITED_NETWORK_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.ACCREDITED_NETWORK_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.BANK_ACCOUNT_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.BANK_ACCOUNT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.CONTACT_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.CONTACT_REQUIRED;
import static javax.persistence.EnumType.STRING;

@Data
@Entity
@ToString(exclude = "services")
@EqualsAndHashCode(exclude = "services")
@Table(name = "establishment")
public class Establishment implements Serializable, Updatable {

    public static final long serialVersionUID = 1L;

    public Establishment(){}

    @CsvBindByName
    @Id
    @Column(name="id")
    @NotNull(groups = {Reference.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @Valid
    @JoinColumn(name="person_id")
    @NotNull(groups = {Create.class, Update.class})
    @ManyToOne
    private Person person;

    @Valid
    @Enumerated(STRING)
    @Column(name="type")
    @JsonView({Views.Establishment.Detail.class})
    private EstablishmentType type;

    @Column(name="contact_mail")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Establishment.Detail.class})
    private String contactMail;

    @Column(name="invoice_mail")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Establishment.Detail.class})
    private String invoiceMail;

    @Column(name="bach_shipment_mail")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Establishment.Detail.class})
    private String bachShipmentMail;

    @Column(name="alternative_mail")
    @JsonView({Views.Establishment.Detail.class})
    private String alternativeMail;

    @Column(name="cancellation_tolerance")
    @JsonView({Views.Establishment.Detail.class})
    @Max(value = 60, groups = {Create.class, Update.class})
    private Integer cancellationTolerance;

    @Column(name = "fee")
    @JsonView({Views.Establishment.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private Double fee;

    @ManyToOne
    @JoinColumn(name="accredited_network_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Establishment.Detail.class})
    private AccreditedNetwork network;

    @Column(name = "facade_photo_uri")
    @JsonView({Views.Establishment.Detail.class})
    private String facadePhotoUri;

    @Column(name = "logo_uri")
    @JsonView({Views.Establishment.Detail.class})
    private String logoUri;

    @Valid
    @OneToOne
    @JoinColumn(name="operational_contact_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Establishment.Detail.class})
    private Contact operationalContact;

    @Valid
    @OneToOne
    @JoinColumn(name="administrative_contact_id")
    @JsonView({Views.Establishment.Detail.class})
    private Contact administrativeContact;

    @Valid
    @OneToOne
    @JoinColumn(name="financier_contact_id")
    @JsonView({Views.Establishment.Detail.class})
    private Contact financierContact;

    @Column(name = "technical_contact")
    @JsonView({Views.Establishment.Detail.class})
    private String technicalContact;

    @JsonView({Views.Establishment.Detail.class})
    @Column(name = "contract_uri")
    private String contractUri;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER, targetClass = GatheringChannel.class)
    @Column(name = "gathering_channel", nullable = false)
    @JsonView({Views.Establishment.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    @CollectionTable(name = "establishment_gathering", joinColumns = @JoinColumn(name = "establishment_id"))
    private Set<GatheringChannel> gatheringChannels;

    @ManyToMany
    @BatchSize(size = 10)
    @JsonView({Views.Establishment.Detail.class})
    @JoinTable(name = "establishment_service",
            joinColumns = { @JoinColumn(name = "establishment_id") },
            inverseJoinColumns = { @JoinColumn(name = "service_id") })
    private Set<Service> services;
    
    @Valid
    @ManyToOne
    @JoinColumn(name="movement_account_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Establishment.Detail.class})
    private BankAccount bankAccount;

    @Valid
    @Embedded
    @JsonView({Views.Establishment.Detail.class})
    private Checkout checkout;

    @Valid
    @Embedded
    @JsonView({Views.Establishment.Detail.class})
    private InvoiceReceipt invoiceReceipt;

    @Column(name = "issue_invoice_type")
    @NotNull(groups = {Create.class, Update.class})
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Establishment.Detail.class})
    private IssueInvoiceType issueInvoiceType;

    public void validateCreate(){
        if(getBankAccount() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(BANK_ACCOUNT_REQUIRED);
        }
        if(getNetwork() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(ACCREDITED_NETWORK_REQUIRED);
        }
        if(getNetwork().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(ACCREDITED_NETWORK_ID_REQUIRED);
        }
        if(getAdministrativeContact() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(CONTACT_REQUIRED);
        }
        if(getOperationalContact() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(CONTACT_REQUIRED);
        }
        if(getFinancierContact() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(CONTACT_REQUIRED);
        }
    }

    public void validateUpdate(){
        validateCreate();
        if(getBankAccount().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(BANK_ACCOUNT_ID_REQUIRED);
        }
        if(getAdministrativeContact().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(CONTACT_ID_REQUIRED);
        }
        if(getOperationalContact().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(CONTACT_ID_REQUIRED);
        }
        if(getFinancierContact().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(CONTACT_ID_REQUIRED);
        }
    }

    public String documentNumber(){
        if(getPerson() != null && getPerson().getDocument() != null){
            return getPerson().getDocument().getNumber();
        }
        return null;
    }

}
