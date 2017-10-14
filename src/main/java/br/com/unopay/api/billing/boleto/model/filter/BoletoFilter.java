package br.com.unopay.api.billing.boleto.model.filter;

import br.com.unopay.api.repository.filter.SearchableField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Data;

@Data
public class BoletoFilter implements Serializable {

    public static final Long serialVersionUID = 1L;


    @SearchableField
    List<String> orderId;

    public List<String> getOrderId(){
        Optional<List<String>> list = Optional.ofNullable(this.orderId);
        return list.orElse(new ArrayList<>());
    }
}
