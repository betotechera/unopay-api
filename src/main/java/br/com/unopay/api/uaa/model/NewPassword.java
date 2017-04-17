package br.com.unopay.api.uaa.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class NewPassword  implements Serializable {

    public static final long serialVersionUID = 1L;

    public NewPassword(){}

    private String token;
    @NotNull
    private String password;

}
