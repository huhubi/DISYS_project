package com.fhtechnikum.project.project.rabbitmq;

import com.rabbitmq.client.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * This class handles the basic implementation of a RabbitMQ queue service.
 */

@Data
@Slf4j
@Component
public class RabbitMQService {
    private final ConnectionFactory connectionFactory;
    private Connection connection;
    private Channel channel;

    @Autowired
    public RabbitMQService(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * Initialize the connection to RabbitMQ and create a queue.
     */
    @PostConstruct
    public void initialize() {
        try {
            connect();
        } catch (IOException | TimeoutException e) {
            log.error("Failed to connect to RabbitMQ", e);
        }
    }

    /**
     * Connect to RabbitMQ and create the queues.
     * @throws IOException if there's an issue creating the queue.
     * @throws TimeoutException if there's a timeout connecting to RabbitMQ.
     */
    private void connect() throws IOException, TimeoutException {
        connection = connectionFactory.createConnection();
        channel = connection.createChannel(false);
        for (Queues queue : Queues.values()) {
            channel.queueDeclare(queue.getQueueName(), true, false, false, null);
            log.info("Connected to RabbitMQ and declared queue: {}", queue.getQueueName());
        }
    }

    /**
     * Close the connection to RabbitMQ.
     */
    @PreDestroy
    public void destroy() {
        try {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
            log.info("RabbitMQ connection closed");
        } catch (IOException | TimeoutException e) {
            log.error("Failed to close RabbitMQ connection", e);
        }
    }

    /**
     * Send a message to the specified queue.
     * @param message the message to send.
     * @param queue the queue to send the message to.
     */
    public void publishMessage(String message, Queues queue) {
        try {
            channel.basicPublish("", queue.getQueueName(), null, message.getBytes());
            log.info("Sent message to {}: {}", queue.getQueueName(), message);
        } catch (IOException e) {
            log.error("Failed to send message to queue: {}", queue.getQueueName(), e);
        }
    }
}

