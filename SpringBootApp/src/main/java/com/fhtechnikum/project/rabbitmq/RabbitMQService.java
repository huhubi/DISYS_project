package com.fhtechnikum.project.rabbitmq;

import com.rabbitmq.client.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
    private String queueName;

    @Autowired
    public RabbitMQService(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.queueName = Queues.SPRING_BOOT_APP.getQueueName(); //TODO: changes needed to queue name
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
     * Connect to RabbitMQ and create a queue with the specified name.
     * @throws IOException if there's an issue creating the queue.
     * @throws TimeoutException if there's a timeout connecting to RabbitMQ.
     */
    private void connect() throws IOException, TimeoutException {
        connection = connectionFactory.createConnection();
        channel = connection.createChannel(false);
        channel.queueDeclare(queueName, true, false, false, null);
        log.info("Connected to RabbitMQ and declared queue: {}", queueName);
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
     * Send a message to the queue.
     * @param message the message to send.
     */
    public void publishMessage(String message) {
        try {
            channel.basicPublish("", queueName, null, message.getBytes());
            log.info("Sent message: {}", message);
        } catch (IOException e) {
            log.error("Failed to send message to queue: {}", queueName, e);
        }
    }

    /**
     * Set the queue name dynamically.
     * @param queue the queue enum.
     */
    public void setQueueName(Queues queue) {
        this.queueName = queue.getQueueName();
        log.info("Queue name set to: {}", queueName);
    }
}
