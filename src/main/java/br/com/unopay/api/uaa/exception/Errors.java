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
    public static final UnovationError FUEL_EVENT_NOT_FOUND =
            new UnovationError("FUEL_EVENT_NOT_FOUND","Fuel event not found.");




    /* service */
    public static final UnovationError
            SERVICE_NOT_FOUND = new UnovationError("SERVICE_NOT_FOUND","Service not found");
    public static final UnovationError
            SERVICE_REQUIRED = new UnovationError("SERVICE_REQUIRED","Service required");
    public static final UnovationError INVALID_FEE_PERCENT =
            new UnovationError("INVALID_FEE_PERCENT","feePercent must be between 0 and 1");
    public static final UnovationError
            SERVICE_WITH_EVENTS = new UnovationError("SERVICE_WITH_EVENTS","Service has events");
    public static final UnovationError LEAST_ONE_FEE_REQUIRED =
            new UnovationError("LEAST_ONE_FEE_REQUIRED","Least one service fee required");
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
    public static final UnovationError PERSON_ISSUER_ALREADY_EXISTS =
            new UnovationError("PERSON_ISSUER_ALREADY_EXISTS","Person issuer already exists");

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
    public static final UnovationError PAYMENT_RULE_GROUP_ALREADY_EXISTS =
            new UnovationError("PAYMENT_RULE_GROUP_ALREADY_EXISTS","PaymentRuleGroup Already Exists");
    public static final UnovationError MINIMUM_PAYMENT_RULE_GROUP_VALUE_REQUIRED =
            new UnovationError("MINIMUM_PAYMENT_RULE_GROUP_VALUE_REQUIRED","Minimum payment rule group value required.");
    public static final UnovationError MAXIMUM_PAYMENT_RULE_GROUP_VALUE_REQUIRED =
            new UnovationError("MAXIMUM_PAYMENT_RULE_GROUP_VALUE_REQUIRED","Maximum payment rule group value required.");

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
    public static final UnovationError INCORRECT_CONTRACTOR_BIRTH_DATE =
            new UnovationError("INCORRECT_CONTRACTOR_BIRTH_DATE","Incorrect contractor birth date");
    public static final UnovationError CONTRACTOR_BIRTH_DATE_REQUIRED =
            new UnovationError("CONTRACTOR_BIRTH_DATE_REQUIRED","Contractor birth date required.");
    public static final UnovationError INSTRUMENT_PASSWORD_REQUIRED =
            new UnovationError("INSTRUMENT_PASSWORD_REQUIRED","Contractor password required.");

    public static final UnovationError RNTRC_REQUIRED_FOR_LEGAL_PERSON =
            new UnovationError("RNTRC_REQUIRED_FOR_LEGAL_PERSON", "RNTRC is Required for legal person");



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
    public static final UnovationError ESTABLISHMENT_WITH_EVENT_VALUE =
            new UnovationError("ESTABLISHMENT_WITH_EVENT_VALUE","Establishment with event value.");
    public static final UnovationError ACCREDITED_NETWORK_REQUIRED =
            new UnovationError("ACCREDITED_NETWORK_REQUIRED","Accredited network is required.");
    public static final UnovationError ACCREDITED_NETWORK_ID_REQUIRED =
            new UnovationError("ACCREDITED_NETWORK_ID_REQUIRED","Accredited network id is required.");
    public static final UnovationError ESTABLISHMENT_EVENT_NOT_FOUND =
            new UnovationError("ESTABLISHMENT_EVENT_NOT_FOUND","Establishment event not found.");
    public static final UnovationError CONTACT_REQUIRED =
            new UnovationError("CONTACT_REQUIRED","Contact is required.");
    public static final UnovationError BANK_ACCOUNT_REQUIRED =
            new UnovationError("BANK_ACCOUNT_REQUIRED","Bank account is required");
    public static final UnovationError BANK_ACCOUNT_ID_REQUIRED =
            new UnovationError("BANK_ACCOUNT_ID_REQUIRED","Bank account id is required");
    public static final UnovationError ESTABLISHMENT_WITH_USERS=
            new UnovationError("ESTABLISHMENT_WITH_USERS","Establishment with users");
    public static final UnovationError ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_EVENT =
            new UnovationError("ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_EVENT",
                    "Establishment not qualified for this event");



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
    public static final UnovationError CONTRACT_INSTALLMENT_NOT_FOUND =
            new UnovationError("CONTRACT_INSTALLMENT_NOT_FOUND","Contact installment not found.");
    public static final UnovationError CONTRACT_NOT_ACTIVATED =
            new UnovationError("CONTRACT_NOT_ACTIVATED","Contact not activated.");
    public static final UnovationError CONTRACT_NOT_IN_PROGRESS =
            new UnovationError("CONTRACT_NOT_IN_PROGRESS","Contact not in progress.");

    public static final UnovationError INVALID_CONTRACTOR =
            new UnovationError("INVALID_CONTRACTOR","Contractor does not belong to contract.");




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
    public static final UnovationError CONTRACTOR_CONTRACT_NOT_FOUND =
            new UnovationError("CONTRACTOR_CONTRACT_NOT_FOUND","Contractor contract not found.");
    public static final UnovationError CONTRACT_ALREADY_EXISTS =
            new UnovationError("CONTRACT_ALREADY_EXISTS","Contract already exists.");
    public static  final UnovationError CONTRACT_END_IS_BEFORE_BEGIN =
            new UnovationError("CONTRACT_END_IS_BEFORE_BEGIN","Contract end date is before begin date.");
    public static final UnovationError CONTRACT_ESTABLISHMENT_NOT_FOUND =
            new UnovationError("CONTRACT_ESTABLISHMENT_NOT_FOUND",
                    "Contract Establishment not found.");
    public static final UnovationError ESTABLISHMENT_ALREADY_IN_CONTRACT =
            new UnovationError("ESTABLISHMENT_ALREADY_IN_CONTRACT", "The establishment is already in the contract.");
    public static final UnovationError CODE_LENGTH_NOT_ACCEPTED =
            new UnovationError("CODE_LENGTH_NOT_ACCEPTED", "Code length not accepted.");

    public static final UnovationError CREDIT_INSERTION_TYPE_NOT_IN_PRODUCT =
            new UnovationError("CREDIT_INSERTION_TYPE_NOT_IN_PRODUCT",
                    "THe informed creditInsertionTypes has values that is not in the informed Product.");

    public static final UnovationError PRODUCT_REQUIRED =
            new UnovationError("PRODUCT_REQUIRED", "product required");


    /* payment instrument */
    public static final UnovationError PAYMENT_INSTRUMENT_NOT_FOUND =
            new UnovationError("PAYMENT_INSTRUMENT_NOT_FOUND","Payment instrument not found.");
    public static final UnovationError EXTERNAL_ID_OF_PAYMENT_INSTRUMENT_ALREADY_EXISTS =
            new UnovationError("EXTERNAL_ID_OF_PAYMENT_INSTRUMENT_ALREADY_EXISTS",
                    "External number id already exists.");
    public static final UnovationError EXPIRATION_IS_BEFORE_CREATION =
            new UnovationError("EXPIRATION_IS_BEFORE_CREATION",
                    "Contract ExpirationDate is before CreationDate.");


    /* credit */
    public static final UnovationError CREDIT_INSERT_TYPE_NOT_CONFIGURED =
            new UnovationError("CREDIT_INSERT_TYPE_NOT_CONFIGURED","Credit insert type not configured");
    public static final UnovationError MINIMUM_PAYMENT_RULE_GROUP_VALUE_NOT_MET =
            new UnovationError("MINIMUM_PAYMENT_RULE_GROUP_VALUE_NOT_MET","Minimum payment rule group value not met.");
    public static final UnovationError MAXIMUM_PAYMENT_RULE_GROUP_VALUE_NOT_MET =
            new UnovationError("MAXIMUM_PAYMENT_RULE_GROUP_VALUE_NOT_MET","Maximum payment rule group value not met.");
    public static final UnovationError CREDIT_PAYMENT_ACCOUNT_NOT_FOUND =
            new UnovationError("CREDIT_PAYMENT_ACCOUNT_NOT_FOUND","Credit payment account not found.");
    public static final UnovationError HIRER_CREDIT_NOT_FOUND =
            new UnovationError("HIRER_CREDIT_NOT_FOUND","Hirer credit not found.");
    public static final UnovationError CREDIT_ALREADY_CANCELED =
            new UnovationError("CREDIT_ALREADY_CANCELED","Credit already canceled.");
    public static final UnovationError PAYMENT_RULE_GROUP_REQUIRED =
            new UnovationError("PAYMENT_RULE_GROUP_REQUIRED","Payment rule group required.");


    /* credit payment account */
    public static final UnovationError CREDIT_REQUIRED_WHEN_UPDATE_BALANCE =
            new UnovationError("CREDIT_REQUIRED_WHEN_UPDATE_BALANCE","Credit required when update balance");
    public static final UnovationError CREDIT_REQUIRED_WHEN_SUBTRACT_BALANCE =
            new UnovationError("CREDIT_REQUIRED_WHEN_SUBTRACT_BALANCE","Credit required when subtract balance");
    public static final UnovationError VALUE_GREATER_THEN_AVAILABLE_BALANCE =
            new UnovationError("VALUE_GREATER_THEN_AVAILABLE_BALANCE","Value greater then available balance");



    /* credit payment instrument */
    public static final UnovationError PRODUCT_CODE_NOT_MET =
            new UnovationError("PRODUCT_CODE_NOT_MET", "Payment instrument product different of hirer product.");
    public static final UnovationError PRODUCT_ID_NOT_MET =
            new UnovationError("PRODUCT_ID_NOT_MET", "Payment instrument product different of hirer product.");
    public static final UnovationError PAYMENT_INSTRUMENT_NOT_VALID =
            new UnovationError("PAYMENT_INSTRUMENT_NOT_VALID", "Payment instrument does not belong to contractor");
    public static final UnovationError CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_HIRER =
            new UnovationError("CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_HIRER",
                    "Credit Payment account does not belong to hirer");
    public static final UnovationError CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_PRODUCT =
            new UnovationError("CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_PRODUCT",
                    "Credit Payment account does not belong to product");
    public static final UnovationError CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_SERVICE =
            new UnovationError("CREDIT_PAYMENT_ACCOUNT_FROM_ANOTHER_SERVICE",
                    "Credit Payment account does not belong to service");
    public static final UnovationError SERVICE_NOT_ACCEPTED =
            new UnovationError("SERVICE_NOT_ACCEPTED", "Service not accepted.");
    public static final UnovationError VALUE_GREATER_THAN_ZERO_REQUIRED =
            new UnovationError("VALUE_GREATER_THAN_ZERO_REQUIRED", "Value greater than zero required.");
    public static final UnovationError EXPIRATION_DATA_GREATER_THAN_NOW_REQUIRED =
            new UnovationError("EXPIRATION_DATA_GREATER_THAN_NOW_REQUIRED",
                    "Expiration date greater than now required.");
    public static final UnovationError VALUE_GREATER_THAN_BALANCE =
            new UnovationError("VALUE_GREATER_THAN_BALANCE", "Value greater than Credit payment account balance.");
    public static final UnovationError CONTRACTOR_INSTRUMENT_CREDIT_NOT_FOUND =
            new UnovationError("CONTRACTOR_INSTRUMENT_CREDIT_NOT_FOUND", "Contractor instrument credit not found.");

    public static final UnovationError CONTRACT_WITHOUT_CREDITS =
            new UnovationError("CONTRACT_WITHOUT_CREDITS","Contact without credits.");
    public static final UnovationError EXPIRED_CREDIT =
            new UnovationError("EXPIRED_CREDIT","Expired credit.");
    public static final UnovationError CREDIT_UNAVAILABLE =
            new UnovationError("CREDIT_UNAVAILABLE","Credit unavailable.");
    public static final UnovationError FINAL_SUPPLY_CREDIT_NOT_FOUND =
            new UnovationError("FINAL_SUPPLY_CREDIT_NOT_FOUND","Credit for service type not found.");

    /* service authorize */
    public static final UnovationError ESTABLISHMENT_REQUIRED =
            new UnovationError("ESTABLISHMENT_REQUIRED","Establishment required.");
    public static final UnovationError SERVICE_AUTHORIZE_NOT_FOUND =
            new UnovationError("SERVICE_AUTHORIZE_NOT_FOUND","Service authorize not found.");
    public static final UnovationError ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_CONTRACT =
            new UnovationError("ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_CONTRACT",
                    "Establishment not qualified for this contract.");

    public static final UnovationError CREDIT_NOT_QUALIFIED_FOR_THIS_CONTRACT =
            new UnovationError("CREDIT_NOT_QUALIFIED_FOR_THIS_CONTRACT",
                    "Credit not qualified for this contract.");

    public static final UnovationError SERVICE_NOT_ACCEPTABLE =
            new UnovationError("SERVICE_NOT_ACCEPTABLE",
                    "Service not acceptable.");

    public static final UnovationError EVENT_NOT_ACCEPTED =
            new UnovationError("EVENT_NOT_ACCEPTED",
                    "Event not accepted.");
    public static final UnovationError EVENT_QUANTITY_GREATER_THAN_ZERO_REQUIRED =
            new UnovationError("EVENT_QUANTITY_GREATER_THAN_ZERO_REQUIRED",
                    "Event quantity greater than zero required");
    public static final UnovationError EVENT_VALUE_GREATER_THAN_ZERO_REQUIRED =
            new UnovationError("EVENT_VALUE_GREATER_THAN_ZERO_REQUIRED",
                    "Event value greater than zero required");
    public static final UnovationError EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE =
            new UnovationError("EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE",
                    "Event value greater than credit balance");

    /* freight receipt */
    public static final UnovationError CARGO_CONTRACT_NOT_FOUND =
            new UnovationError("CARGO_CONTRACT_NOT_FOUND",
                    "Cargo contract not found.");
    public static final UnovationError TRAVEL_DOCUMENT_NOT_FOUND =
            new UnovationError("TRAVEL_DOCUMENT_NOT_FOUND",
                    "Travel document not found.");
    public static final UnovationError WEIGHT_REQUIRED =
            new UnovationError("WEIGHT_REQUIRED", "Weight greater than or equals zero required.");
    public static final UnovationError DAMAGED_ITEMS_REQUIRED =
            new UnovationError("DAMAGED_ITEMS_REQUIRED", "Damaged items greater than or equals zero required.");
    public static final UnovationError BASE_KEY_REQUIRED =
            new UnovationError("BASE_KEY_REQUIRED", "Base key annotation required to translate fields.");

    /* batch closing */
    public static final UnovationError INVOICE_NOT_REQUIRED_FOR_BATCH =
            new UnovationError("INVOICE_NOT_REQUIRED_FOR_BATCH", "Invoice not required for batch.");
    public static final UnovationError BATCH_CLOSING_ITEM_NOT_FOUND =
            new UnovationError("BATCH_CLOSING_ITEM_NOT_FOUND", "Batch closing item not found");
    public static final UnovationError BATCH_CLOSING_NOT_FOUND =
            new UnovationError("BATCH_CLOSING_NOT_FOUND", "Batch closing not found");
    public static final UnovationError ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_BATCH =
            new UnovationError("ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_BATCH",
                    "Establishment not qualified for this batch");
    public static final UnovationError ISSUER_NOT_QUALIFIED_FOR_THIS_BATCH =
            new UnovationError("ISSUER_NOT_QUALIFIED_FOR_THIS_BATCH",
                    "Issuer not qualified for this batch");
    public static final UnovationError BATCH_FINALIZED =
            new UnovationError("BATCH_FINALIZED", "Batch already finalized");
    public static final UnovationError BATCH_CANCELED =
            new UnovationError("BATCH_CANCELED", "Batch canceled");
    public static final UnovationError SITUATION_NOT_ALLOWED =
            new UnovationError("SITUATION_NOT_ALLOWED", "Situation not allowed");
    public static final UnovationError BATCH_ALREADY_RUNNING =
            new UnovationError("BATCH_ALREADY_RUNNING", "Batch already running.");

    /* Cnba240 */
    public static final UnovationError REMITTANCE_COLUMN_LENGTH_NOT_MET =
            new UnovationError("REMITTANCE_COLUMN_LENGTH_NOT_MET", "Remittance column length not met");
    public static final UnovationError HEADER_REQUIRED_ON_WRAPPED_RECORD =
            new UnovationError("HEADER_REQUIRED_ON_WRAPPED_RECORD", "Header required on wrapped record.");
    public static final UnovationError TRAILER_REQUIRED_ON_WRAPPED_RECORD =
            new UnovationError("TRAILER_REQUIRED_ON_WRAPPED_RECORD", "Trailer required on wrapped record.");
    public static final UnovationError RULE_COLUMN_REQUIRED =
            new UnovationError("RULE_COLUMN_REQUIRED", "Rule column required when fill cnab240 field.");
    public static final UnovationError LAYOUT_COLUMN_NOT_FILLED =
            new UnovationError("LAYOUT_COLUMN_NOT_FILLED", "Layout column not filled.");


    /* Service Payment */
    public static final UnovationError PROCESSING_REMITTANCE_ITEM_NOT_FOUND =
            new UnovationError("PROCESSING_REMITTANCE_ITEM_NOT_FOUND", "Processing remittance item not found");

    /* Remittance */
    public static final UnovationError REMITTANCE_ALREADY_RUNNING =
            new UnovationError("REMITTANCE_ALREADY_RUNNING", "Remittance already running.");
    public static final UnovationError REMITTANCE_WITH_INVALID_DATA =
            new UnovationError("REMITTANCE_WITH_INVALID_DATA", "Remittance with invalid data.");



































}
