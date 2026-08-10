package gov.iti.jets.NutriScan.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.scan.events.exchange.name}")
    private String scanExchangeName;

    @Value("${rabbitmq.scan.events.dlx.name}")
    private String scanDlxName;

    @Value("${rabbitmq.scan.index.queue.name}")
    private String scanIndexQueue;

    @Value("${rabbitmq.scan.index.routing-key}")
    private String scanIndexRoutingKey;

    @Value("${rabbitmq.scan.index.dlq.name}")
    private String scanIndexDlq;

    @Value("${rabbitmq.scan.index.dlq.routing-key}")
    private String scanIndexDlqRoutingKey;

    @Value("${rabbitmq.scan.delete.queue.name}")
    private String scanDeleteQueue;

    @Value("${rabbitmq.scan.delete.routing-key}")
    private String scanDeleteRoutingKey;

    @Value("${rabbitmq.scan.delete.dlq.name}")
    private String scanDeleteDlq;

    @Value("${rabbitmq.scan.delete.dlq.routing-key}")
    private String scanDeleteDlqRoutingKey;

    @Value("${rabbitmq.user.delete.queue.name}")
    private String userDeleteQueue;

    @Value("${rabbitmq.user.delete.routing-key}")
    private String userDeleteRoutingKey;

    @Value("${rabbitmq.user.delete.dlq.name}")
    private String userDeleteDlq;

    @Value("${rabbitmq.user.delete.dlq.routing-key}")
    private String userDeleteDlqRoutingKey;

    @Value("${rabbitmq.scan.index.retry.queue.name}")
    private String scanIndexRetryQueue;

    @Value("${rabbitmq.scan.index.retry.routing-key}")
    private String scanIndexRetryRoutingKey;

    @Bean
    public DirectExchange scanExchange() {
        return ExchangeBuilder
                .directExchange(scanExchangeName)
                .durable(true)
                .build();
    }

    @Bean
    public DirectExchange scanDlx() {
        return ExchangeBuilder
                .directExchange(scanDlxName)
                .durable(true)
                .build();
    }

    @Bean
    public Queue scanIndexQueue() {
        return QueueBuilder.durable(scanIndexQueue)
                .deadLetterExchange(scanDlxName)
                .deadLetterRoutingKey(scanIndexDlqRoutingKey)
                .build();
    }

    @Bean
    public Queue scanIndexDlq() {
        return QueueBuilder.durable(scanIndexDlq).build();
    }

    @Bean
    public Queue scanDeleteQueue() {
        return QueueBuilder.durable(scanDeleteQueue)
                .deadLetterExchange(scanDlxName)
                .deadLetterRoutingKey(scanDeleteDlqRoutingKey)
                .build();
    }

    @Bean
    public Queue scanDeleteDlq() {
        return QueueBuilder.durable(scanDeleteDlq).build();
    }

    @Bean
    public Queue userDeleteQueue() {
        return QueueBuilder.durable(userDeleteQueue)
                .deadLetterExchange(scanDlxName)
                .deadLetterRoutingKey(userDeleteDlqRoutingKey)
                .build();
    }

    @Bean
    public Queue userDeleteDlq() {
        return QueueBuilder.durable(userDeleteDlq).build();
    }

    @Bean
    public Queue scanIndexRetryQueue() {
        return QueueBuilder
                .durable(scanIndexRetryQueue)
                .ttl(30_000)
                .deadLetterExchange(scanExchangeName)
                .deadLetterRoutingKey(scanIndexRoutingKey)
                .build();
    }

    @Bean
    public Binding scanIndexBinding(
            @Qualifier("scanIndexQueue") Queue queue,
            @Qualifier("scanExchange") DirectExchange exchange) {

        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(scanIndexRoutingKey);
    }

    @Bean
    public Binding scanIndexDlqBinding(
            @Qualifier("scanIndexDlq") Queue queue,
            @Qualifier("scanDlx") DirectExchange exchange) {

        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(scanIndexDlqRoutingKey);
    }

    @Bean
    public Binding scanDeleteBinding(
            @Qualifier("scanDeleteQueue") Queue queue,
            @Qualifier("scanExchange") DirectExchange exchange) {

        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(scanDeleteRoutingKey);
    }

    @Bean
    public Binding scanDeleteDlqBinding(
            @Qualifier("scanDeleteDlq") Queue queue,
            @Qualifier("scanDlx") DirectExchange exchange) {

        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(scanDeleteDlqRoutingKey);
    }

    @Bean
    public Binding userDeleteBinding(
            @Qualifier("userDeleteQueue") Queue queue,
            @Qualifier("scanExchange") DirectExchange exchange) {

        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(userDeleteRoutingKey);
    }

    @Bean
    public Binding userDeleteDlqBinding(
            @Qualifier("userDeleteDlq") Queue queue,
            @Qualifier("scanDlx") DirectExchange exchange) {

        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(userDeleteDlqRoutingKey);
    }

    @Bean
    public Binding scanIndexRetryQueueBinding(
            @Qualifier("scanIndexRetryQueue") Queue queue,
            @Qualifier("scanExchange") DirectExchange exchange) {

        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(scanIndexRetryRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);

        template.setConnectionFactory(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setMandatory(true);

        template.setConfirmCallback((correlation, ack, cause) -> {

            if (ack) {
                return;
            }

            log.error(
                    "Failed to publish RabbitMQ message. Correlation={}, Cause={}",
                    correlation,
                    cause);
        });

        template.setReturnsCallback(returned -> {

            log.error("""
            Returned message

            Exchange : {}
            Routing key : {}
            Reply : {}
            """,
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    returned.getReplyText());
        });

        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);

        factory.setMessageConverter(messageConverter);

        factory.setDefaultRequeueRejected(false);

        factory.setPrefetchCount(25);

        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(6);

        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);

        return factory;
    }
}
