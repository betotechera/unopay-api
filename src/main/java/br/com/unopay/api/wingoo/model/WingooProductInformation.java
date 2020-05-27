package br.com.unopay.api.wingoo.model;

import br.com.unopay.api.model.Product;
import lombok.Data;

@Data
public class WingooProductInformation {

    private String id;

    public WingooProductInformation(Product product) {
        this.id = product.getId();
    }
}
