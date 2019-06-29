package br.com.unopay.api.billing.boleto.registry;

import br.com.caelum.stella.boleto.Banco;
import br.com.caelum.stella.boleto.bancos.Santander;
import br.com.unopay.api.bacen.model.PaymentBankAccount;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl.TicketRequest;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.TituloDto;
import br.com.unopay.api.billing.boleto.santander.service.CobrancaOnlineService;
import br.com.unopay.api.billing.boleto.santander.mapping.CobrancaOlnineBuilder;
import br.com.unopay.api.model.Billable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SantanderRegistry implements TicketRegistry {

    private CobrancaOnlineService cobrancaOnlineService;

    @Value("${unopay.boleto.deadline_in_days}")
    private Integer deadlineInDays;

    @Autowired
    public SantanderRegistry(CobrancaOnlineService cobrancaOnlineService) {
        this.cobrancaOnlineService = cobrancaOnlineService;
    }

    @Override
    public String registryTicket(Billable billable, PaymentBankAccount paymentBankAccount, String number) {
        List<TicketRequest.Dados.Entry> entries = new CobrancaOlnineBuilder()
                .payer(billable.getPayer()).expirationDays(deadlineInDays)
                .paymentBankAccount(paymentBankAccount)
                .value(billable.getValue())
                .yourNumber(number).build();

        TituloDto tituloDto = cobrancaOnlineService.getTicket(entries, paymentBankAccount.getStation());
        return Integer.valueOf(tituloDto.getNossoNumero()).toString();
    }

    @Override
    public Banco getBank() {
        return new Santander();
    }

    @Override
    public boolean hasBacenCode(Integer bacenCode) {
        return Integer.valueOf(33).equals(bacenCode);
    }

}
