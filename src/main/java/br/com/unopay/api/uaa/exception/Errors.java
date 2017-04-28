package br.com.unopay.api.uaa.exception;

import br.com.unopay.bootcommons.exception.UnovationError;

public final class Errors {


    private Errors() {
        throw new IllegalAccessError("Utility class");
    }

    /* event */
    public static final UnovationError
            TYPE_REQUIRED = new UnovationError("TYPE_REQUIRED","type required");
    public static final UnovationError
            EVENT_NOT_FOUND = new UnovationError("EVENT_NOT_FOUND","Event not found");
    public static final UnovationError QUANTITY_UNITY_REQUIRED =
            new UnovationError("QUANTITY_UNITY_REQUIRED","quantityUnity is required");
    public static final UnovationError EVENT_NAME_ALREADY_EXISTS =
            new UnovationError("EVENT_NAME_ALREADY_EXISTS","Event name already exists");
    public static final UnovationError EVENT_CODE_ALREADY_EXISTS =
            new UnovationError("EVENT_CODE_ALREADY_EXISTS","Event code already exists");

    /* service */
    public static final UnovationError
            SERVICE_NOT_FOUND = new UnovationError("SERVICE_NOT_FOUND","Service not found");
    public static final UnovationError
            SERVICE_REQUIRED = new UnovationError("SERVICE_REQUIRED","Service required");
    public static final UnovationError INVALID_TAX_PERCENT =
            new UnovationError("INVALID_TAX_PERCENT","taxPercent must be between 0 and 1");
    public static final UnovationError
            SERVICE_WITH_EVENTS = new UnovationError("SERVICE_WITH_EVENTS","Service has events");
    public static final UnovationError LEAST_ONE_TAX_REQUIRED =
            new UnovationError("LEAST_ONE_TAX_REQUIRED","Least one service tax required");
    public static final UnovationError SERVICE_NAME_ALREADY_EXISTS =
            new UnovationError("SERVICE_NAME_ALREADY_EXISTS","Service name already exists");
    public static final UnovationError SERVICE_CODE_ALREADY_EXISTS =
            new UnovationError("SERVICE_CODE_ALREADY_EXISTS","Service code already exists");



    /* bank account */
    public static final UnovationError BANK_ACCOUNT_NOT_FOUND =
            new UnovationError("BANK_ACCOUNT_NOT_FOUND","Bank account not found");
    public static final UnovationError
            BANK_NOT_FOUND = new UnovationError("BANK_NOT_FOUND","Bank not found");
    public static final UnovationError
            BANK_REQUIRED = new UnovationError("BANK_REQUIRED","Bank required");
    public static final UnovationError
            BANK_CODE_REQUIRED = new UnovationError("BANK_CODE_REQUIRED","Bank code required");
    public static final UnovationError
            AGENCY_REQUIRED = new UnovationError("AGENCY_REQUIRED","Agency required");
    public static final UnovationError ACCOUNT_NUMBER_REQUIRED =
            new UnovationError("ACCOUNT_NUMBER_REQUIRED","Account number required");

    /* issuer */
    public static final UnovationError PAYMENT_ACCOUNT_REQUIRED =
            new UnovationError("PAYMENT_ACCOUNT_REQUIRED","Payment account required");
    public static final UnovationError PAYMENT_ACCOUNT_ID_REQUIRED =
            new UnovationError("PAYMENT_ACCOUNT_ID_REQUIRED","Payment account id required");
    public static final UnovationError PAYMENT_ACCOUNT_NOT_FOUND =
            new UnovationError("PAYMENT_ACCOUNT_NOT_FOUND","Payment account not found");
    public static final UnovationError MOVEMENT_ACCOUNT_REQUIRED =
            new UnovationError("MOVEMENT_ACCOUNT_REQUIRED","Movement account required");
    public static final UnovationError ISSUER_NOT_FOUND =
            new UnovationError("ISSUER_NOT_FOUND","Issuer not found");
    public static final UnovationError USER_TYPE_MUST_SET_AN_ISSUER =
            new UnovationError("USER_TYPE_MUST_SET_AN_ISSUER","UserType must set an Issuer");
    public static final UnovationError ISSUER_WITH_USERS=
            new UnovationError("ISSUER_WITH_USERS","Issuer with users.");
    public static final UnovationError ISSUER_ID_REQUIRED=
            new UnovationError("ISSUER_ID_REQUIRED","Issuer id required");

