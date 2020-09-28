package br.com.unopay.api.uaa.exception;

import br.com.unopay.bootcommons.exception.UnovationError;

public final class Errors {


    private Errors() {
        throw new IllegalAccessError("Utility class");
    }

    /* notification */
    public static final UnovationError INVALID_EMAIL =
            new UnovationError("INVALID_EMAIL","invalid email");

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
    public static final UnovationError CANNOT_INVOKE_TYPE =
            new UnovationError("fCANNOT_INVOKE_TYPE","Cannot invoke type");


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
    public static final UnovationError VALID_PASSWORD_RESET_REQUEST_ORIGIN_REQUIRED =
            new UnovationError("VALID_PASSWORD_RESET_REQUEST_ORIGIN_REQUIRED","Valid password reset request origin required");

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
    public static final UnovationError INSTITUTION_REQUIRED =
            new UnovationError("INSTITUTION_REQUIRED","Institution required.");


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
            ADDRESS_REQUIRED = new UnovationError("ADDRESS_REQUIRED","Address required");
    public static final UnovationError
            ADDRESS_NOT_FOUND = new UnovationError("ADDRESS_NOT_FOUND","Address not found");
    public static final UnovationError
            PERSON_ID_REQUIRED = new UnovationError("PERSON_ID_REQUIRED","Person id required");
    public static final UnovationError
            ADDRESS_ID_REQUIRED = new UnovationError("ADDRESS_ID_REQUIRED","Address id required");
    public static final UnovationError
            GOOGLE_ADDRESS_NOT_FOUND = new UnovationError("GOOGLE_ADDRESS_NOT_FOUND","google address not found");



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
    public static final UnovationError HIRER_WITH_NEGOTIATION =
            new UnovationError("HIRER_WITH_NEGOTIATION","Hirer with negotiation");




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
    public static final UnovationError ESTABLISHMENT_EVENT_ALREADY_EXISTS =
            new UnovationError("ESTABLISHMENT_EVENT_ALREADY_EXISTS","Establishment event already exists.");
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


    /* contact */
    public static final UnovationError CONTACT_ID_REQUIRED =
            new UnovationError("CONTACT_ID_REQUIRED","Contact id is required.");
    public static final UnovationError CONTACT_NOT_FOUND =
            new UnovationError("CONTACT_NOT_FOUND","Contact not found.");
    public static final UnovationError CONTRACT_INSTALLMENT_NOT_FOUND =
            new UnovationError("CONTRACT_INSTALLMENT_NOT_FOUND","Contact installment not found.");
    public static final UnovationError CONTRACT_INSTALLMENTS_NOT_FOUND =
            new UnovationError("CONTRACT_INSTALLMENTS_NOT_FOUND","Contact installments not found.");
    public static final UnovationError CONTRACT_NOT_ACTIVATED =
            new UnovationError("CONTRACT_NOT_ACTIVATED","Contact not activated.");
    public static final UnovationError CONTRACT_NOT_IN_PROGRESS =
            new UnovationError("CONTRACT_NOT_IN_PROGRESS","Contact not in progress.");
    public static final UnovationError INVALID_CONTRACTOR =
            new UnovationError("INVALID_CONTRACTOR","Contractor does not belong to contract.");
    public static final UnovationError FILE_WIHOUT_LINES_OR_HEADER =
            new UnovationError("FILE_WITHOUT_LINES_OR_HEADER","File without lines or header");


    /* branch */
    public static final UnovationError BRANCH_NOT_FOUND =
            new UnovationError("BRANCH_NOT_FOUND","Branch not found.");
    public static final UnovationError HEAD_OFFICE_REQUIRED =
            new UnovationError("HEAD_OFFICE_REQUIRED","Head office required.");
    public static final UnovationError CANNOT_CHANGE_HEAD_OFFICE =
            new UnovationError("CANNOT_CHANGE_HEAD_OFFICE","Cannot change head office.");
    public static final UnovationError PERIOD_ALREADY_REGISTERED =
            new UnovationError("PERIOD_ALREADY_REGISTERED","period already registered");
    public static final UnovationError PERIOD_NOT_FOUND =
            new UnovationError("PERIOD_NOT_FOUND","Period not found.");
    public static final UnovationError PERIOD_BELONGS_TO_ANOTHER_BRANCH =
            new UnovationError("PERIOD_BELONGS_TO_ANOTHER_BRANCH","period belongs to another branch");


