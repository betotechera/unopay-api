package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import lombok.Data;

import static javax.persistence.EnumType.STRING;

@Data
@Embeddable
public class InvoiceReceipt implements Serializable{

    public static final long serialVersionUID = 1L;

    public InvoiceReceipt(){}

    @Enumerated(STRING)
    @Column(name="invoice_receipt_type")
    @JsonView({Views.AccreditedNetwork.Detail.class, Views.Establishment.Detail.class, Views.Branch.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private InvoiceReceiptType type;

    @Enumerated(STRING)
    @Column(name="invoice_receipt_period")
    @JsonView({Views.AccreditedNetwork.Detail.class, Views.Establishment.Detail.class, Views.Branch.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private RecurrencePeriod period;

}
