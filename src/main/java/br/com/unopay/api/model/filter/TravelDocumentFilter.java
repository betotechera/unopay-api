package br.com.unopay.api.model.filter;

import br.com.unopay.api.pamcary.translate.KeyField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.UUID;
import lombok.Data;

@Data
public class TravelDocumentFilter implements Serializable {

    public static final long serialVersionUID = 1L;

    public TravelDocumentFilter(){}

    @KeyField(key = "viagem.favorecido.documento.numero")
    private String contractorDocument;

    @KeyField(key = "viagem.favorecido.documento.tipo")
    private String contractorDocumentType;

    @KeyField(key = "pontoapoio.documento.numero")
    private String establishmentDocument;

    @KeyField(key = "viagem.id")
    private String contractCode;

    @JsonIgnore
    @KeyField(key = "viagem.transacao.nsu")
    private String transactionNsu;

    public void defineTransaction(){
        transactionNsu = UUID.randomUUID().toString();
    }
}
