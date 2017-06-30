package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum BatchClosingSituation  implements DescriptableEnum {

    PROCESSING_MANUAL_BATCH("Em processamento fechamento manual", "1"),
    PROCESSING_AUTOMATIC_BATCH("Em processamento fechamento Automático", "2"),
    DOCUMENT_RECEIVED("Documentos recebidos", "3"), PAYMENT_RELEASED("Liberado para pagamento","4"),
    CANCELED("Cancelado", "5"),  FINALIZED("Finalizado","6");

    private String description;

    public String getCode() {
        return code;
    }

    private String code;

    BatchClosingSituation(String description, String code) {
        this.description = description;
        this.code = code;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
