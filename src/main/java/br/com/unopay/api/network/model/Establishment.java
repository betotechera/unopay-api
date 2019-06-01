package br.com.unopay.api.network.model;

import br.com.unopay.api.bacen.model.BankAccount;
import br.com.unopay.api.bacen.model.Checkout;
import br.com.unopay.api.bacen.model.GatheringChannel;
import br.com.unopay.api.bacen.model.InvoiceReceipt;
import br.com.unopay.api.geo.model.Localizable;
import br.com.unopay.api.model.Contact;
import br.com.unopay.api.model.IssueInvoiceType;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.opencsv.bean.CsvBindByName;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
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
@ToString(exclude = {"services", "eventPrices"})
@EqualsAndHashCode(exclude = {"services", "eventPrices"})
@Table(name = "establishment")
public class Establishment implements Serializable, Updatable, Localizable {

    public static final long serialVersionUID = 1L;

    public Establishment() {
    }

    @CsvBindByName
    @Id
    @Column(name = "id")
    @NotNull(groups = {Reference.class})
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @GeneratedValue(generator = "system-uuid")
    private String id;

    @Valid
    @JoinColumn(name = "person_id")
    @NotNull(groups = {Create.class, Update.class})
    @ManyToOne
    private Person person;

    @Valid
    @Enumerated(STRING)
    @Column(name = "type")
    @JsonView({Views.Establishment.Detail.class})
    private EstablishmentType type;

    @Column(name = "contact_mail")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Establishment.Detail.class})
    private String contactMail;

    @Column(name = "invoice_mail")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Establishment.Detail.class})
    private String invoiceMail;

    @Column(name = "bach_shipment_mail")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Establishment.Detail.class})
    private String bachShipmentMail;

    @Column(name = "alternative_mail")
    @JsonView({Views.Establishment.Detail.class})
    private String alternativeMail;

    @Column(name = "cancellation_tolerance")
    @JsonView({Views.Establishment.Detail.class})
    @Max(value = 60, groups = {Create.class, Update.class})
    private Integer cancellationTolerance;

    @Column(name = "fee")
    @JsonView({Views.Establishment.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private Double fee;

    @ManyToOne
    @JoinColumn(name = "accredited_network_id")
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
    @JoinColumn(name = "operational_contact_id")
    @JsonView({Views.Establishment.Detail.class})
    private Contact operationalContact;

    @Valid
    @OneToOne
    @JoinColumn(name = "administrative_contact_id")
    @JsonView({Views.Establishment.Detail.class})
    private Contact administrativeContact;

    @Valid
    @OneToOne
    @JoinColumn(name = "financier_contact_id")
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
            joinColumns = {@JoinColumn(name = "establishment_id")},
            inverseJoinColumns = {@JoinColumn(name = "service_id")})
    private Set<Service> services;

    @OneToMany(mappedBy = "establishment")
    @BatchSize(size = 10)
    @JsonIgnore
    private Set<EstablishmentEvent> eventPrices;

    @Valid
    @ManyToOne
    @JoinColumn(name = "movement_account_id")
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

    @NotNull(groups = {Create.class, Update.class})
    @Column(name = "returning_deadline")
    @JsonView({Views.Establishment.Detail.class})
    private Integer returningDeadline;

    @Transient
    private boolean createBranch;

    @Transient
    private Set<BranchServicePeriod> servicePeriods = new HashSet<>();

    public AccreditedNetwork getNetwork() {
        return network;
    }

    public void validateCreate() {
        if (getBankAccount() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(BANK_ACCOUNT_REQUIRED);
        }
        if (getNetwork() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(ACCREDITED_NETWORK_REQUIRED);
        }
        if (getNetwork().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(ACCREDITED_NETWORK_ID_REQUIRED);
        }
        if (getFinancierContact() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(CONTACT_REQUIRED);
        }
    }

    public void validateUpdate() {
        validateCreate();
        if (getBankAccount().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(BANK_ACCOUNT_ID_REQUIRED);
        }
        if (getFinancierContact().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(CONTACT_ID_REQUIRED);
        }
    }

    public boolean hasOperationalContact() {
        return this.operationalContact != null;
    }

    public boolean hasAdministrativeContact() {
        return this.administrativeContact != null;
    }

    public String documentNumber() {
        if (getPerson() != null && getPerson().getDocument() != null) {
            return getPerson().getDocument().getNumber();
        }
        return null;
    }

    @Override
    public void defineAddressLat(double lat) {
        this.person.getAddress().setLatitude(lat);
    }

    @Override
    public void defineAddressLong(double lng) {
        this.person.getAddress().setLongitude(lng);
    }

    public String formattedAddress() {
        return person.getFormatedAddress();
    }

    public String personId() {
        if (getPerson() != null) {
            return getPerson().getId();
        }
        return null;
    }

    public Person returnPerson() {
        if (getPerson() != null) {
            return getPerson();
        }
        return null;
    }

    public Branch toBranch() {
        Branch branch = new Branch();
        branch.setSituation(BranchSituation.REGISTERED);
        branch.setServicePeriods(Collections.unmodifiableSet(this.servicePeriods));
        branch.setGatheringChannels(Collections.unmodifiableSet(this.gatheringChannels));
        branch.setFantasyName(this.person.getLegalPersonDetail().getFantasyName());
        branch.setShortName(this.person.getShortName());
        branch.setName(this.person.getName());
        branch.setAddress(this.person.getAddress());
        branch.setHeadOffice(this);
        if(this.services != null) {
            branch.setServices(Collections.unmodifiableSet(this.services));
        }
        branch.setContactMail(this.contactMail);
        branch.setTechnicalContact(this.technicalContact);
        branch.setBranchPhotoUri(this.facadePhotoUri);
        return branch;
    }
}
