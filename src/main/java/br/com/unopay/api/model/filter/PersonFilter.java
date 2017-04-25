package br.com.unopay.api.model.filter;

import br.com.unopay.api.model.DocumentType;
import br.com.unopay.api.repository.filter.SearchableField;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class PersonFilter implements Serializable{

    public static final long serialVersionUID = 1L;

    public PersonFilter(){}

    @SearchableField(field = "document.number")
    private String documentNumber;

    @SearchableField(field = "document.type")
    private DocumentType documentType;


}
