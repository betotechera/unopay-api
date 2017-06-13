package br.com.unopay.api.uaa.model;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewPassword  implements Serializable {

    public static final long serialVersionUID = 1L;

    public NewPassword(){}

    private String token;
    @NotNull
    private String password;

}
