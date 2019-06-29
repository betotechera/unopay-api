package br.com.unopay.api.billing.boleto.registry;

import br.com.caelum.stella.boleto.Banco;
import br.com.unopay.api.bacen.model.PaymentBankAccount;
import br.com.unopay.api.model.Billable;

public interface TicketRegistry {

    String registryTicket(Billable billable, PaymentBankAccount paymentBankAccount, String number);

    Banco getBank();

    boolean hasBacenCode(Integer bacenCode);
}
