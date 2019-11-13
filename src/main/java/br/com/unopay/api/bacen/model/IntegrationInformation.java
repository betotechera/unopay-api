package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class IntegrationInformation implements Serializable, Updatable {

    public static final long serialVersionUID = 1L;

    public IntegrationInformation(){}

    @Size(min = 3, max = 50)
    @Column(name = "payzen_shop_id")
    @JsonView({Views.Issuer.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private String payzenShopId;

    @Size(min = 5, max = 50)
    @Column(name = "payzen_shop_key")
    @JsonView({Views.Issuer.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private String payzenShopKey;

    @Size(min = 3, max = 50)
    @Column(name = "wingoo_client_id")
    @JsonView({Views.Issuer.Detail.class})
    private String wingooClientId;

    @Size(min = 5, max = 256)
    @Column(name = "wingoo_client_secret")
    @JsonView({Views.Issuer.Detail.class})
    private String wingooClientSecret;

    public void validate() {
        if (payzenShopId == null)
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.PAYZEN_SHOP_ID_REQUIRED);
        if (payzenShopKey == null)
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.PAYZEN_SHOP_KEY_REQUIRED);
    }
}