    /* uaa */
    public static final UnovationError
            USER_TYPE_REQUIRED = new UnovationError("USER_TYPE_REQUIRED","User type required");
    public static final UnovationError
            USER_TYPE_NOT_FOUND = new UnovationError("USER_TYPE_NOT_FOUND","User type not found");
    public static final UnovationError GROUP_NAME_ALREADY_EXISTS =
            new UnovationError("GROUP_NAME_ALREADY_EXISTS","Group name already exists");
    public static final UnovationError
            USER_REQUIRED = new UnovationError("USER_REQUIRED","User required");
    public static final UnovationError
            USER_NOT_FOUND = new UnovationError("USER_NOT_FOUND","User not found");
    public static final UnovationError
            TOKEN_NOT_FOUND = new UnovationError("TOKEN_NOT_FOUND","Token not found");
    public static final UnovationError
            GROUP_ID_REQUIRED = new UnovationError("GROUP_ID_REQUIRED","Group id required");
    public static final UnovationError
            GROUP_NOT_FOUND = new UnovationError("GROUP_NOT_FOUND","Group not found");
    public static final UnovationError
            GROUP_NAME_REQUIRED = new UnovationError("GROUP_NAME_REQUIRED","Group name required");
    public static final UnovationError LARGE_GROUP_NAME =
            new UnovationError("LARGE_GROUP_NAME","Lager group name. Max 50 characters");
    public static final UnovationError LARGE_GROUP_DESCRIPTION =
            new UnovationError("LARGE_GROUP_DESCRIPTION","Lager group name. Max 250 characters");
    public static final UnovationError SHORT_GROUP_NAME =
            new UnovationError("SHORT_GROUP_NAME","Short group name. Min 3 characters");
    public static final UnovationError KNOWN_MEMBERS_REQUIRED =
            new UnovationError("KNOWN_MEMBERS_REQUIRED","Known members required");
    public static final UnovationError KNOWN_AUTHORITIES_REQUIRED =
            new UnovationError("KNOWN_AUTHORITIES_REQUIRED","Known authorities required");
    public static final UnovationError
            KNOWN_GROUP_REQUIRED = new UnovationError("KNOWN_GROUPS_REQUIRED","Known groups required");
    public static final UnovationError GROUP_WITH_MEMBERS =
            new UnovationError("GROUP_WITH_MEMBERS","Cannot exclude group with members");
    public static final UnovationError UNKNOWN_GROUP_FOUND =
            new UnovationError("UNKNOWN_GROUP_FOUND","Unknown group found in group list");
    public static final UnovationError USER_EMAIL_ALREADY_EXISTS =
            new UnovationError("USER_EMAIL_ALREADY_EXISTS","User email already exists");

