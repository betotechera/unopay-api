package br.com.unopay.api.network.model.filter;

import br.com.wingoo.reusable.repository.filter.SearchableField;
import lombok.Data;

@Data
public class EstablishmentEventFilter {

    @SearchableField(field = "establishment.network.id")
    private String network;

    @SearchableField(field = "establishment.id")
    private String establishment;
}
