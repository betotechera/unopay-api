package br.com.unopay.api.network.model.filter;

import br.com.unopay.api.model.State;
import br.com.unopay.api.network.model.EstablishmentType;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import java.io.Serializable;
import java.util.Collection;
import lombok.Data;

@Data
public class BranchFilter implements Serializable {

    public static final Long serialVersionUID = 1L;

    @SearchableField
    private String shortName;

    @SearchableField
    private String fantasyName;

    @SearchableField(field = "headOffice.type")
    private EstablishmentType headOfficeType;

    @SearchableField(field = "headOffice.person.document.number")
    private String headOfficeDocument;

    @SearchableField(field = "headOffice.id")
    private String headOffice;

    @SearchableField(field = "headOffice.id")
    private Collection<String> headOffices;

    @SearchableField(field = "headOffice.network.id")
    private String network;

    @SearchableField(field = "address.district")
    private String district;

    @SearchableField(field = "address.city")
    private String city;

    @SearchableField(field = "address.state")
    private State state;
}
