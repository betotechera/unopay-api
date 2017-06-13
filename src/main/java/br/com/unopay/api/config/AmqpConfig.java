package br.com.unopay.api.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("!test")
class AmqpConfig {

    @Autowired
    RabbitMessagingTemplate amqpTemplate;

    @Bean
    public SimpleRabbitListenerContainerFactory nonDurableRabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDefaultRequeueRejected(false);
        factory.setMissingQueuesFatal(false);
        factory.setAdviceChain(rejectAndDontRequeueInterceptor());
        factory.setMaxConcurrentConsumers(10);
        factory.setConcurrentConsumers(5);
        return factory;
    }

    private RetryOperationsInterceptor rejectAndDontRequeueInterceptor() {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(5)
                .backOffOptions(1000, 2, 5000)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }

    @Bean
    public InitializingBean setupQueues(AmqpAdmin amqpAdmin) {
        return ()->{
            declareQueue(amqpAdmin, Queues.UNOPAY_NOTIFICAITON, Queues.UNOPAY_NOTIFICAITON);
            declareQueue(amqpAdmin, Queues.PAMCARY_TRAVEL_DOCUMENTS, Queues.PAMCARY_TRAVEL_DOCUMENTS);
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
}