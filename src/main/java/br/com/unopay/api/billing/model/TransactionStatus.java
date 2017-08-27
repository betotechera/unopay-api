package br.com.unopay.api.billing.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum TransactionStatus implements DescriptableEnum{

    ERROR(true,"Erro"),
    PENDING(true,"Pendente"),
    AUTHORIZED(false,"Autorizada"),
    IN_ANALYSIS(false,"Em analise"),
    DENIED(true,"Negada"),
    REFUND(false,"Estornada"),
    REFUND_PENDING(true,"Estorno pendente"),
    CANCEL_PENDING(true,"Cancelamento pendente"),
    CANCELED(false,"Cancelada"),
    CANCEL_DENIED(false,"Cancelamento negado"),
    CAPTURE_RECEIVED(true,"Captura recebida"),
    CAPTURED(false,"Capturada"),
    CAPTURE_DENIED(false,"Captura negada");

    private boolean processing;
    private String description;

    TransactionStatus(boolean processing, String Description) {
        this.processing = processing;
        description = Description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public boolean isPending() {
        return processing;
    }
}
