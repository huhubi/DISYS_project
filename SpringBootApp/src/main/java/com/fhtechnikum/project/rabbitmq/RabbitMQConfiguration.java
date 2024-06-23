package com.fhtechnikum.project.rabbitmq;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
    private static final String HOST = "localhost";
    private static final int PORT = 30003;

    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(HOST, PORT);
    }

    @Bean(name = "dataCollectionDispatcherQueue")
    public RabbitMQService dataCollectionDispatcherQueue(ConnectionFactory connectionFactory) {
        RabbitMQService service = new RabbitMQService(connectionFactory);
        service.setQueueName(Queues.DATA_COLLECTION_DISPATCHER);
        return service;
    }

    @Bean(name = "stationDataCollectorQueue")
    public RabbitMQService stationDataCollectorQueue(ConnectionFactory connectionFactory) {
        RabbitMQService service = new RabbitMQService(connectionFactory);
        service.setQueueName(Queues.STATION_DATA_COLLECTOR);
        return service;
    }

    @Bean(name = "dataCollectionReceiverQueue")
    public RabbitMQService dataCollectionReceiverQueue(ConnectionFactory connectionFactory) {
        RabbitMQService service = new RabbitMQService(connectionFactory);
        service.setQueueName(Queues.DATA_COLLECTION_RECEIVER);
        return service;
    }

    @Bean(name = "pdfGeneratorQueue")
    public RabbitMQService pdfGeneratorQueue(ConnectionFactory connectionFactory) {
        RabbitMQService service = new RabbitMQService(connectionFactory);
        service.setQueueName(Queues.PDF_GENERATOR);
        return service;
    }
}
