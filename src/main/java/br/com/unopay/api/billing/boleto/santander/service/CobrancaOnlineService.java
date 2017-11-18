package br.com.unopay.api.billing.boleto.santander.service;

import br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl.TicketEndpointService;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.CobrancaEndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CobrancaOnlineService {

    private TicketEndpointService ticketEndpointService;
    private CobrancaEndpointService cobrancaEndpointService;

    @Autowired
    public CobrancaOnlineService(TicketEndpointService ticketEndpointService,
                                 CobrancaEndpointService cobrancaEndpointService) {
        this.ticketEndpointService = ticketEndpointService;
        this.cobrancaEndpointService = cobrancaEndpointService;
    }

}
