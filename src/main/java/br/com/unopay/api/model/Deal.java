package br.com.unopay.api.model;

import br.com.unopay.api.market.model.AuthorizedMemberCandidate;
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

    public Deal(Person person, String productCode, Set<AuthorizedMemberCandidate> members) {
        this.person = person;
        this.productCode = productCode;
        this.members = members;
    }

    public Deal(Person person, String productCode) {
        this.person = person;
        this.productCode = productCode;
        this.members = new HashSet<>();
    }

    private Person person;
    private String hirerDocument;
    private String productCode;
    private Set<AuthorizedMemberCandidate> members;

    public Boolean hasHirerDocument() {
        return this.hirerDocument != null;
    }
}