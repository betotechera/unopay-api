package br.com.unopay.api.wingoo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class Password implements Serializable{

    private static final long serialVersionUID = 8679664832098056775L;

    @JsonProperty("senha")
    private String newPassword;

    @JsonProperty("email")
    private String email;

    @JsonProperty("cpf")
    private String document;

    public Password(String email, String document, String newPassword) {
        this.email = email;
        this.newPassword = newPassword;
        this.document = document;

    }
}