    /* payment rule group */
    public static final UnovationError PAYMENT_RULE_GROUP_CODE_ALREADY_EXISTS =
        new UnovationError("PAYMENT_RULE_GROUP_CODE_ALREADY_EXISTS","Payment rule code already exists");
    public static final UnovationError PAYMENT_RULE_GROUP_NOT_FOUND  =
            new UnovationError("PAYMENT_RULE_GROUP_NOT_FOUND","PaymentRuleGroup not found");
    public static final UnovationError PAYMENT_RULE_GROUP_IN_ISSUER =
        new UnovationError("PAYMENT_RULE_GROUP_IN_ISSUER","PaymentRuleGroup is associated with issuers");
    public static final UnovationError PAYMENT_RULE_GROUP_IN_ACCREDITED_NETWORK =
    new UnovationError("PAYMENT_RULE_GROUP_IN_ACCREDITED_NETWORK",
            "PaymentRuleGroup is associated with AccreditedNetwork");
    public static final UnovationError PAYMENT_RULE_GROUP_NAME_REQUIRED =
            new UnovationError("PAYMENT_RULE_GROUP_NAME_REQUIRED","Name is Required");
    public static final UnovationError PAYMENT_RULE_GROUP_CODE_REQUIRED =
            new UnovationError("PAYMENT_RULE_GROUP_CODE_REQUIRED","Code is Required");
    public static final UnovationError USER_RELATIONSHIP_REQUIRED =
            new UnovationError("USER_RELATIONSHIP_REQUIRED","UserRelationship is Required");
    public static final UnovationError LARGE_PAYMENT_RULE_GROUP_NAME =
            new UnovationError("LARGE_PAYMENT_RULE_GROUP_NAME","Name is too large");
    public static final UnovationError SHORT_PAYMENT_RULE_GROUP_NAME =
            new UnovationError("SHORT_PAYMENT_RULE_GROUP_NAME","Name is too short");
    public static final UnovationError PAYMENT_RULE_GROUP_ID_REQUIRED =
            new UnovationError("PAYMENT_RULE_GROUP_ID_REQUIRED","Id is Required");
    public static final UnovationError PAYMENT_RULE_GROUP_REQUIRED =
            new UnovationError("PAYMENT_RULE_GROUP_REQUIRED","Payment rule group is required");



    /* Person */
    public static final UnovationError INVALID_DOCUMENT_TYPE_FOR_USER =
            new UnovationError("INVALID_DOCUMENT_TYPE_FOR_USER","Invalid document type for user");
    public static final UnovationError PERSON_DOCUMENT_ALREADY_EXISTS  =
            new UnovationError("PERSON_DOCUMENT_ALREADY_EXISTS","Person with document already exists");
    public static final UnovationError PERSON_WITH_DOCUMENT_NOT_FOUND =
            new UnovationError("PERSON_WITH_DOCUMENT_NOT_FOUND","Person with document not found");
    public static final UnovationError PERSON_NOT_FOUND =
            new UnovationError("PERSON_NOT_FOUND","Person not found");
    public static final UnovationError
            PERSON_REQUIRED = new UnovationError("PERSON_REQUIRED","Person required");
    public static final UnovationError
            PERSON_ID_REQUIRED = new UnovationError("PERSON_ID_REQUIRED","Person id required");


    /* Institution */
    public static final UnovationError PERSON_INSTITUTION_ALREADY_EXISTS =
            new UnovationError("PERSON_INSTITUTION_ALREADY_EXISTS","Person Institution already exists");
    public static final UnovationError USER_TYPE_MUST_SET_AN_INSTITUTION =
            new UnovationError("USER_TYPE_MUST_SET_AN_INSTITUTION","UserType must set an Institution");
    public static final UnovationError INSTITUTION_NOT_FOUND =
            new UnovationError("INSTITUTION_NOT_FOUND","Institution not found");
    public static final UnovationError INSTITUTION_WITH_USERS =
            new UnovationError("INSTITUTION_WITH_USERS"," Institution has Users");
    public static final UnovationError INSTITUTION_WITH_PAYMENT_RULE_GROUPS =
            new UnovationError("INSTITUTION_WITH_PAYMENT_RULE_GROUPS",
                    "Institution has PaymentRuleGroups");

    /* Hirer */
    public static final UnovationError PERSON_HIRER_ALREADY_EXISTS =
            new UnovationError("PERSON_HIRER_ALREADY_EXISTS","Person Hirer already exists");
    public static final UnovationError HIRER_NOT_FOUND =
            new UnovationError("HIRER_NOT_FOUND","Hirer not found");
    public static final UnovationError HIRER_WITH_USERS =
            new UnovationError("HIRER_WITH_USERS"," Hirer has Users");
    public static final UnovationError HIRER_DOCUMENT_NOT_FOUND =
            new UnovationError("HIRER_DOCUMENT_NOT_FOUND","Hirer document not found");



