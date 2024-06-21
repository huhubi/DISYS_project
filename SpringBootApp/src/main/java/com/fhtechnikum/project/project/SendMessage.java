package com.fhtechnikum.project.project;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class SendMessage {
    private final static String QUEUE_NAME = "Message";

    public String send(String s1){
        System.out.println("REST API Application:");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(30003);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", QUEUE_NAME, null, s1.getBytes());
            System.out.println(" [x] Sent '" + s1 + "'");
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        return "";
    }
}