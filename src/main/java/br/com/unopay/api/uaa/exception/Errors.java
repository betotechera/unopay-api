package br.com.unopay.api.uaa.exception;

import br.com.unopay.bootcommons.exception.UnovationError;

public class Errors {
    public static final UnovationError GROUP_NAME_ALREADY_EXISTS = new UnovationError("GROUP_NAME_ALREADY_EXISTS","Group name already exists");

    public static final UnovationError USER_REQUIRED = new UnovationError("USER_REQUIRED","User required");

    public static final UnovationError GROUP_REQUIRED = new UnovationError("GROUP_REQUIRED","Group required");

    public static final UnovationError KNOWN_MEMBERS_REQUIRED = new UnovationError("KNOWN_MEMBERS_REQUIRED","Known members required");

    public static final UnovationError KNOWN_AUTHORITIES_REQUIRED = new UnovationError("KNOWN_AUTHORITIES_REQUIRED","Known authorities required");

    public static final UnovationError KNOWN_GROUP_REQUIRED = new UnovationError("KNOWN_GROUPS_REQUIRED","Known groups required");
}

