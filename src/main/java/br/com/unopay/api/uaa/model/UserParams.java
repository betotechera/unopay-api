package br.com.unopay.api.uaa.model;

import lombok.Data;

@Data
public class UserParams {

    private String name;
    private String email;
    private String groupName;
}
