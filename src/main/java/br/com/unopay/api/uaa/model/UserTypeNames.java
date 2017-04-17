package br.com.unopay.api.uaa.model;

public class UserTypeNames {

    private UserTypeNames() {
        throw new IllegalAccessError("Utility class");
    }

    public static final String ISSUER = "EMISSORA";
    public static final String INSTITUTION = "INSTITUIDOR";
    public static final String ACCREDITED_NETWORK = "CREDENCIADORA";


}