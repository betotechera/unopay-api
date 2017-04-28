package br.com.unopay.api.model;

import br.com.unopay.api.filter.DescriptionEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.unopay.api.model.PersonType.LEGAL;
import static br.com.unopay.api.model.PersonType.PHYSICAL;

public enum DocumentType implements DescriptionEnum {

    CNPJ(LEGAL),
    STATE_REGISTRATION(LEGAL),
    CPF(PHYSICAL),
    RNTRC,
    PIS(PHYSICAL),
    CNH(PHYSICAL),
    PASSPORT(PHYSICAL),
    RG(PHYSICAL);

    private List<PersonType> personTypes;

    DocumentType() {
        this.personTypes = new ArrayList<>();
        this.personTypes.addAll(Arrays.asList(PersonType.values()));
    }


    DocumentType(PersonType personType) {
        this.personTypes = new ArrayList<>();
        this.personTypes.add(personType);
    }

    public boolean isValidDocumentFor(PersonType personType){
      return personTypes.contains(personType);
    }

    public String getDescription(){
        return personTypes.stream().map(PersonType::getDescription).collect(Collectors.joining());
    }



}
