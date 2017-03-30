package br.com.unopay.api.bacen.model;

import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import static javax.persistence.EnumType.STRING;

@Data
@Embeddable
public class InvoiceReceipt {

    @Enumerated(STRING)
    @Column(name="invoice_receipt_type")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class, Update.class})
    private InvoiceReceiptType type;

    @Enumerated(STRING)
    @Column(name="invoice_receipt_period")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class, Update.class})
    private Period period;

}