    /* product */
    public static final UnovationError PRODUCT_NOT_FOUND =
            new UnovationError("PRODUCT_NOT_FOUND","Product not found.");
    public static final UnovationError PRODUCT_ALREADY_EXISTS =
            new UnovationError("PRODUCT_ALREADY_EXISTS","Product already exists.");
    public static final UnovationError BONUS_PERCENTAGE_REQUIRED =
            new UnovationError("BONUS_PERCENTAGE_REQUIRED","Bonus percentage required.");
    public static final UnovationError MONTHS_TO_EXPIRE_BONUS_REQUIRED =
            new UnovationError("MONTHS_TO_EXPIRE_BONUS_REQUIRED","Months to expire bonus required.");
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
    public static final UnovationError CONTRACT_HIRER_NOT_FOUND =
            new UnovationError("CONTRACT_HIRER_NOT_FOUND",
                    "Contract hirer not found.");
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

    /* service authorize */
    public static final UnovationError ESTABLISHMENT_REQUIRED =
            new UnovationError("ESTABLISHMENT_REQUIRED","Establishment required.");
    public static final UnovationError SERVICE_AUTHORIZE_NOT_FOUND =
            new UnovationError("SERVICE_AUTHORIZE_NOT_FOUND","Service authorize not found.");
    public static final UnovationError EVENTS_REQUIRED =
            new UnovationError("EVENTS_REQUIRED",
                    "Events required");
    public static final UnovationError EVENT_VALUE_GREATER_THAN_ZERO_REQUIRED =
            new UnovationError("EVENT_VALUE_GREATER_THAN_ZERO_REQUIRED",
                    "Event value greater than zero required");
    public static final UnovationError EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE =
            new UnovationError("EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE",
                    "Event value greater than credit balance");
    public static final UnovationError CREDIT_BALANCE_REQUIRED =
            new UnovationError("CREDIT_BALANCE_REQUIRED",
                    "Credit balance required when authorize event");
    public static final UnovationError INSTRUMENT_NOT_QUALIFIED_FOR_THIS_CONTRACT =
            new UnovationError("INSTRUMENT_NOT_QUALIFIED_FOR_THIS_CONTRACT",
                    "Instrument not qualified for this contract");
    public static final UnovationError EVENT_QUANTITY_REQUIRED =
            new UnovationError("EVENT_QUANTITY_REQUIRED",
                    "Event quantity required");
    public static final UnovationError AUTHORIZATION_IN_BATCH_PROCESSING =
            new UnovationError("AUTHORIZATION_IN_BATCH_PROCESSING",
                    "The ServiceAuthorize is in batch process.");
    public static final UnovationError AUTHORIZATION_CANNOT_BE_CANCELLED =
            new UnovationError("AUTHORIZATION_CANNOT_BE_CANCELLED",
                    "The ServiceAuthorize cannot be cancelled.");

    public static final UnovationError AUTHORIZATION_SHOULD_BE_AUTHORIZED =
            new UnovationError("AUTHORIZATION_SHOULD_BE_AUTHORIZED",
                    "The ServiceAuthorize should be authorized.");

    public static final UnovationError AUTHORIZATION_ALREADY_RATED =  new UnovationError("AUTHORIZATION_ALREADY_RATED",
            "The ServiceAuthorize is already rated.");

    public static final UnovationError SERVICE_AUTHORIZE_SHOULD_NOT_HAVE_EXCEPTIONAL_CIRCUMSTANCE =
            new UnovationError("SERVICE_AUTHORIZE_SHOULD_NOT_HAVE_EXCEPTIONAL_CIRCUMSTANCE","Service Authorize should " +
                    "not have exceptional circumstance");


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

    /* Remittance */
    public static final UnovationError REMITTANCE_ALREADY_RUNNING =
            new UnovationError("REMITTANCE_ALREADY_RUNNING", "Remittance already running.");
    public static final UnovationError REMITTANCE_WITH_INVALID_DATA =
            new UnovationError("REMITTANCE_WITH_INVALID_DATA", "Remittance with invalid data.");

