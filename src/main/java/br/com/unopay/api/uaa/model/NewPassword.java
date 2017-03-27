package br.com.unopay.api.uaa.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class NewPassword {

    private String token;
    @NotNull
    private String password;

}
