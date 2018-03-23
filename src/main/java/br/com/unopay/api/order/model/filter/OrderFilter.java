package br.com.unopay.api.order.model.filter;

import br.com.unopay.api.order.model.OrderType;
import br.com.unopay.api.order.model.PaymentStatus;
import br.com.unopay.bootcommons.model.Period;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import lombok.Data;

@Data
public class OrderFilter {

    @SearchableField(field = "person.document.number")
    private String document;

    @SearchableField(field = "product.code")
    private String product;

    @SearchableField(field = "product.issuer.id")
    private String issuer;

    @SearchableField
    private OrderType type;

    @SearchableField
    private PaymentStatus status;

    @SearchableField(field = "createDateTime")
    private Period createdAt;
}
