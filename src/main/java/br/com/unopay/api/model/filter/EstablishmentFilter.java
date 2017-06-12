package br.com.unopay.api.model.filter;

import br.com.unopay.api.pamcary.translate.KeyBase;
import br.com.unopay.api.pamcary.translate.KeyField;
import lombok.Data;

@Data
@KeyBase(key = "pontoapoio")
public class EstablishmentFilter {

    @KeyField(field = "documento.numero")
    private String document;
}
