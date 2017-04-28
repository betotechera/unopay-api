package br.com.unopay.api.bacen.model.filter;

import br.com.unopay.api.model.State;
import br.com.unopay.api.repository.filter.SearchableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ContractorFilter implements Serializable {

    public static final Long serialVersionUID = 1L;

    @SearchableField(field = "person.document.number")
    private String documentNumber;

    @SearchableField(field = "person.name")
    private String personName;

    @SearchableField(field = "person.address.city")
    private String city;

    @SearchableField(field = "person.address.state")
    private State state;

}
