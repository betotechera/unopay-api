package br.com.unopay.api.bacen.model;

import br.com.unopay.api.repository.SearchableField;
import lombok.Data;

@Data
public class IssuerFilter {

    @SearchableField
    private String tax;
}
