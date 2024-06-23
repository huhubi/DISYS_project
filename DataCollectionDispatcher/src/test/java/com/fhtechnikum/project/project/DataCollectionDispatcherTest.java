package com.fhtechnikum.project.project;

import com.fhtechnikum.project.project.Database.DatabaseConfig;
import com.fhtechnikum.project.project.Database.DatabaseConnector;
import com.fhtechnikum.project.project.rabbitmq.RabbitMQService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.ResultSet;
import java.util.Collections;

import static org.mockito.Mockito.*;

public class DataCollectionDispatcherTest {

    @Mock
    private RabbitMQService dispatcherQueueService;
    @Mock
    private RabbitMQService collectorQueueService;
    @Mock
    private RabbitMQService receiverQueueService;
    @Mock
    private DatabaseConnector databaseConnector;
    @Mock
    private DatabaseConfig databaseConfig;

    private DataCollectionDispatcher dataCollectionDispatcher;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        dataCollectionDispatcher = new DataCollectionDispatcher(dispatcherQueueService, collectorQueueService, receiverQueueService, databaseConnector, databaseConfig);
    }

    @Test
    public void sendMessageForEachStation_HappyPath() throws Exception {
        // Given
        DatabaseConfig.DataSourceProperties properties = mock(DatabaseConfig.DataSourceProperties.class);
        when(databaseConfig.getDatasources()).thenReturn(Collections.singletonMap("stations", properties));
        when(databaseConnector.executeSQLQuery(anyString())).thenReturn(mock(ResultSet.class));

        // When
        dataCollectionDispatcher.sendMessageForEachStation(collectorQueueService, 1L);

        // Then
        verify(databaseConnector).connect("stations");
        verify(databaseConnector).executeSQLQuery(anyString());
        verify(databaseConnector).disconnect();
    }
}