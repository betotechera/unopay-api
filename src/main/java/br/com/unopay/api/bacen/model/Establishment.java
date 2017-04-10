package br.com.unopay.api.bacen.model;


import br.com.unopay.api.model.BrandFlag;
import br.com.unopay.api.model.Contact;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.Set;

import static br.com.unopay.api.uaa.exception.Errors.*;
import static javax.persistence.EnumType.STRING;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "establishment")
public class Establishment {

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Valid
    @ManyToOne
    @JoinColumn(name="person_id")
    @NotNull(groups = {Create.class, Update.class})
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
    @Max(value = 60, groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Integer cancellationTolerance;

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

    public void updateMe(Establishment other) {
         administrativeContact =  other.getAdministrativeContact();
         alternativeMail =  other.getAlternativeMail();
         bachShipmentMail =  other.getBachShipmentMail();
         bankAccount =  other.getBankAccount();
         brandFlag =  other.getBrandFlag();
         checkout =  other.getCheckout();
         contactMail =  other.getContactMail();
         contractUri =  other.getContractUri();
         establishmentPhotoUri =  other.getEstablishmentPhotoUri();
         cancellationTolerance =  other.getCancellationTolerance();
         financierContact =  other.getFinancierContact();
         gatheringChannel =  other.getGatheringChannel();
         invoiceMail =  other.getInvoiceMail();
         logoUri =  other.getLogoUri();
         person =  other.getPerson();
         tax =  other.getTax();
         network =  other.getNetwork();
         type =  other.getType();
         operationalContact =  other.getOperationalContact();
         technicalContact =  other.getTechnicalContact();
    }
}
