package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.billing.boleto.model.TicketPaymentSource;
import java.math.BigDecimal;
import java.util.Date;

public interface Billable {

    BigDecimal getValue();

    Person getPayer();

    Issuer getIssuer();

    String getId();

    String getNumber();

    Date getCreateDateTime();

    String getBillingMail();

    TicketPaymentSource getPaymentSource();
}
