package br.com.unopay.api.bacen.model;

import br.com.unopay.api.repository.SearchableField;
import lombok.Data;

@Data
public class IssuerFilter {

    @SearchableField(field = "person.document.number")
    private String documentNumber;

    @SearchableField(field = "person.legalPersonDetail.fantasyName")
    private String fantasyName;

    @SearchableField(field = "person.address.streetName")
    private String streetName;

    @SearchableField(field = "person.address.city")
    private String city;

    @SearchableField(field = "person.address.state")
    private String state;
}
