package br.com.unopay.api.wingoo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class Password implements Serializable{

    private static final long serialVersionUID = 8679664832098056775L;

    @JsonProperty("current_password")
    private String currentPassword;

    @JsonProperty("new_password")
    private String newPassword;

    @JsonProperty("password_confirmation")
    private String passwordConfirmation;

    public Password(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.passwordConfirmation = newPassword;

    }
}