    /* Billing */
    public static final UnovationError ORDER_WITH_PENDING_TRANSACTION =
            new UnovationError("ORDER_WITH_PENDING_TRANSACTION","order with pending transaction.");
    public static final UnovationError ORDER_WITH_PROCESSED_TRANSACTION =
            new UnovationError("ORDER_WITH_PROCESSED_TRANSACTION","order with processed transaction.");
    public static final UnovationError INVALID_PAYMENT_VALUE =
            new UnovationError("INVALID_PAYMENT_VALUE","Invalid payment value.");
    public static final UnovationError PAYMENT_REQUEST_REQUIRED =
            new UnovationError("PAYMENT_REQUEST_REQUIRED","Payment request required.");
    public static final UnovationError ORDER_REQUIRED =
            new UnovationError("ORDER_REQUIRED","Order required.");
    public static final UnovationError INVALID_ORDER_TYPE =
            new UnovationError("INVALID_ORDER_TYPE","invalid order type");
    public static final UnovationError ALREADY_PAID_ORDER =
            new UnovationError("ALREADY_PAID_ORDER","already paid order");
    public static final UnovationError PAYZEN_ERROR =
            new UnovationError("PAYZEN_ERROR","payzen error");

    /* Order */
    public static final UnovationError INSTRUMENT_NOT_BELONGS_TO_CONTRACTOR =
            new UnovationError("INSTRUMENT_NOT_BELONGS_TO_CONTRACTOR",
                    "Payment instrument does not belongs to contractor.");
    public static final UnovationError INSTRUMENT_IS_NOT_FOR_PRODUCT =
            new UnovationError("INSTRUMENT_IS_NOT_FOR_PRODUCT",
                    "The instrument is not for this product.");
    public static final UnovationError PAYMENT_INSTRUMENT_REQUIRED =
            new UnovationError("PAYMENT_INSTRUMENT_REQUIRED",
                    "Payment instrument required.");
    public static final UnovationError CONTRACT_REQUIRED =
            new UnovationError("CONTRACT_REQUIRED","Contract required.");
    public static final UnovationError VALUE_REQUIRED =
            new UnovationError("VALUE_REQUIRED","Value required.");
    public static final UnovationError EXISTING_CONTRACTOR =
            new UnovationError("EXISTING_CONTRACTOR","Existing contractor.");
    public static final UnovationError ORDER_NOT_FOUND =
            new UnovationError("ORDER_NOT_FOUND","Order not found.");
    public static final UnovationError USER_ALREADY_EXISTS =
            new UnovationError("USER_ALREADY_EXISTS","User already exists.");
    public static final UnovationError PERSON_EMAIL_IS_REQUIRED =
            new UnovationError("PERSON_EMAIL_IS_REQUIRED","person email is required.");
    public static final UnovationError UNABLE_TO_UPDATE_ORDER_STATUS =
            new UnovationError("UNABLE_TO_UPDATE_ORDER_STATUS", "Unable to update order status.");
    public static final UnovationError RECURRENCE_PAYMENT_INFORMATION_REQUIRED =
            new UnovationError("RECURRENCE_PAYMENT_INFORMATION_REQUIRED", "recurrence payment information required");

    /* Ticket */
    public static final UnovationError CLIENT_REQUIRED =
            new UnovationError("CLIENT_REQUIRED", "Client required.");
    public static final UnovationError ISSUER_REQUIRED =
            new UnovationError("ISSUER_REQUIRED", "Issuer required.");
    public static final UnovationError NUMBER_REQUIRED =
            new UnovationError("NUMBER_REQUIRED", "Number required.");
    public static final UnovationError EXPIRATION_DAYS_REQUIRED =
            new UnovationError("EXPIRATION_DAYS_REQUIRED", "Expiration days required.");
    public static final UnovationError TICKET_REGISTRATION_ERROR =
            new UnovationError("TICKET_REGISTRATION_ERROR", "Error when register ticket.");
    public static final UnovationError TICKET_NUMBER_ALREADY_EXISTS =
            new UnovationError("TICKET_NUMBER_ALREADY_EXISTS", "Ticket number already exists.");
    public static final UnovationError TICKET_NOT_FOUND =
            new UnovationError("TICKET_NOT_FOUND", "Ticket not found.");


