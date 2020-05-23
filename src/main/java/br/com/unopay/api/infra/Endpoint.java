package br.com.unopay.api.infra;

class Endpoint {

    static final String ESTABLISHMENTS = "/establishments";
    private static final String TYPES = "/types";
    static final String PERSON_TYPES = TYPES + "/person";
    static final String COMPANY_ACTIVITIES = TYPES + "/company-activities";
    static final String COMPANY_TYPES = TYPES + "/companies";
    static final String REGISTRY_ENTITIES = TYPES + "/registry-entities";
    static final String STATES = TYPES + "/states";
    static final String DOCUMENT_TYPES = TYPES + "/documents";
    static final String PURPOSES = TYPES + "/purposes";
    static final String SCOPES = TYPES + "/scopes";
    static final String USER_RELATIONSHIPS = TYPES + "/user-relationships";
    static final String SERVICE_TYPES = TYPES + "/services";
    static final String ESTABLISHMENT_TYPES = TYPES + ESTABLISHMENTS;
    static final String PAYMENT_INSTRUMENTS = "/payment-instruments";
    static final String CONTRACT_SITUATIONS_TYPE = TYPES + "/contract-situations";
    static final String CREDIT_INSERTION_TYPES = TYPES + "/credit-insertions";
    static final String PAYMENT_INSTRUMENT_SITUATION_TYPES = TYPES + "/payment-instrument-situations";
    static final String PAYMENT_INSTRUMENT_TYPES = TYPES + PAYMENT_INSTRUMENTS;
    static final String PRODUCT_SITUATIONS_TYPES = TYPES + "/product-situations";
    static final String CREDIT_SITUATIONS_TYPES = TYPES + "/credit-situations";
    static final String RECEIPT_STEP_TYPES = TYPES + "/receipt-steps";
    static final String RECEIPT_SITUATION_TYPES = TYPES + "/receipt-situations";
    static final String REASON_RECEIPT_SITUATION_TYPES = TYPES + "/reason-receipt-situations";
    static final String PAYMENT_SOURCE_TYPES = TYPES + "/payment-sources";
    static final String DOCUMENT_CAVEAT_TYPES = TYPES + "/document-caveats";
    static final String GATHERING_CHANNELS_TYPES = TYPES + "/gathering-channels";
    static final String DOCUMENT_SITUATION_TYPES = TYPES + "/document-situations";
    static final String BATCH_CLOSING_SITUATION_TYPES = TYPES + "/batch-closing-situations";
    static final String ISSUE_INVOICE_TYPES = TYPES + "/issue-invoices";
    static final String TRANSACTION_SITUATION_TYPES = TYPES + "/transaction-situations";
    static final String REMITTANCE_SITUATION_TYPES = TYPES + "/remittance-situations";
}
