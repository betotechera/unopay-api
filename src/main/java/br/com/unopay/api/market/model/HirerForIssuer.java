package br.com.unopay.api.market.model;

import br.com.unopay.api.bacen.model.Hirer;
import lombok.Data;

@Data
public class HirerForIssuer {

    private Hirer hirer;
    private HirerNegotiation hirerNegotiation;
}
