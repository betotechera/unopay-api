package br.com.unopay.api.bacen.model;

import br.com.unopay.api.repository.SearchableField;
import lombok.Data;

@Data
public class AccreditedNetworkFilter {

    @SearchableField(field = "person.document.number")
    private String documentNumber;

    @SearchableField(field = "person.name")
    private String name;

}
