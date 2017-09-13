package br.com.unopay.api.order.model.filter;

import br.com.unopay.api.model.Period;
import br.com.unopay.api.order.model.OrderStatus;
import br.com.unopay.api.order.model.OrderType;
import br.com.unopay.api.repository.filter.SearchableField;
import lombok.Data;

@Data
public class OrderFilter {

    @SearchableField(field = "person.document.number")
    private String document;

    @SearchableField(field = "product.code")
    private String product;

    @SearchableField
    private OrderType type;

    @SearchableField
    private OrderStatus status;

    @SearchableField(field = "createDateTime")
    private Period createdAt;
}