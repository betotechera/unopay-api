package br.com.unopay.api.network.model.filter;

import br.com.unopay.bootcommons.repository.filter.SearchableField;
import java.io.Serializable;
import lombok.Data;

@Data
public class EstablishmentEventFilter implements Serializable {

    public static final Long serialVersionUID = 1L;

    @SearchableField(field = "establishment.network.id")
    private String network;

    @SearchableField(field = "establishment.id")
    private String establishment;
}
