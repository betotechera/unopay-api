package br.com.unopay.api.uaa.model;

import br.com.unopay.api.billing.creditcard.model.StoreCard;
import br.com.unopay.api.network.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.network.model.Establishment;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.Institution;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.network.model.Partner;
import br.com.unopay.api.infra.ReflectionHelper;
import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.PasswordRequired;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Entity
@Table(name = "oauth_user_details")
@Data
@EqualsAndHashCode(exclude = { "groups" })
public class UserDetail implements Serializable, Updatable, StoreCard {

    public static final long serialVersionUID = 1L;

    public UserDetail(Contractor contractor, String password){
        this.contractor = contractor;
        this.name = contractor.getPerson().getShortName();
        this.email = contractor.getPerson().getPhysicalPersonEmail();
        this.password = password;
    }

    @Id
    @NotNull(groups = Update.class)
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @Column(name="id")
    private String id;

    @NotNull(groups = Create.class)
    @Column(name="email", unique = true)
    @JsonView({Views.User.List.class})
    @Size(min=5, max = 50, groups = {Create.class, Update.class})
    private String email;


    @Column(name="name")
    @NotNull(groups = Create.class)
    @JsonView({Views.User.List.class})
    @Size(min=2, max = 20, groups = {Create.class, Update.class})
    private String name;

    @ManyToOne
    @JoinColumn(name="type")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.User.List.class})
    private UserType type;

    @ManyToOne
    @JoinColumn(name="institution_id")
    @JsonView({Views.User.Detail.class})
    private Institution institution;

    @ManyToOne
    @JoinColumn(name="accredited_network_id")
    @JsonView({Views.User.Detail.class})
    private AccreditedNetwork accreditedNetwork;

    @ManyToOne
    @JoinColumn(name="establishment_id")
    @JsonView({Views.User.Detail.class})
    private Establishment establishment;

    @ManyToOne
    @JoinColumn(name="issuer_id")
    @JsonView({Views.User.Detail.class})
    private Issuer issuer;

    @ManyToOne
    @JoinColumn(name="hirer_id")
    @JsonView({Views.User.Detail.class})
    private Hirer hirer;

    @ManyToOne
    @JoinColumn(name="contractor_id")
    @JsonView({Views.User.Detail.class, Views.PersonCreditCard.List.class})
    private Contractor contractor;

    @ManyToOne
    @JoinColumn(name="partner_id")
    @JsonView({Views.User.Detail.class})
    private Partner partner;


    @JsonView({Views.User.Private.class})
    @NotNull(groups = PasswordRequired.class)
    @Column(name="password")
    @Size(min=5, max = 50, groups = {PasswordRequired.class })
    private String password;

    @BatchSize(size = 10)
    @OneToMany(fetch = FetchType.EAGER)
    @JsonView({Views.User.List.class})
    @JoinTable(name = "oauth_group_members",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "group_id") })
    private Set<Group> groups;

    @Transient
    @NotNull(groups = Create.PersonCreditCard.class)
    private String issuerDocument;

    @Version
    @JsonIgnore
    Long version;

    public UserDetail() {}

    public UserDetail(String id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }


    public List<SimpleGrantedAuthority> toGrantedAuthorities(List<Group> groups) {
        return getAuthoritiesNames(groups).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public List<Authority> getAuthorities(List<Group> groups) {
        if (groups == null) {
            return Collections.emptyList();
        }
        return groups.stream()
                .filter(Objects::nonNull)
                .map(Group::getAuthorities)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<String> getAuthoritiesNames(List<Group> groups) {
        return getAuthorities(groups).stream().map(Authority::getName).collect(Collectors.toList());
    }

    public void addToMyGroups(Group group){
        if(getGroups() == null) {
            setGroups(new HashSet<>());
        }
        getGroups().add(group);
    }

    public void addToMyGroups(List<Group> groups){
        if(groups == null) {
            return;
        }
        groups.forEach(this::addToMyGroups);
    }

    @JsonView(Views.User.Detail.class)
    public List<Authority> getGroupsAuthorities() {
        if(groups == null) {
            return Collections.emptyList();
        }
        return groups.stream().map(Group::getAuthorities)
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "UserDetail{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", contractor='" + myContractor().map(Contractor::toString).orElse(null) + '\'' +
                '}';
    }

    @JsonIgnore
    public boolean isEstablishmentType(){
        return  establishment != null;
    }

    public String establishmentId(){
        return isEstablishmentType() ? getEstablishment().getId() : null;
    }

    public String institutionId(){
        return isInstitutionType() ? getInstitution().getId() : null;
    }

    @JsonIgnore
    public boolean isInstitutionType() {
        return institution != null;
    }

    public String partnerId() {
        return isPartnerType() ?  getPartner().getId() : null;
    }

    @JsonIgnore
    public boolean isPartnerType() {
        return partner != null;
    }


    public Optional<Establishment> myEstablishment() {
        return Optional.ofNullable(getEstablishment());
    }

    public Optional<Contractor> myContractor() {
        return Optional.ofNullable(getContractor());
    }

    public Optional<AccreditedNetwork> myNetWork() {
        return Optional.ofNullable(getAccreditedNetwork());
    }
    public Optional<Hirer> myHirer() {
        return Optional.ofNullable(getHirer());
    }

    public Optional<Issuer> myIssuer() {
        return Optional.ofNullable(getIssuer());
    }

    public Optional<Institution> myInstitution() {
        return Optional.ofNullable(getInstitution());
    }

    public Optional<Partner> myPartner() {
        return Optional.ofNullable(getPartner());
    }


    public Object my(Class<?> clazz) {
        return ReflectionHelper.invokeAttributeOfType(clazz, this);
    }

    public String myEstablishmentId() {
        return myEstablishment().map(Establishment::getId).orElse(null);
    }

    public String myContractorId() {
        return myContractor().map(Contractor::getId).orElse(null);
    }

    public String myHirerId() {
        return myHirer().map(Hirer::getId).orElse(null);
    }

    public String myNetworkId() {
        return myNetWork().map(AccreditedNetwork::getId).orElse(null);
    }

    @JsonIgnore
    public boolean isIssuerType() {
        return issuer != null;
    }

    public String issuerId() {
        return isIssuerType() ? issuer.getId() : null;
    }

    @JsonIgnore
    public boolean isAccreditedNetworkType() {
        return accreditedNetwork != null;
    }

    public String accreditedNetworkId() {
        return isAccreditedNetworkType() ? accreditedNetwork.getId() : null;
    }

    @JsonIgnore
    public boolean isHirerType() {
        return hirer != null;
    }

    public String hirerId() {
        return isHirerType() ? hirer.getId() : null;
    }

    @JsonIgnore
    public boolean isContractorType() {
        return contractor != null;
    }

    public String contractorId() {
        return isContractorType() ? contractor.getId() : null;
    }

    public boolean hasPassword(){
        return this.password != null;
    }

    public String contractorDocument() {
        return isContractorType() ? contractor.getDocumentNumber() : null;
    }

    public void updateMe(UserDetail source) {
        updateOnly(source,"email", "name", "type");
        institution = source.institution;
        accreditedNetwork = source.accreditedNetwork;
        establishment = source.establishment;
        issuer = source.issuer;
        hirer = source.hirer;
        contractor = source.contractor;
        partner = source.partner;
        groups = source.groups;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getIssuerDocument() {
        return this.issuerDocument;
    }
}
