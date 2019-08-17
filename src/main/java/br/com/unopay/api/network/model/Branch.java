package br.com.unopay.api.network.model;

import br.com.unopay.api.bacen.model.GatheringChannel;
import br.com.unopay.api.geo.model.Localizable;
import br.com.unopay.api.model.Address;
import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.uaa.exception.Errors.ADDRESS_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.ADDRESS_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.CANNOT_CHANGE_HEAD_OFFICE;
import static br.com.unopay.api.uaa.exception.Errors.HEAD_OFFICE_REQUIRED;

@Data
@EqualsAndHashCode(exclude = {"servicePeriods", "services", "gatheringChannels"})
@Entity
@Table(name = "branch")
public class Branch implements Serializable, Updatable, Localizable {

    public static final long serialVersionUID = 1L;

    public Branch(){}

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @Column(name="id")
    private String id;

    @NotNull(groups = {Create.class, Update.class})
    @Column(name="name")
    @Size(min=2, max = 50, groups = {Create.class, Update.class})
    private String name;

    @Column(name="short_name")
    @NotNull(groups = {Create.class, Update.class})
    @Size(min=2,max = 20, groups = {Create.class, Update.class})
    private String shortName;

    @Column(name="fantasy_name")
    @Size(max = 50, groups = {Create.class, Update.class})
    @NotNull(groups = {Create.class, Update.class})
    private String fantasyName;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @OneToOne
    @JsonView({Views.Branch.List.class})
    @JoinColumn(name="address_id")
    private Address address;

    @ManyToOne
    @JoinColumn(name="head_office_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Branch.Detail.class})
    private Establishment headOffice;

    @Column(name="contact_mail")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Branch.Detail.class})
    private String contactMail;

    @JsonView({Views.Branch.Detail.class})
    @Column(name = "technical_contact")
    private String technicalContact;

    @JsonView({Views.Branch.Detail.class})
    @Column(name = "branch_photo_uri")
    private String branchPhotoUri;

    @JsonView({Views.Branch.List.class})
    @Enumerated(EnumType.STRING)
    @Column(name = "situation")
    @NotNull(groups = {Create.class, Update.class})
    private BranchSituation situation;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER, targetClass = GatheringChannel.class)
    @Column(name = "gathering_channel", nullable = false)
    @JsonView({Views.Branch.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    @CollectionTable(name = "establishment_branch_gathering", joinColumns = @JoinColumn(name = "branch_id"))
    private Set<GatheringChannel> gatheringChannels;

    @ManyToMany
    @BatchSize(size = 10)
    @JsonView({Views.Branch.Detail.class})
    @JoinTable(name = "establishment_branch_service",
            joinColumns = { @JoinColumn(name = "branch_id") },
            inverseJoinColumns = { @JoinColumn(name = "service_id") })
    private Set<Service> services;

    @JsonManagedReference
    @JsonView({Views.Branch.List.class})
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "branch")
    private Set<BranchServicePeriod> servicePeriods = new HashSet<>();

    @NotNull(groups = {Create.class, Update.class})
    @Column(name = "returning_deadline")
    @JsonView({Views.Branch.Detail.class})
    private Integer returningDeadline;

    public Set<BranchServicePeriod> cutServicePeriods(){
        Set<BranchServicePeriod> periodsToReturn = this.servicePeriods;
        this.servicePeriods = null;
        return periodsToReturn;
    }

    public void validateCreate(){
        if(getHeadOffice() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(HEAD_OFFICE_REQUIRED);
        }
        if(getAddress() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(ADDRESS_REQUIRED);
        }
    }

    public void validateUpdate(Branch current) {
        validateCreate();
        if(getHeadOffice().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(CANNOT_CHANGE_HEAD_OFFICE);
        }
        if(!Objects.equals(getHeadOffice().getId(), current.getHeadOffice().getId())) {
            throw UnovationExceptions.unprocessableEntity().withErrors(CANNOT_CHANGE_HEAD_OFFICE);
        }
        if(getAddress().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(ADDRESS_ID_REQUIRED);
        }
    }

    public String getId() {
        return id;
    }

    public String headOfficeId(){
        if(this.headOffice != null){
            this.headOffice.getId();
        }
        return null;
    }

    @Override
    public void defineAddressLat(double lat) {
        if(hasAddress()) {
            this.address.setLatitude(lat);
        }
    }

    @Override
    public void defineAddressLong(double lng) {
        if(hasAddress()) {
            this.address.setLongitude(lng);
        }
    }

    @Override
    public String formattedAddress() {
        if(hasAddress()) {
            return this.address.toString();
        }
        return null;
    }

    public boolean hasAddress(){
        return this.address != null;
    }
}
