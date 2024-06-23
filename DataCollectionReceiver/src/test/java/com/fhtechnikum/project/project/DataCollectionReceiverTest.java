package com.fhtechnikum.project.project;

import com.fhtechnikum.project.project.rabbitmq.RabbitMQService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class DataCollectionReceiverTest {
    private RabbitMQService dispatcherReceiverQueue;
    private RabbitMQService collectorReceiverQueue;
    private RabbitMQService receiverPdfQueue;
    private DataCollectionReceiver dataCollectionReceiver;

    @BeforeEach
    void setUp() {
        dispatcherReceiverQueue = mock(RabbitMQService.class);
        collectorReceiverQueue = mock(RabbitMQService.class);
        receiverPdfQueue = mock(RabbitMQService.class);
        dataCollectionReceiver = new DataCollectionReceiver(dispatcherReceiverQueue, collectorReceiverQueue, receiverPdfQueue);
    }

    @Test
    void shouldProcessDispatcherMessage() {
        dataCollectionReceiver.processDispatcher("123", true);
        assertTrue(dataCollectionReceiver.isFromDispatcherReceived());
    }

    @Test
    void shouldNotProcessNonDispatcherMessage() {
        dataCollectionReceiver.processDispatcher("123", false);
        assertFalse(dataCollectionReceiver.isFromDispatcherReceived());
    }

    @Test
    void shouldProcessCollectorMessage() {
        dataCollectionReceiver.processCollector("END", true);
        assertTrue(dataCollectionReceiver.isFromCollectorReceived());
    }

    @Test
    void shouldNotProcessNonCollectorMessage() {
        dataCollectionReceiver.processCollector("END", false);
        assertFalse(dataCollectionReceiver.isFromCollectorReceived());
    }

    @Test
    void shouldProcessJobWhenBothMessagesReceived() throws IOException {
        dataCollectionReceiver.processDispatcher("123", true);
        dataCollectionReceiver.processCollector("END", true);
        dataCollectionReceiver.processJob();
        assertFalse(dataCollectionReceiver.isFromDispatcherReceived());
        assertFalse(dataCollectionReceiver.isFromCollectorReceived());
    }

    @Test
    void shouldNotProcessJobWhenOnlyDispatcherMessageReceived() throws IOException {
        dataCollectionReceiver.processDispatcher("123", true);
        dataCollectionReceiver.processJob();
        assertTrue(dataCollectionReceiver.isFromDispatcherReceived());
        assertFalse(dataCollectionReceiver.isFromCollectorReceived());
    }

    @Test
    void shouldNotProcessJobWhenOnlyCollectorMessageReceived() throws IOException {
        dataCollectionReceiver.processCollector("END", true);
        dataCollectionReceiver.processJob();
        assertFalse(dataCollectionReceiver.isFromDispatcherReceived());
        assertTrue(dataCollectionReceiver.isFromCollectorReceived());
    }
}