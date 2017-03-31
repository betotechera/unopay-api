package br.com.unopay.api.model;

import br.com.unopay.api.repository.SearchableField;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class PersonFilter implements Serializable{

    public static final Long serialVersionUID = 1L;

    @SearchableField(field = "document.number")
    private String documentNumber;

    @SearchableField(field = "document.type")
    private DocumentType documentType;


}
