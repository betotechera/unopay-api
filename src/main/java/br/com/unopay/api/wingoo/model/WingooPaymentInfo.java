package br.com.unopay.api.wingoo.model;

import br.com.wingoo.userclient.model.User;
import lombok.Data;

@Data
public class WingooPaymentInfo {
    private User user;
    private WingooProductInformation product;

    public WingooPaymentInfo(User user, WingooProductInformation product) {
        this.user = user;
        this.product = product;
    }
}
