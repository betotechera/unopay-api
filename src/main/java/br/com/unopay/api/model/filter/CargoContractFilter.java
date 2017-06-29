package br.com.unopay.api.model.filter;

import br.com.unopay.api.pamcary.translate.KeyBase;
import br.com.unopay.api.pamcary.translate.KeyField;
import br.com.unopay.api.pamcary.translate.KeyFieldReference;
import java.io.Serializable;
import lombok.Data;

@Data
@KeyBase(key = "viagem")
public class CargoContractFilter implements Serializable {

    public static final long serialVersionUID = 1L;

    public CargoContractFilter(){}

    @KeyField(baseField = "favorecido.documento.numero")
    private String contractorDocument;

    @KeyField(baseField = "favorecido.documento.tipo")
    private String contractorDocumentType;

    @KeyFieldReference
    private EstablishmentFilter establishment;

    @KeyField(baseField = "id")
    private String contractCode;

}
