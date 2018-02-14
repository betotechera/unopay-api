package br.com.unopay.api.market.model;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.model.validation.group.Create;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HirerForIssuer {

    @NotNull(groups = {Create.class})
    private Hirer hirer;
    @NotNull(groups = {Create.class})
    private HirerNegotiation hirerNegotiation;

    public String productId(){
        if(getHirerNegotiation() != null && getHirerNegotiation().getProduct() !=null){
            return getHirerNegotiation().getProduct().getId();
        }
        return null;
    }

}
