package com.fhtechnikum.project.project;

import com.fhtechnikum.project.project.model.Invoice;
import com.fhtechnikum.project.project.model.Station;
import com.fhtechnikum.project.project.rabbitmq.RabbitMQService;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import static com.fhtechnikum.project.project.rabbitmq.Queues.*;

@Slf4j
public class DataCollectionReceiver {
    private static DataCollectionReceiver instance;
    private final RabbitMQService dispatcherReceiverQueue;
    private final RabbitMQService collectorReceiverQueue;
    private final RabbitMQService receiverPdfQueue;
    private Invoice invoice = new Invoice();
    private boolean fromDispatcherReceived = false;
    private boolean fromCollectorReceived = false;


    public DataCollectionReceiver(RabbitMQService dispatcherReceiverQueue,
                                  RabbitMQService collectorReceiverQueue,
                                  RabbitMQService receiverPdfQueue) {
        this.dispatcherReceiverQueue = dispatcherReceiverQueue;
        this.collectorReceiverQueue = collectorReceiverQueue;
        this.receiverPdfQueue = receiverPdfQueue;
    }

    public void init() {
        receiveDataCollectionJob();
    }

    public static void main(String[] args) {
        // Initialize the Spring ApplicationContext
        ApplicationContext context = new AnnotationConfigApplicationContext("com.fhtechnikum.project.project.rabbitmq");

        // Get the RabbitMQService instances from the context
        RabbitMQService dispatcherReceiverQueue = context.getBean("dataCollectionDispatcherQueue", RabbitMQService.class);
        RabbitMQService collectorReceiverQueue = context.getBean("stationDataCollectorQueue", RabbitMQService.class);
        RabbitMQService receiverPdfQueue = context.getBean("pdfGeneratorQueue", RabbitMQService.class);

        // Initialize the DataCollectionReceiver instance
        DataCollectionReceiver dataCollectionReceiver = new DataCollectionReceiver(dispatcherReceiverQueue, collectorReceiverQueue, receiverPdfQueue);
        dataCollectionReceiver.init();
    }

    /**
     * Receives the data collection job from the data collector and sends it to the
     * PDF Generator when the data is complete
     */
    public void receiveDataCollectionJob() {
        try {
            dispatcherReceiverQueue.initialize();
            collectorReceiverQueue.initialize();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String receivedMessage = new String(delivery.getBody(), "UTF-8");
                log.info("Received message: {}", receivedMessage);
                receiverPdfQueue.initialize();
                boolean isDispatcher = delivery.getEnvelope().getRoutingKey().equals(DATA_COLLECTION_DISPATCHER.getQueueName());
                boolean isCollector = delivery.getEnvelope().getRoutingKey().equals(STATION_DATA_COLLECTOR.getQueueName());

                processDispatcher(receivedMessage, isDispatcher);
                processCollector(receivedMessage, isCollector);

                processJob();
            };
            dispatcherReceiverQueue.getChannel().basicConsume(dispatcherReceiverQueue.getQueueName(), true, deliverCallback, consumerTag -> {});
            collectorReceiverQueue.getChannel().basicConsume(collectorReceiverQueue.getQueueName(), true, deliverCallback, consumerTag -> {});
        } catch (IOException e) {
            log.error("Error in receiving data collection job", e);
        }
    }

    /**
     * If both messages are received, the data gathering process ends and then sends gathered data to PDF generator
     */
    protected void processJob() throws IOException {
        if (fromDispatcherReceived && fromCollectorReceived) {
            invoice.setTotalKwh(invoice.getStations().stream().mapToLong(Station::getTotalKwh).sum());
            receiverPdfQueue.publishMessage(invoice.toCsv());
            // Reset variables
            invoice = new Invoice();
            fromDispatcherReceived = false;
            fromCollectorReceived = false;
        }
    }

    /**
     * Processes the message received from the dispatcher
     * @param receivedMessage the message received from the dispatcher
     * @param isDispatcher true if the message is from the dispatcher, false otherwise
     */
    protected void processDispatcher(String receivedMessage, boolean isDispatcher) {
        if (isDispatcher) {
            invoice.setInvoiceNumber(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
            invoice.setCustomerId(Long.parseLong(receivedMessage));
            invoice.setInvoiceDate(LocalDate.now());
            fromDispatcherReceived = true;
        }
    }

    /**
     * Processes the message received from the collector
     * @param receivedMessage the message received from the collector
     * @param isCollector true if the message is from the collector, false otherwise
     */
    protected void processCollector(String receivedMessage, boolean isCollector) {
        if (isCollector) {
            if (receivedMessage.equals("END")) {
                fromCollectorReceived = true;
            } else {
                String[] messageParts = receivedMessage.split(",");
                Station station = new Station();
                station.setUrl(messageParts[0]);
                station.setTotalKwh(Long.parseLong(messageParts[1]));
                invoice.getStations().add(station);
            }
        }
    }
}