    /* HirerBranch */
    public static final UnovationError PERSON_HIRER_BRANCH_ALREADY_EXISTS =
            new UnovationError("PERSON_HIRER_ALREADY_EXISTS","Person Hirer already exists");
    public static final UnovationError HIRER_BRANCH_NOT_FOUND =
            new UnovationError("HIRER_NOT_FOUND","Hirer not found");

    /* Contractor */
    public static final UnovationError PERSON_CONTRACTOR_ALREADY_EXISTS =
            new UnovationError("PERSON_CONTRACTOR_ALREADY_EXISTS","Person Contractor already exists");
    public static final UnovationError CONTRACTOR_NOT_FOUND =
            new UnovationError("CONTRACTOR_NOT_FOUND","Contractor not found");
    public static final UnovationError CONTRACTOR_WITH_USERS =
            new UnovationError("CONTRACTOR_WITH_USERS"," Contractor has Users");

    /* Partner */
    public static final UnovationError PERSON_PARTNER_ALREADY_EXISTS =
            new UnovationError("PERSON_PARTNER_ALREADY_EXISTS","Person Partner already exists");
    public static final UnovationError PARTNER_NOT_FOUND =
            new UnovationError("PARTNER_NOT_FOUND","Partner not found");
    public static final UnovationError PARTNER_WITH_USERS =
            new UnovationError("PARTNER_WITH_USERS"," Partner has Users");


    /* AccreditedNetwork */
    public static final UnovationError ACCREDITED_NETWORK_WITH_USERS =
            new UnovationError("ACCREDITED_NETWORK_WITH_USERS","AccreditedNetwork has users");
    public static final UnovationError PERSON_ACCREDITED_NETWORK_ALREADY_EXISTS =
            new UnovationError("PERSON_ACCREDITED_NETWORK_ALREADY_EXISTS",
                    "Person AccreditedNetwork already exists");
    public static final UnovationError INVALID_MERCHANT_DISCOUNT_RATE_RANGE =
            new UnovationError("INVALID_MERCHANT_DISCOUNT_RATE_RANGE",
                    "merchantDiscountRate must be between 0 and 1");
    public static final UnovationError INVALID_MINIMUM_DEPOSIT_VALUE =
            new UnovationError("INVALID_MINIMUM_DEPOSIT_VALUE",
                    "minimumDepositValue must be a positive value");
    public static final UnovationError USER_TYPE_MUST_SET_AN_ACCREDITED_NETWORK =
            new UnovationError("USER_TYPE_MUST_SET_AN_ACCREDITED_NETWORK",
                    "UserType must set an AccreditedNetwork");
    public static final UnovationError ACCREDITED_NETWORK_NOT_FOUND =
            new UnovationError("ACCREDITED_NETWORK_NOT_FOUND","AccreditedNetwork not found");

    /* file upload */
    public static final UnovationError FILE_SERVICE_NOT_CONFIGURED =
            new UnovationError("FILE_SERVICE_NOT_CONFIGURED","File service not configured.");

    /* establishment */
    public static final UnovationError ESTABLISHMENT_NOT_FOUND =
            new UnovationError("ESTABLISHMENT_NOT_FOUND","Establishment not found.");
    public static final UnovationError ESTABLISHMENT_WITH_BRANCH =
            new UnovationError("ESTABLISHMENT_WITH_BRANCH","Establishment with branch.");
    public static final UnovationError ACCREDITED_NETWORK_REQUIRED =
            new UnovationError("ACCREDITED_NETWORK_REQUIRED","Accredited network is required.");
    public static final UnovationError ACCREDITED_NETWORK_ID_REQUIRED =
            new UnovationError("ACCREDITED_NETWORK_ID_REQUIRED","Accredited network id is required.");
    public static final UnovationError BRAND_FLAG_REQUIRED =
            new UnovationError("BRAND_FLAG_REQUIRED","Brand flag is required.");
    public static final UnovationError CONTACT_REQUIRED =
            new UnovationError("CONTACT_REQUIRED","Contact is required.");
    public static final UnovationError BANK_ACCOUNT_REQUIRED =
            new UnovationError("BANK_ACCOUNT_REQUIRED","Bank account is required");
    public static final UnovationError BANK_ACCOUNT_ID_REQUIRED =
            new UnovationError("BANK_ACCOUNT_ID_REQUIRED","Bank account id is required");
    public static final UnovationError ESTABLISHMENT_WITH_USERS=
            new UnovationError("ESTABLISHMENT_WITH_USERS","Establishment with users");



