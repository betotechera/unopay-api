package br.com.unopay.api.bacen.model;


import br.com.unopay.api.model.Person;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.Objects;

import static br.com.unopay.api.uaa.exception.Errors.*;
import static javax.persistence.EnumType.STRING;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "subsidiary")
public class Subsidiary {

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
    @ManyToOne
    @JoinColumn(name="matrix_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Establishment matrix;

    @Column(name="contact_mail")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String contactMail;

    @Column(name="invoice_mail")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String invoiceMail;

    @Column(name="alternative_mail")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String alternativeMail;

    @Column(name="cancellation_tolerance")
    @Size(max = 60, groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Integer cancellationTolerance;

    @Column(name = "tax")
    @NotNull(groups = {Create.class, Update.class})
    private Double tax;

    @Column(name = "technical_contact")
    private String technicalContact;

    @Column(name = "subsidiary_photo_uri")
    private String subsidiaryPhotoUri;

    @Column(name = "contract_uri")
    private String contractUri;

    @Valid
    @Enumerated(STRING)
    @Column(name="gathering_channel")
    @JsonView({Views.Public.class,Views.List.class})
    private EstablishmentType gatheringChannel;

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

    public void validateCreate(){
        if(getMatrix() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(MATRIX_REQUIRED);
        }
        if(getPerson() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PERSON_REQUIRED);
        }
        if(getBankAccount() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(BANK_ACCOUNT_REQUIRED);
        }
    }

    public void validateUpdate(Subsidiary current) {
        validateCreate();
        if(getBankAccount().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(BANK_ACCOUNT_ID_REQUIRED);
        }
        if(getMatrix().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(CANNOT_CHANGE_MATRIX);
        }
        if(!Objects.equals(getMatrix().getId(), current.getMatrix().getId())) {
            throw UnovationExceptions.unprocessableEntity().withErrors(CANNOT_CHANGE_MATRIX);
        }
        if(getPerson().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PERSON_ID_REQUIRED);
        }
    }
}
