package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.Document;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "authorized_member")
public class AuthorizedMember implements Serializable, Updatable{

    public static final long serialVersionUID = 1L;
    public static final int YEAR_LIMIT = 150;

    @Id
    @Column(name="id")
    @NotNull(groups = {Reference.class})
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Column(name="birth_date")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.AuthorizedMember.List.class})
    private Date birthDate;

    @ManyToOne
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.AuthorizedMember.List.class})
    @JoinColumn(name="contract_id")
    private Contract contract;

    @Column(name="name")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.AuthorizedMember.List.class})
    @Size(max=256)
    private String name;

    @Column(name="gender")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.AuthorizedMember.Detail.class})
    @Size(max=50)
    private String gender;

    @Column(name="relatedness")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.AuthorizedMember.Detail.class})
    @Size(max=50)
    private String relatedness;

    @Column(name="email")
    @JsonView({Views.AuthorizedMember.List.class})
    @Size(max=256)
    private String email;

    @Valid
    @JsonView({Views.AuthorizedMember.List.class})
    @Embedded
    private Document document;

    @ManyToOne
    @JoinColumn(name="payment_instrument_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.AuthorizedMember.List.class})
    private PaymentInstrument paymentInstrument;


    public void validateMe() {
        validateBirthDate();
        validateName();
        validateGender();
        validateRelatedness();
        validateContract();
        validatePaymentInstrument();
    }

    private void validateContract() {
        if(contract == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.CONTRACT_REQUIRED);
        }
    }

    private void validateRelatedness() {
        if(relatedness == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.AUTHORIZED_MEMBER_RELATEDNESS_REQUIRED);
        }
    }

    private void validateName() {
        if(name == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.AUTHORIZED_MEMBER_NAME_REQUIRED);
        }
    }

    private void validateGender() {
        if(gender == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.AUTHORIZED_MEMBER_GENDER_REQUIRED);
        }
    }

    private void validateBirthDate() {
        Date maximumDate = new Date();
        Date minimumDate = new DateTime().minusYears(YEAR_LIMIT).toDate();
        if(birthDate == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.AUTHORIZED_MEMBER_BIRTH_DATE_REQUIRED);
        }
        if(birthDate.before(minimumDate) || birthDate.after(maximumDate)) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.INVALID_AUTHORIZED_MEMBER_BIRTH_DATE);
        }
    }

    private void validatePaymentInstrument() {
        if(instrumentRequired()) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.PAYMENT_INSTRUMENT_REQUIRED);
        }
        if(!validInstrumentContractor()) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.INSTRUMENT_NOT_BELONGS_TO_CONTRACTOR);
        }
    }

    private boolean instrumentRequired() {
        return paymentInstrument == null;
    }

    private boolean validInstrumentContractor(){
        return paymentInstrument.contractorId().equals(contract.getContractor().getId());
    }

    public String instrumentId() {
        return paymentInstrument != null ? paymentInstrument.getId() : null;
    }

    public String contractId() {
        return contract != null ? contract.getId() : null;
    }

    public String contractorDocumentNumber() {
        return contract.getContractor().getDocumentNumber();
    }

    public boolean withInstrument() {
        return paymentInstrument != null;
    }
}
