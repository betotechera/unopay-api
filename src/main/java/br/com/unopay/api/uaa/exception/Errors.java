package br.com.unopay.api.uaa.exception;

import br.com.unopay.bootcommons.exception.UnovationError;

public final class Errors {

    public static final UnovationError GROUP_NAME_ALREADY_EXISTS = new UnovationError("GROUP_NAME_ALREADY_EXISTS","Group name already exists");

    public static final UnovationError USER_REQUIRED = new UnovationError("USER_REQUIRED","User required");

    public static final UnovationError PERSON_REQUIRED = new UnovationError("PERSON_REQUIRED","Person required");

    public static final UnovationError PERSON_NOT_FOUND = new UnovationError("PERSON_NOT_FOUND","Person not found");

    public static final UnovationError PAYMENT_ACCOUNT_REQUIRED = new UnovationError("PAYMENT_ACCOUNT_REQUIRED","Payment account required");

    public static final UnovationError BANK_ACCOUNT_NOT_FOUND = new UnovationError("BANK_ACCOUNT_NOT_FOUND","Bank account not found");

    public static final UnovationError MOVEMENT_ACCOUNT_REQUIRED = new UnovationError("MOVEMENT_ACCOUNT_REQUIRED","Movement account required");

    public static final UnovationError USER_TYPE_REQUIRED = new UnovationError("USER_TYPE_REQUIRED","User type required");

    public static final UnovationError USER_TYPE_NOT_FOUND = new UnovationError("USER_TYPE_NOT_FOUND","User type not found");

    public static final UnovationError ISSUER_NOT_FOUND = new UnovationError("ISSUER_NOT_FOUND","Issuer not found");

    public static final UnovationError USER_NOT_FOUND = new UnovationError("USER_NOT_FOUND","User not found");

    public static final UnovationError TOKEN_NOT_FOUND = new UnovationError("TOKEN_NOT_FOUND","Token not found");

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

    public static final UnovationError USER_EMAIL_ALREADY_EXISTS =  new UnovationError("USER_EMAIL_ALREADY_EXISTS","User email already exists");

    public static final UnovationError PAYMENT_RULE_GROUP_CODE_ALREADY_EXISTS =  new UnovationError("PAYMENT_RULE_GROUP_CODE_ALREADY_EXISTS","Payment rule code already exists");

    public static final UnovationError USER_TYPE_MUST_SET_A_PAYMENT_RULE_GROUP =  new UnovationError("USER_TYPE_MUST_SET_A_PAYMENT_RULE_GROUP","User type must set a PaymentRuleGroup");

    public static final UnovationError PAYMENT_RULE_GROUP_NOT_FOUND  =  new UnovationError("PAYMENT_RULE_GROUP_NOT_FOUND","PaymentRuleGroup not found");

    public static final UnovationError PAYMENT_RULE_GROUP_WITH_USERS =  new UnovationError("PAYMENT_RULE_GROUP_WITH_USERS","PaymentRuleGroup has users");

    public static final UnovationError PAYMENT_RULE_GROUP_NAME_REQUIRED =  new UnovationError("PAYMENT_RULE_GROUP_NAME_REQUIRED","Name is Required");

    public static final UnovationError PAYMENT_RULE_GROUP_CODE_REQUIRED =  new UnovationError("PAYMENT_RULE_GROUP_CODE_REQUIRED","Code is Required");

    public static final UnovationError USER_RELATIONSHIP_REQUIRED =  new UnovationError("USER_RELATIONSHIP_REQUIRED","UserRelationship is Required");

    public static final UnovationError LARGE_PAYMENT_RULE_GROUP_NAME =  new UnovationError("LARGE_PAYMENT_RULE_GROUP_NAME","Name is too large");

    public static final UnovationError SHORT_PAYMENT_RULE_GROUP_NAME =  new UnovationError("SHORT_PAYMENT_RULE_GROUP_NAME","Name is too short");

    public static final UnovationError LARGE_PAYMENT_RULE_GROUP_CODE =  new UnovationError("LARGE_PAYMENT_RULE_GROUP_CODE","Code is too large");

    public static final UnovationError SHORT_PAYMENT_RULE_GROUP_CODE =  new UnovationError("SHORT_PAYMENT_RULE_GROUP_CODE","Code is too short");

    public static final UnovationError INVALID_DOCUMENT_TYPE_FOR_USER = new UnovationError("INVALID_DOCUMENT_TYPE_FOR_USER","Invalid document type for user");

    public static final UnovationError LEGAL_PERSON_DETAIL_IS_REQUIRED_FOR_LEGAL_PERSON = new UnovationError("LEGAL_PERSON_DETAIL_IS_REQUIRED_FOR_LEGAL_PERSON","LegalPersonDetail is required for PersonType.LEGAL");
    public static final UnovationError PERSON_DOCUMENT_ALREADY_EXISTS  = new UnovationError("PERSON_DOCUMENT_ALREADY_EXISTS","Person with document already exists");
    public static final UnovationError PERSON_WITH_DOCUMENT_NOT_FOUND = new UnovationError("PERSON_WITH_DOCUMENT_NOT_FOUND","Person with document not found");
    public static final UnovationError PERSON_INSTITUTION_ALREADY_EXISTS = new UnovationError("PERSON_INSTITUTION_ALREADY_EXISTS","Person Institution already exists");
    public static final UnovationError PAYMENT_RULE_GROUP_WITH_INSTITUTIONS = new UnovationError("PAYMENT_RULE_GROUP_WITH_INSTITUTIONS","PaymentRuleGroup has Institutions");

    private Errors(){}

}