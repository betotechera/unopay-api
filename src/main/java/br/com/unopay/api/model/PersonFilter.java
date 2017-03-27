package br.com.unopay.api.model;

import br.com.unopay.api.repository.SearchableField;

public class PersonFilter {


    public PersonFilter(Document document){
        this.documentNumber = document.getNumber();
        this.documentType = document.getType();
    }

    @SearchableField(field = "document.number")
    private String documentNumber;

    @SearchableField(field = "document.type")
    private DocumentType documentType;


}
