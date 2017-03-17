package br.com.unopay.api.uaa.exception;

import br.com.unopay.bootcommons.exception.UnovationError;

public class Errors {
    public static final UnovationError GROUP_NAME_ALREADY_EXISTS = new UnovationError("GROUP_NAME_ALREADY_EXISTS","Group name already exists");
    public static UnovationError USER_IS_REQUIRED = new UnovationError("USER_IS_REQUIRED","User is required");
    public static UnovationError NAME_IS_REQUIRED = new UnovationError("NAME_IS_REQUIRED","Name is required");
}
