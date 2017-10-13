package br.com.unopay.api.billing.boleto.model.filter;

import br.com.unopay.api.repository.filter.SearchableField;
import java.util.List;
import lombok.Data;

@Data
public class BoletoFilter {

    @SearchableField
    List<String> orderId;
}
