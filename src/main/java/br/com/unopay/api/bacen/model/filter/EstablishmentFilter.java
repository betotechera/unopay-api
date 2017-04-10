package br.com.unopay.api.bacen.model.filter;

import br.com.unopay.api.bacen.model.EstablishmentType;
import br.com.unopay.api.repository.filter.SearchableField;

import java.io.Serializable;

public class EstablishmentFilter implements Serializable {

    public static final Long serialVersionUID = 1L;

    @SearchableField(field = "person.document.number")
    private String documentNumber;

    @SearchableField(field = "person.name")
    private String personName;

    @SearchableField
    private EstablishmentType type;

    @SearchableField(field = "person.address.city")
    private String city;

    @SearchableField(field = "person.address.state")
    private String state;
}