    /* Instrument Balance */
    public static final UnovationError INSTRUMENT_BALANCE_NOT_FOUND =
            new UnovationError("INSTRUMENT_BALANCE_NOT_FOUND", "Instrument balance not found.");
    public static final UnovationError BALANCE_LESS_THAN_REQUIRED =
            new UnovationError("BALANCE_LESS_THAN_REQUIRED", "Balance less than required.");
    public static final UnovationError INVALID_VALUE =
            new UnovationError("INVALID_VALUE", "Invalid value.");

    /* AccreditedNetworkIssuer */
    public static final UnovationError ACCREDITED_NETWORK_ISSUER_ALREADY_EXISTS =
            new UnovationError("ACCREDITED_NETWORK_ISSUER_ALREADY_EXISTS", "" +
                    "AccreditedNetworkIssuer already exists");
    public static final UnovationError ESTABLISHMENT_BELONG_TO_ANOTHER_NETWORK =
            new UnovationError("ESTABLISHMENT_BELONG_TO_ANOTHER_NETWORK", "" +
                    "establishment belong to another network");
    public static final UnovationError ESTABLISHMENT_BRANCH_BELONG_TO_ANOTHER_NETWORK =
            new UnovationError("ESTABLISHMENT_BRANCH_BELONG_TO_ANOTHER_NETWORK", "" +
                    "establishment branch belong to another network");


    /* Contractor Credit Recurrence */
    public static final UnovationError HIRER_BELONG_TO_OTHER_CONTRACT =
            new UnovationError("HIRER_BELONG_TO_OTHER_CONTRACT", "" +
                    "Hirer belong to other contract");


    /* User Credit Card */
    public static final UnovationError USER_CREDIT_CARD_NOT_FOUND =
            new UnovationError("USER_CREDIT_CARD_NOT_FOUND","User credit card not found.");
    public static final UnovationError CREDIT_CARD_NUMBER_REQUIRED =
            new UnovationError("CREDIT_CARD_NUMBER_REQUIRED","credit card number required");
    public static final UnovationError INVALID_MONTH =
            new UnovationError("INVALID_MONTH", "" +
                    "Invalid month");
    public static final UnovationError INVALID_YEAR =
            new UnovationError("INVALID_YEAR", "" +
                    "Invalid year");
    public static final UnovationError INVALID_EXPIRATION_DATE =
            new UnovationError("INVALID_EXPIRATION_DATE", "" +
                    "Invalid expiration date");
    public static final UnovationError INVALID_NUMBER =
            new UnovationError("INVALID_NUMBER", "" +
                    "Invalid number");
    public static final UnovationError INVALID_CARD_REFERENCE =
            new UnovationError("INVALID_CARD_REFERENCE", "" +
                    "Invalid card reference");
    public static final UnovationError INVALID_HOLDER_NAME =
            new UnovationError("INVALID_HOLDER_NAME", "" +
                    "Invalid holder name");

    /* Hirer Negotiation */
    public static final UnovationError EFFECTIVE_DATE_IS_BEFORE_CREATION =
            new UnovationError("EFFECTIVE_DATE_IS_BEFORE_CREATION", "" +
                    "Effective date is before creation");

    public static final UnovationError EFFECTIVE_DATE_REQUIRED =
            new UnovationError("EFFECTIVE_DATE_REQUIRED", "" +
                    "effective date required");

    public static final UnovationError HIRER_NEGOTIATION_NOT_FOUND =
            new UnovationError("HIRER_NEGOTIATION_NOT_FOUND", "" +
                    "Hirer negotiation not found");

    public static final UnovationError HIRER_NEGOTIATION_BILLING_NOT_FOUND =
            new UnovationError("HIRER_NEGOTIATION_BILLING_NOT_FOUND", "" +
                    "Hirer negotiation billing not found");
    public static final UnovationError NEGOTIATION_FOR_PRODUCT_AND_HIRER_EXISTING =
            new UnovationError("NEGOTIATION_FOR_PRODUCT_AND_HIRER_EXISTING", "" +
                    "Negotiation for product and hirer existing");

    /* Authorized Member*/
    public static final UnovationError AUTHORIZED_MEMBER_BIRTH_DATE_REQUIRED =
            new UnovationError("AUTHORIZED_MEMBER_BIRTH_DATE_REQUIRED", "The authorized member " +
                    "birth date is required");

