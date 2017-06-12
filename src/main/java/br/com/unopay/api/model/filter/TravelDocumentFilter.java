package br.com.unopay.api.model.filter;

import br.com.unopay.api.pamcary.translate.KeyBase;
import br.com.unopay.api.pamcary.translate.KeyField;
import br.com.unopay.api.pamcary.translate.KeyFieldReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.UUID;
import lombok.Data;

@Data
@KeyBase(key = "viagem")
public class TravelDocumentFilter implements Serializable {

    public static final long serialVersionUID = 1L;

    public TravelDocumentFilter(){}

    @KeyField(field = "favorecido.documento.numero")
    private String contractorDocument;

    @KeyField(field = "favorecido.documento.tipo")
    private String contractorDocumentType;

    @KeyFieldReference
    private EstablishmentFilter establishment;

    @KeyField(field = "id")
    private String contractCode;

    @JsonIgnore
    @KeyField(field = "transacao.nsu")
    private String transactionNsu;

    public void defineTransaction(){
        transactionNsu = UUID.randomUUID().toString();
    }
}
