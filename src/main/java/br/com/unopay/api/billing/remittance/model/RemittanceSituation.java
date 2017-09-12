package br.com.unopay.api.billing.remittance.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum RemittanceSituation  implements DescriptableEnum {

    PROCESSING("1","em processamento"),
    REMITTANCE_FILE_GENERATED("2","gerado arquivo cnab de pagamento"),
    REMITTANCE_FILE_SUBMITTED("3","enviado arquivo cnab de pagamento"),
    RETURN_PROCESSED_SUCCESSFULLY("4","retorno processado com sucesso"),
    RETURN_PROCESSED_WITH_ERROR("5","retorno processado com erro"),
    CANCELED_REMITTANCE("6","remessa cancelada");

    private String code;
    private String description;

    RemittanceSituation(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }
}
