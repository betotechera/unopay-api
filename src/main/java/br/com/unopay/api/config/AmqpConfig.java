package br.com.unopay.api.config;

import br.com.unopay.bootcommons.amqp.RetryMessageRecoverer;
import java.util.HashMap;
import java.util.Map;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
@Profile("!test")
class AmqpConfig {


    @Bean
    public SimpleRabbitListenerContainerFactory durableRabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                                      RabbitMessagingTemplate amqpTemplate) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDefaultRequeueRejected(false);
        factory.setMissingQueuesFatal(false);
        factory.setAdviceChain(retryWithDelayedQueueInterceptor(amqpTemplate));
        factory.setMaxConcurrentConsumers(10);
        factory.setConcurrentConsumers(5);
        return factory;
    }

    private RetryOperationsInterceptor retryWithDelayedQueueInterceptor(RabbitMessagingTemplate amqpTemplate) {
        RetryMessageRecoverer recover = new RetryMessageRecoverer(amqpTemplate, 5,2,60);
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(1)
                .recoverer(recover)
                .build();
    }

    @Bean
    public InitializingBean setupQueues(AmqpAdmin amqpAdmin) {
        return ()->{
            declareQueue(amqpAdmin, Queues.NOTIFICATION, Queues.NOTIFICATION);
            declareQueue(amqpAdmin, Queues.DLQ_NOTIFICATION, Queues.DLQ_NOTIFICATION);
            declareQueue(amqpAdmin, Queues.BATCH_CLOSING, Queues.BATCH_CLOSING);
            declareQueue(amqpAdmin, Queues.DLQ_BATCH_CLOSING, Queues.DLQ_BATCH_CLOSING);
            declareQueue(amqpAdmin, Queues.PAYMENT_REMITTANCE, Queues.PAYMENT_REMITTANCE);
            declareQueue(amqpAdmin, Queues.DLQ_PAYMENT_REMITTANCE, Queues.DLQ_PAYMENT_REMITTANCE);
            declareQueue(amqpAdmin, Queues.CREDIT_PROCESSED, Queues.CREDIT_PROCESSED);
            declareQueue(amqpAdmin, Queues.DLQ_CREDIT_PROCESSED, Queues.DLQ_CREDIT_PROCESSED);
            declareQueue(amqpAdmin, Queues.ORDER_CREATED, Queues.ORDER_CREATED);
            declareQueue(amqpAdmin, Queues.ORDER_CREATE, Queues.ORDER_CREATE);
            declareQueue(amqpAdmin, Queues.DLQ_ORDER_CREATED, Queues.DLQ_ORDER_CREATED);
            declareQueue(amqpAdmin, Queues.DLQ_ORDER_CREATE, Queues.DLQ_ORDER_CREATE);
            declareQueue(amqpAdmin, Queues.HIRER_CREDIT_CREATED, Queues.HIRER_CREDIT_CREATED);
            declareQueue(amqpAdmin, Queues.DLQ_HIRER_CREDIT_CREATED, Queues.DLQ_HIRER_CREDIT_CREATED);
            declareQueue(amqpAdmin, Queues.HIRER_BILLING_CREATED, Queues.HIRER_BILLING_CREATED);
            declareQueue(amqpAdmin, Queues.DLQ_HIRER_BILLING_CREATED, Queues.DLQ_HIRER_BILLING_CREATED);
            declareQueue(amqpAdmin, Queues.ORDER_UPDATED, Queues.ORDER_UPDATED);
            declareQueue(amqpAdmin, Queues.DLQ_ORDER_UPDATED, Queues.DLQ_ORDER_UPDATED);
            declareQueue(amqpAdmin, Queues.BONUS_BILLING_CREATED, Queues.BONUS_BILLING_CREATED);
            declareQueue(amqpAdmin, Queues.DLQ_BONUS_BILLING_CREATED, Queues.DLQ_BONUS_BILLING_CREATED);
            declareQueue(amqpAdmin, Queues.CONTRACTOR_CREATED, Queues.CONTRACTOR_CREATED);
            declareQueue(amqpAdmin, Queues.DLQ_CONTRACTOR_CREATED, Queues.DLQ_CONTRACTOR_CREATED);
        };
    }

    private void declareQueue(AmqpAdmin amqpAdmin, String queueName, String exchangeName) {
        Map<String,Object> args = getArgsMap();
        Exchange exchange = new CustomExchange(exchangeName, "x-delayed-message", true, false, args);
        Queue queue = new Queue(queueName, true);
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(exchangeName).and(args);
        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(binding);
    }

    private Map<String, Object> getArgsMap() {
        Map<String, Object> argsMap = new HashMap<>();
        argsMap.put("x-delayed-type", "direct");
        return argsMap;
    }

    private void declareBinding(AmqpAdmin amqpAdmin, String queueName, String exchangeName) {
        Exchange exchange = new FanoutExchange(exchangeName);
        Queue queue = new Queue(queueName, true);
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(exchangeName).noargs();
        amqpAdmin.declareBinding(binding);
    }
}