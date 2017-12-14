package br.com.unopay.api.bacen.model.filter;

import br.com.unopay.api.model.State;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import java.io.Serializable;
import lombok.Data;

@Data
public class ContractorFilter implements Serializable {

    public static final Long serialVersionUID = 1L;

    @SearchableField(field = "person.document.number")
    private String documentNumber;

    @SearchableField(field = "person.physicalPersonDetail.email")
    private String email;

    @SearchableField(field = "person.name")
    private String personName;

    @SearchableField(field = "person.address.city")
    private String city;

    @SearchableField(field = "person.address.state")
    private State state;

    @SearchableField(field = "contracts.hirer.id")
    private String hirer;

    @SearchableField(field = "contracts.product.issuer.id")
    private String issuer;

}
