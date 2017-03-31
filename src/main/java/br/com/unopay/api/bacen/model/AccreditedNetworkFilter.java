package br.com.unopay.api.bacen.model;

import br.com.unopay.api.repository.SearchableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class AccreditedNetworkFilter implements Serializable{

    public static final Long serialVersionUID = 1L;

    @SearchableField(field = "person.document.number")
    private String documentNumber;

    @SearchableField(field = "person.name")
    private String name;

}
