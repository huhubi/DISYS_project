package com.fhtechnikum.project.project;

import com.fhtechnikum.project.project.Database.DatabaseConfig;
import com.fhtechnikum.project.project.Database.DatabaseConnector;
import com.fhtechnikum.project.project.rabbitmq.RabbitMQService;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

/**
 * This class is responsible for dispatching the data collection to the correct data collector.
 * <p>
 * <i><br>
 *     Starts the data gathering job<br>
 *     Has knowledge about the available stations<br>
 *     Sends a message for every charging station to the Station Data Collector<br>
 *     Sends a message to the Data Collection Receiver, that a new job started
 * </i>
 */

@Slf4j
public class DataCollectionDispatcher {

	private static final String QUERY = "SELECT * FROM station";

	private final RabbitMQService dispatcherQueueService;
	private final RabbitMQService collectorQueueService;
	private final RabbitMQService receiverQueueService;
	private final DatabaseConnector databaseConnector;
	private final DatabaseConfig databaseConfig;

	public DataCollectionDispatcher(RabbitMQService dispatcherQueueService,
									RabbitMQService collectorQueueService,
									RabbitMQService receiverQueueService,
									DatabaseConnector databaseConnector,
									DatabaseConfig databaseConfig) {
		this.dispatcherQueueService = dispatcherQueueService;
		this.collectorQueueService = collectorQueueService;
		this.receiverQueueService = receiverQueueService;
		this.databaseConnector = databaseConnector;
		this.databaseConfig = databaseConfig;
	}

	public void init() {
		dispatchDataCollectionJob();
	}

	public static void main(String[] args) {
		// Initialize the Spring ApplicationContext
		ApplicationContext context = new AnnotationConfigApplicationContext("com.fhtechnikum.project.project.rabbitmq", "com.fhtechnikum.project.project.Database");

		// Get the RabbitMQService instances from the context
		RabbitMQService dispatcherQueueService = context.getBean("dataCollectionDispatcherQueue", RabbitMQService.class);
		RabbitMQService collectorQueueService = context.getBean("springbootAppQueue", RabbitMQService.class);
		RabbitMQService receiverQueueService = context.getBean("dataCollectionReceiverQueue", RabbitMQService.class);

		// Get the DatabaseConnector and DatabaseConfig instances from the context
		DatabaseConnector databaseConnector = context.getBean(DatabaseConnector.class);
		DatabaseConfig databaseConfig = context.getBean(DatabaseConfig.class);

		// Initialize the DataCollectionDispatcher instance
		DataCollectionDispatcher dataCollectionDispatcher = new DataCollectionDispatcher(dispatcherQueueService, collectorQueueService, receiverQueueService, databaseConnector, databaseConfig);
		dataCollectionDispatcher.init();
	}


	/**
	 * Receives the data collection job from the queue and dispatches it to the correct data collector.
	 */
	public void dispatchDataCollectionJob() {
		try {
			dispatcherQueueService.initialize();
			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				String receivedMessage = new String(delivery.getBody(), "UTF-8");
				log.info("Received message: {}", receivedMessage);
				try {
					receiverQueueService.initialize();
					collectorQueueService.initialize();
					sendMessageForEachStation(collectorQueueService, Long.parseLong(receivedMessage));
					receiverQueueService.publishMessage(receivedMessage);
				} catch (TimeoutException e) {
					throw new RuntimeException(e);
				}
			};
			dispatcherQueueService.getChannel().basicConsume(dispatcherQueueService.getQueueName(), true, deliverCallback, consumerTag -> {});
		} catch (IOException e) {
			log.error("Error in dispatching data collection job", e);
		}
	}

	/**
	 * Sends a message for every charging station to the Station Data Collector.
	 * @param queueService send from this queue service
	 * @param customerId the customer id
	 * @throws IOException problem with sending the message
	 * @throws TimeoutException timeout while sending the message
	 */
	public void sendMessageForEachStation(RabbitMQService queueService, long customerId) throws IOException, TimeoutException {
		DatabaseConfig.DataSourceProperties properties = databaseConfig.getDatasources().get("stations");
		databaseConnector.connect("stations");
		try (ResultSet resultSet = databaseConnector.executeSQLQuery(QUERY)) {
			while (true) {
				StationModel station = new StationModel();
				if (!resultSet.next()) {
					queueService.publishMessage("END");
					break;
				}
				station.setId(resultSet.getLong("id"));
				station.setDbUrl(resultSet.getString("db_url"));
				station.setLat(resultSet.getDouble("lat"));
				station.setLng(resultSet.getDouble("lng"));
				queueService.publishMessage(station.toCsv() + "," + customerId);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			databaseConnector.disconnect();
		}
	}
}
