package br.com.unopay.api.network.model.filter;

import br.com.unopay.api.model.State;
import br.com.unopay.api.network.model.EstablishmentType;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import java.io.Serializable;
import java.util.Collection;
import lombok.Data;

@Data
public class EstablishmentFilter implements Serializable {

    public static final Long serialVersionUID = 1L;

    @SearchableField(field = "person.document.number")
    private String documentNumber;

    @SearchableField(field = "network.person.document.number")
    private String accreditedNetwork;

    @SearchableField(field = "network.id")
    private Collection<String> accreditedNetworks;

    @SearchableField(field = "person.name")
    private String personName;

    @SearchableField
    private EstablishmentType type;

    @SearchableField(field = "type")
    private Collection<EstablishmentType> types;

    @SearchableField(field = "person.address.district")
    private String district;

    @SearchableField(field = "person.address.city")
    private String city;

    @SearchableField(field = "person.address.state")
    private State state;

}
