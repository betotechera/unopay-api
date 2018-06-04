package br.com.unopay.api.config

object QueuesScala {

  final val NOTIFICATION = "unopay.notification"
  final val BATCH_CLOSING = "unopay.batch.closing"
  final val PAYMENT_REMITTANCE = "unopay.payment.remittance"
  final val CREDIT_PROCESSED = "unopay.credit.processed"
  final val ORDER_CREATED = "unopay.order.created"
  final val ORDER_UPDATED = "unopay.order.updated"
  final val HIRER_CREDIT_CREATED = "unopay.hirer.credit.created"
  final val HIRER_BILLING_CREATED = "unopay.hirer.billing.created"
  final val BONUS_BILLING_CREATED = "unopay.bonus.billing.created"
  final val DLQ_NOTIFICATION = "dlq.unopay.notification"
  final val DLQ_BATCH_CLOSING = "dlq.unopay.batch.closing"
  final val DLQ_PAYMENT_REMITTANCE = "dlq.unopay.payment.remittance"
  final val DLQ_CREDIT_PROCESSED = "dlq.unopay.credit.processed"
  final val DLQ_ORDER_CREATED = "dlq.unopay.order.created"
  final val DLQ_HIRER_BILLING_CREATED = "dlq.unopay.hirer.billing.created"
  final val DLQ_HIRER_CREDIT_CREATED = "dlq.unopay.hirer.credit.created"
  final val DLQ_ORDER_UPDATED = "dlq.unopay.order.updated"
  final val DLQ_BONUS_BILLING_CREATED = "dlq.unopay.bonus.billing.created"
  final val DURABLE_CONTAINER = "durableRabbitListenerContainerFactory"
}
