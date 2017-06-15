package br.com.unopay.api.model;

import static br.com.unopay.api.model.PersonType.LEGAL;
import static br.com.unopay.api.model.PersonType.PHYSICAL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum DocumentType{

    CNPJ(LEGAL,"1"),
    STATE_REGISTRATION(LEGAL,"0"),
    CPF(PHYSICAL,"2"),
    RNTRC("0"),
    PIS(PHYSICAL,"0"),
    CNH(PHYSICAL, "0"),
    PASSPORT(PHYSICAL, "0"),
    RG(PHYSICAL, "0");

    private List<PersonType> personTypes;

    public String getCode() {
        return code;
    }

    private String code;

    DocumentType(String code) {
        this.code = code;
        this.personTypes = new ArrayList<>();
        this.personTypes.addAll(Arrays.asList(PersonType.values()));
    }


    DocumentType(PersonType personType, String code) {
        this.code = code;
        this.personTypes = new ArrayList<>();
        this.personTypes.add(personType);
    }

    public boolean isValidDocumentFor(PersonType personType){
      return personTypes.contains(personType);
    }

}
