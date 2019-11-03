package br.com.unopay.api.model;

import br.com.unopay.api.billing.creditcard.model.PaymentMethod;
import br.com.unopay.api.market.model.AuthorizedMemberCandidate;
import br.com.unopay.api.order.model.RecurrencePaymentInformation;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

@Getter
public class Deal {

    public Deal(Person person, String hirerDocument, String productCode) {
        this.person = person;
        this.hirerDocument = hirerDocument;
        this.productCode = productCode;
        this.members = new HashSet<>();
    }

    public Deal(Person person, String productCode, Boolean createUser, Set<AuthorizedMemberCandidate> members) {
        this.person = person;
        this.productCode = productCode;
        this.createUser = createUser;
        this.members = members;
    }

    public Deal(Person person, String productCode, Boolean createUser, Set<AuthorizedMemberCandidate> members, String password,
                RecurrencePaymentInformation recurrencePaymentInformation) {
        this.person = person;
        this.productCode = productCode;
        this.createUser = createUser;
        this.members = members;
        this.password = password;
        this.recurrencePaymentInformation = recurrencePaymentInformation;
    }

    public Deal(Person person, String productCode, Boolean createUser) {
        this.person = person;
        this.productCode = productCode;
        this.createUser = createUser;
        this.members = new HashSet<>();
    }

    public Deal(Person person, String productCode, Boolean createUser, String Password) {
        this.person = person;
        this.productCode = productCode;
        this.createUser = createUser;
        this.password = Password;
        this.members = new HashSet<>();
    }

    private Person person;
    private String hirerDocument;
    private String productCode;
    private Boolean createUser;
    private Set<AuthorizedMemberCandidate> members;
    private String password;
    private RecurrencePaymentInformation recurrencePaymentInformation = new RecurrencePaymentInformation();

    public Boolean hasHirerDocument() {
        return this.hirerDocument != null;
    }

    public boolean mustCreateUser() {
        return createUser == null || createUser;
    }

    public PaymentMethod getRecurrencePaymentMethod() {
        if(recurrencePaymentInformation != null){
            return this.recurrencePaymentInformation.getPaymentMethod();
        }
        return null;
    }

}
