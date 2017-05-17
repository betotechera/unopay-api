package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.BrandFlag;
import br.com.unopay.api.model.Contact;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.Updatable;
import static br.com.unopay.api.uaa.exception.Errors.ACCREDITED_NETWORK_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.ACCREDITED_NETWORK_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.BANK_ACCOUNT_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.BANK_ACCOUNT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.BRAND_FLAG_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.BRAND_FLAG_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.CONTACT_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.CONTACT_REQUIRED;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import static javax.persistence.EnumType.STRING;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
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
import java.io.Serializable;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "establishment")
public class Establishment implements Serializable, Updatable {

    public static final long serialVersionUID = 1L;

    public Establishment(){}

    @Id
    @Column(name="id")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @Valid
    @JoinColumn(name="person_id")
    @NotNull(groups = {Create.class, Update.class})
    @ManyToOne
    @JsonView({Views.Public.class,Views.List.class})
    private Person person;

    @Valid
    @Enumerated(STRING)
    @Column(name="type")
    @JsonView({Views.Public.class,Views.List.class})
    private EstablishmentType type;

    @Column(name="contact_mail")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String contactMail;

    @Column(name="invoice_mail")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String invoiceMail;

    @Column(name="bach_shipment_mail")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String bachShipmentMail;

    @Column(name="alternative_mail")
    @JsonView({Views.Public.class,Views.List.class})
    private String alternativeMail;

    @Column(name="cancellation_tolerance")
    @JsonView({Views.Public.class,Views.List.class})
    @Max(value = 60, groups = {Create.class, Update.class})
    private Integer cancellationTolerance;

    @SuppressWarnings("squid:S1192")
    @Column(name = "tax")
    @NotNull(groups = {Create.class, Update.class})
    private Double tax;

    @ManyToOne
    @JoinColumn(name="accredited_network_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private AccreditedNetwork network;

    @ManyToOne
    @JoinColumn(name="brand_flag_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private BrandFlag brandFlag;

    @Column(name = "logo_uri")
    private String logoUri;

    @Valid
    @OneToOne
    @JoinColumn(name="operational_contact_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Contact operationalContact;

    @Valid
    @OneToOne
    @JoinColumn(name="administrative_contact_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Contact administrativeContact;

    @Valid
    @OneToOne
    @JoinColumn(name="financier_contact_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Contact financierContact;

    @Column(name = "technical_contact")
    private String technicalContact;

    @Column(name = "establishment_photo_uri")
    private String establishmentPhotoUri;

    @Column(name = "contract_uri")
    private String contractUri;

    @Valid
    @Enumerated(STRING)
    @Column(name="gathering_channel")
    @JsonView({Views.Public.class,Views.List.class})
    private GatheringChannel gatheringChannel;

    @ManyToMany
    @BatchSize(size = 10)
    @JsonView({Views.Public.class})
    @JoinTable(name = "establishment_service",
            joinColumns = { @JoinColumn(name = "establishment_id") },
            inverseJoinColumns = { @JoinColumn(name = "service_id") })
    private Set<Service> services;
    
    @Valid
    @ManyToOne
    @JoinColumn(name="movement_account_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private BankAccount bankAccount;

    @Valid
    @Embedded
    @JsonView({Views.Public.class})
    private Checkout checkout;

    @Valid
    @Embedded
    @JsonView({Views.Public.class})
    private InvoiceReceipt invoiceReceipt;

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
        if(getBrandFlag() == null) {
            throw  UnovationExceptions.unprocessableEntity().withErrors(BRAND_FLAG_REQUIRED);
        }
        if(getBrandFlag().getId() == null){
            throw  UnovationExceptions.unprocessableEntity().withErrors(BRAND_FLAG_ID_REQUIRED);
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
    public String getDocumentNumber(){
        if(getPerson() != null && getPerson().getDocument() != null){
            return getPerson().getDocument().getNumber();
        }
        return null;
    }
}