    /* brand flag */
    public static final UnovationError BRAND_FLAG_NOT_FOUND =
            new UnovationError("BRAND_FLAG_NOT_FOUND","Brand flag not found.");
    public static final UnovationError BRAND_FLAG_ID_REQUIRED =
            new UnovationError("BRAND_FLAG_ID_REQUIRED","Brand flag id is required.");

    /* contact */
    public static final UnovationError CONTACT_ID_REQUIRED =
            new UnovationError("CONTACT_ID_REQUIRED","Contact id is required.");
    public static final UnovationError CONTACT_NOT_FOUND =
            new UnovationError("CONTACT_NOT_FOUND","Contact not found.");

    /* branch */
    public static final UnovationError BRANCH_NOT_FOUND =
            new UnovationError("BRANCH_NOT_FOUND","Branch not found.");
    public static final UnovationError HEAD_OFFICE_REQUIRED =
            new UnovationError("HEAD_OFFICE_REQUIRED","Head office required.");
    public static final UnovationError CANNOT_CHANGE_HEAD_OFFICE =
            new UnovationError("CANNOT_CHANGE_HEAD_OFFICE","Cannot change head office.");

    /* product */
    public static final UnovationError PRODUCT_NOT_FOUND =
            new UnovationError("PRODUCT_NOT_FOUND","Product not found.");
    public static final UnovationError PRODUCT_ALREADY_EXISTS =
            new UnovationError("PRODUCT_ALREADY_EXISTS","Product already exists.");
    /* Contract */
    public static final UnovationError CONTRACT_NOT_FOUND =
            new UnovationError("CONTRACT_NOT_FOUND","Contract not found.");
    public static final UnovationError CONTRACT_ALREADY_EXISTS =
            new UnovationError("CONTRACT_ALREADY_EXISTS","Contract already exists.");
    public static  final UnovationError CONTRACT_END_IS_BEFORE_BEGIN =
            new UnovationError("CONTRACT_END_IS_BEFORE_BEGIN","Contract end date is before begin date.");
    public static final UnovationError ESTABLISHMENTS_NOT_FOUND =
            new UnovationError("ESTABLISHMENTS_NOT_FOUND",
                    "The informed Contract ServiceTypes are not in the informed Product ServiceTypes.");

    /* payment instrument */
    public static final UnovationError PAYMENT_INSTRUMENT_NOT_FOUND =
            new UnovationError("PAYMENT_INSTRUMENT_NOT_FOUND","Payment instrument not found.");
    public static final UnovationError EXTERNAL_ID_OF_PAYMENT_INSTRUMENT_ALREADY_EXISTS =
            new UnovationError("EXTERNAL_ID_OF_PAYMENT_INSTRUMENT_ALREADY_EXISTS",
                    "External number id already exists.");

    /* credit */
    public static final UnovationError PAYMENT_RULE_GROUP_OR_PRODUCT_REQUIRED =
            new UnovationError("PAYMENT_RULE_GROUP_OR_PRODUCT_REQUIRED","Payment rule group or product is required");

    public static final UnovationError CREDIT_INSERT_TYPE_REQUIRED =
            new UnovationError("CREDIT_INSERT_TYPE_REQUIRED","Credit insert type is required");




}