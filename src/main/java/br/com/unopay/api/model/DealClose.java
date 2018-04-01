package br.com.unopay.api.model;

import br.com.unopay.api.market.model.AuthorizedMemberCandidate;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

@Getter
public class DealClose {

    public DealClose(Person person, String hirerDocument, String productCode) {
        this.person = person;
        this.hirerDocument = hirerDocument;
        this.productCode = productCode;
        this.members = new HashSet<>();
    }

    public DealClose(Person person, String productCode, Set<AuthorizedMemberCandidate> members) {
        this.person = person;
        this.productCode = productCode;
        this.members = members;
    }

    public DealClose(Person person, String productCode) {
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
