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

    @Bean
    public RabbitMQService rabbitMQService(ConnectionFactory connectionFactory) {
        return new RabbitMQService(connectionFactory);
    }
}


