package br.com.unopay.api.billing.boleto.registry;

import br.com.caelum.stella.boleto.Banco;
import br.com.caelum.stella.boleto.bancos.Itau;
import br.com.itau.autorizador.model.Cobranca;
import br.com.unopay.api.bacen.model.PaymentBankAccount;
import br.com.unopay.api.billing.boleto.itau.mapping.ItauBuilder;
import br.com.unopay.api.billing.boleto.itau.service.AutorizadorService;
import br.com.unopay.api.model.Billable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ItauRegistry implements TicketRegistry{

    private AutorizadorService autorizadorService;

    @Value("${unopay.boleto.deadline_in_days}")
    private Integer deadlineInDays;

    @Autowired
    public ItauRegistry(AutorizadorService autorizadorService) {
        this.autorizadorService = autorizadorService;
    }

    @Override
    public String registryTicket(Billable billable, PaymentBankAccount paymentBankAccount, String number) {
        Cobranca cobranca = new ItauBuilder()
                .payer(billable.getPayer()).expirationDays(deadlineInDays)
                .paymentBankAccount(paymentBankAccount)
                .value(billable.getValue())
                .yourNumber(number).build();
        Cobranca registered = autorizadorService.registro(cobranca);
        return registered.getNossoNumero();
    }

    @Override
    public Banco getBank() {
        return new Itau();
    }

    @Override
    public boolean hasBacenCode(Integer bacenCode) {
        return Integer.valueOf(341).equals(bacenCode);
    }
}
