package br.com.unopay.api.uaa.exception;

import br.com.unopay.bootcommons.exception.UnovationError;

public class Errors {
    public static final UnovationError GROUP_NAME_ALREADY_EXISTS = new UnovationError("GROUP_NAME_ALREADY_EXISTS","Group name already exists");

    public static final UnovationError USER_REQUIRED = new UnovationError("USER_REQUIRED","User required");

    public static final UnovationError USER_TYPE_REQUIRED = new UnovationError("USER_TYPE_REQUIRED","User type required");

    public static final UnovationError USER_TYPE_NOT_FOUND = new UnovationError("USER_TYPE_NOT_FOUND","User type not found");

    public static final UnovationError USER_NOT_FOUND = new UnovationError("USER_NOT_FOUND","User not found");

    public static final UnovationError GROUP_ID_REQUIRED = new UnovationError("GROUP_ID_REQUIRED","Group id required");

    public static final UnovationError GROUP_NOT_FOUND = new UnovationError("GROUP_NOT_FOUND","Group not found");

    public static final UnovationError GROUP_NAME_REQUIRED = new UnovationError("GROUP_NAME_REQUIRED","Group name required");

    public static final UnovationError LARGE_GROUP_NAME = new UnovationError("LARGE_GROUP_NAME","Lager group name. Max 50 characters");

    public static final UnovationError LARGE_GROUP_DESCRIPTION = new UnovationError("LARGE_GROUP_DESCRIPTION","Lager group name. Max 250 characters");

    public static final UnovationError SHORT_GROUP_NAME = new UnovationError("SHORT_GROUP_NAME","Short group name. Min 3 characters");

    public static final UnovationError KNOWN_MEMBERS_REQUIRED = new UnovationError("KNOWN_MEMBERS_REQUIRED","Known members required");

    public static final UnovationError KNOWN_AUTHORITIES_REQUIRED = new UnovationError("KNOWN_AUTHORITIES_REQUIRED","Known authorities required");

    public static final UnovationError KNOWN_GROUP_REQUIRED = new UnovationError("KNOWN_GROUPS_REQUIRED","Known groups required");

    public static final UnovationError GROUP_WITH_MEMBERS =  new UnovationError("GROUP_WITH_MEMBERS","Cannot exclude group with members");

    public static final UnovationError UNKNOWN_GROUP_FOUND =  new UnovationError("UNKNOWN_GROUP_FOUND","Unknown group found in group list");

    public static UnovationError USER_EMAIL_ALREADY_EXISTS =  new UnovationError("USER_EMAIL_ALREADY_EXISTS","User email already exists");

    public static UnovationError PAYMENT_RULE_GROUP_CODE_ALREADY_EXISTS =  new UnovationError("PAYMENT_RULE_GROUP_CODE_ALREADY_EXISTS","Payment rule code already exists");

    public static UnovationError USER_TYPE_MUST_SET_A_PAYMENT_RULE_GROUP =  new UnovationError("USER_TYPE_MUST_SET_A_PAYMENT_RULE_GROUP","User type must set a PaymentRuleGroup");

    public static UnovationError PAYMENT_RULE_GROUP_NOT_FOUND =  new UnovationError("PAYMENT_RULE_GROUP_NOT_FOUND","PaymentRuleGroup not found");
}

