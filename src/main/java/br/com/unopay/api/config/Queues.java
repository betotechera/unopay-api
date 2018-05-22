package br.com.unopay.api.config;

public class Queues {

    public static final String NOTIFICATION = "unopay.notification";
    public static final String BATCH_CLOSING = "unopay.batch.closing";
    public static final String PAYMENT_REMITTANCE = "unopay.payment.remittance";
    public static final String CREDIT_PROCESSED = "unopay.credit.processed";
    public static final String ORDER_CREATED = "unopay.order.created";
    public static final String ORDER_UPDATED = "unopay.order.updated";
    public static final String HIRER_CREDIT_CREATED = "unopay.hirer.credit.created";
    public static final String HIRER_BILLING_CREATED = "unopay.hirer.billing.created";
    public static final String BONUS_BILLING_CREATED = "unopay.bonus.billing.created";
    public static final String DLQ_NOTIFICATION = "dlq.unopay.notification";
    public static final String DLQ_BATCH_CLOSING = "dlq.unopay.batch.closing";
    public static final String DLQ_PAYMENT_REMITTANCE = "dlq.unopay.payment.remittance";
    public static final String DLQ_CREDIT_PROCESSED = "dlq.unopay.credit.processed";
    public static final String DLQ_ORDER_CREATED = "dlq.unopay.order.created";
    public static final String DLQ_HIRER_BILLING_CREATED = "dlq.unopay.hirer.billing.created";
    public static final String DLQ_HIRER_CREDIT_CREATED = "dlq.unopay.hirer.credit.created";
    public static final String DLQ_ORDER_UPDATED = "dlq.unopay.order.updated";
    public static final String DLQ_BONUS_BILLING_CREATED = "dlq.unopay.bonus.billing.created";

    public static final String DURABLE_CONTAINER = "durableRabbitListenerContainerFactory";


}