    public static final UnovationError INVALID_AUTHORIZED_MEMBER_BIRTH_DATE =
            new UnovationError("INVALID_AUTHORIZED_MEMBER_BIRTH_DATE", "The informed authorized " +
                    "member birth date is invalid");

    public static final UnovationError AUTHORIZED_MEMBER_NAME_REQUIRED =
            new UnovationError("AUTHORIZED_MEMBER_NAME_REQUIRED", "The authorized member " +
                    "name is required");

    public static final UnovationError AUTHORIZED_MEMBER_GENDER_REQUIRED =
            new UnovationError("AUTHORIZED_MEMBER_GENDER_REQUIRED", "The authorized member " +
                    "gender is required");
    public static final UnovationError AUTHORIZED_MEMBER_RELATEDNESS_REQUIRED =
            new UnovationError("AUTHORIZED_MEMBER_RELATEDNESS_REQUIRED", "The authorized member " +
                    "relatedness with a hirer is required");

    public static final UnovationError AUTHORIZED_MEMBER_NOT_FOUND =
            new UnovationError("AUTHORIZED_MEMBER_NOT_FOUND", "Authorized member not founds");

    public static final UnovationError PREVIOUS_DIGITAL_WALLET_OR_PAYMENT_INSTRUMENT_REQUIRED =
            new UnovationError("PREVIOUS_DIGITAL_WALLET_OR_PAYMENT_INSTRUMENT_REQUIRED", "The " +
                    "previous digital wallet or payment instrument is required");

    /*  Negotiation Billing */
    public static final UnovationError NEGOTIATION_BILLING_NOT_FOUND =
            new UnovationError("NEGOTIATION_BILLING_NOT_FOUND", "Negotiation billing not found");

    /*  Bonus Billing */
    public static final UnovationError BONUS_BILLING_TOTAL_REQUIRED =
            new UnovationError("BONUS_BILLING_TOTAL_REQUIRED", "Bonus Billing total required");

    public static final UnovationError INVALID_BONUS_BILLING_PROCESS_DATE =
            new UnovationError("INVALID_BONUS_BILLING_PROCESS_DATE", "Invalid Bonus Billing " +
                    "ProcessedAt date time");

    public static final UnovationError INVALID_BONUS_BILLING_EXPIRATION_DATE =
            new UnovationError("INVALID_BONUS_BILLING_EXPIRATION_DATE", "Invalid Bonus Billing " +
                    "expiration date");

    public static final UnovationError BONUS_BILLING_NOT_FOUND =
            new UnovationError("BONUS_BILLING_NOT_FOUND", "Bonus billing not found");

    public static final UnovationError BONUS_BILLING_ISSUER_REQUIRED =
            new UnovationError("BONUS_BILLING_ISSUER_REQUIRED", "Bonus Billing Issuer required.");
    /*  Contractor Bonus */
    public static final UnovationError CONTRACTOR_BONUS_NOT_FOUND =
            new UnovationError("CONTRACTOR_BONUS_NOT_FOUND","Contractor bonus not found");
    public static final UnovationError INVALID_PROCESSED_AT =
            new UnovationError("INVALID_PROCESSED_AT","Invalid processed at date");
    public static final UnovationError INVALID_BONUS_SITUATION =
            new UnovationError("INVALID_BONUS_SITUATION","Invalid bonus situation");
    public static final UnovationError INVALID_SOURCE_VALUE =
            new UnovationError("INVALID_SOURCE_VALUE","Invalid source value");
    public static final UnovationError SCHEDULING_NOT_FOUND =
            new UnovationError("SCHEDULING_NOT_FOUND","Scheduling not found");

    /* Hirer Product*/
    public static final UnovationError HIRER_REQUIRED =
            new UnovationError("HIRER_REQUIRED","hirer required");
    public static final UnovationError HIRER_PRODUCT_NOT_FOUND =
            new UnovationError("HIRER_PRODUCT_NOT_FOUND","hirer product not found");

    /* IntegrationInformation */
    public static final UnovationError PAYZEN_SHOP_ID_REQUIRED =
            new UnovationError("PAYZEN_SHOP_ID_REQUIRED","payzenShopId required");
    public static final UnovationError PAYZEN_SHOP_KEY_REQUIRED =
            new UnovationError("PAYZEN_SHOP_KEY_REQUIRED","payzenShopKey required");

}
