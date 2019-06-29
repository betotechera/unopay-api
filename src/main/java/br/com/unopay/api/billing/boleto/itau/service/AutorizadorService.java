package br.com.unopay.api.billing.boleto.itau.service;

import br.com.itau.autorizador.CobrancaClient;
import br.com.itau.autorizador.model.Cobranca;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AutorizadorService {

    private CobrancaClient cobrancaClient;

    @Autowired
    public AutorizadorService(CobrancaClient cobrancaClient) {
        this.cobrancaClient = cobrancaClient;
    }

    public Cobranca registro(Cobranca cobranca) {
        return cobrancaClient.registro(cobranca);
    }
}
