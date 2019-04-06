package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
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
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.uaa.exception.Errors.CANNOT_CHANGE_HEAD_OFFICE;
import static br.com.unopay.api.uaa.exception.Errors.HEAD_OFFICE_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PERSON_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PERSON_REQUIRED;

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

    @JsonView({Views.Branch.Detail.class})
    @Column(name = "technical_contact")
    private String technicalContact;

    @JsonView({Views.Branch.Detail.class})
    @Column(name = "branch_photo_uri")
    private String branchPhotoUri;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER, targetClass = GatheringChannel.class)
    @Column(name = "gathering_channel", nullable = false)
    @JsonView({Views.Establishment.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    @CollectionTable(name = "establishment_branch_gathering", joinColumns = @JoinColumn(name = "branch_id"))
    private Set<GatheringChannel> gatheringChannels;

    @ManyToMany
    @BatchSize(size = 10)
    @JsonView({Views.Establishment.Detail.class})
    @JoinTable(name = "establishment_branch_service",
            joinColumns = { @JoinColumn(name = "branch_id") },
            inverseJoinColumns = { @JoinColumn(name = "service_id") })
    private Set<Service> services;

    public void validateCreate(){
        if(getHeadOffice() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(HEAD_OFFICE_REQUIRED);
        }
        if(getPerson() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PERSON_REQUIRED);
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
        if(getPerson().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PERSON_ID_REQUIRED);
        }
    }

    public void updateMe(Branch other) {
        headOffice = other.getHeadOffice();
        contactMail = other.getContactMail();
        branchPhotoUri = other.getBranchPhotoUri();
        gatheringChannels = other.getGatheringChannels();
        person = other.getPerson();
        technicalContact = other.getTechnicalContact();
        services = other.getServices();
    }
}
