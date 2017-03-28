package br.com.unopay.api.model;

import br.com.unopay.api.repository.SearchableField;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PersonFilter {

    @SearchableField(field = "document.number")
    private String documentNumber;

    @SearchableField(field = "document.type")
    private DocumentType documentType;


}
