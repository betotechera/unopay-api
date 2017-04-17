package br.com.unopay.api;

public class Permissions {

    private Permissions() {
        throw new IllegalAccessError("Utility class");
    }

    public static final String MANAGE_GROUPS = "hasRole('MANAGE_GROUPS')";
}
