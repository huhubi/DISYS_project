package com.fhtechnikum.project.project;

import com.fhtechnikum.project.project.Database.DatabaseConnector;
import com.fhtechnikum.project.project.rabbitmq.RabbitMQService;
import com.fhtechnikum.project.project.rabbitmq.Queues;
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
	private final RabbitMQService receiverQueueService;
	private final RabbitMQService collectorQueueService;
	private final DatabaseConnector databaseConnector;

	public DataCollectionDispatcher(RabbitMQService dispatcherQueueService,
									RabbitMQService receiverQueueService,
									RabbitMQService collectorQueueService,
									DatabaseConnector databaseConnector) {
		this.dispatcherQueueService = dispatcherQueueService;
		this.receiverQueueService = receiverQueueService;
		this.collectorQueueService = collectorQueueService;
		this.databaseConnector = databaseConnector;
	}

	public void init() {
		dispatchDataCollectionJob();
	}

	public static void main(String[] args) {
		// Initialize the Spring ApplicationContext
		ApplicationContext context = new AnnotationConfigApplicationContext("com.fhtechnikum.project.project.rabbitmq", "com.fhtechnikum.project.project.Database");

		// Get the RabbitMQService instances from the context
		RabbitMQService dispatcherQueueService = context.getBean(RabbitMQService.class);
		RabbitMQService receiverQueueService = context.getBean(RabbitMQService.class);
		RabbitMQService collectorQueueService = context.getBean(RabbitMQService.class);

		// Get the DatabaseConnector and DatabaseConfig instances from the context
		DatabaseConnector databaseConnector = context.getBean(DatabaseConnector.class);

		// Initialize the DataCollectionDispatcher instance
		DataCollectionDispatcher dataCollectionDispatcher = new DataCollectionDispatcher(dispatcherQueueService, receiverQueueService, collectorQueueService, databaseConnector);
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
					sendMessageForEachStation(collectorQueueService, Long.parseLong(receivedMessage));
					receiverQueueService.publishMessage(receivedMessage, Queues.DATA_COLLECTION_RECEIVER);
				} catch (TimeoutException e) {
					throw new RuntimeException(e);
				}
			};
			dispatcherQueueService.getChannel().basicConsume(Queues.SPRING_BOOT_APP.getQueueName(), true, deliverCallback, consumerTag -> {});
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
		databaseConnector.connect("stations");
		try (ResultSet resultSet = databaseConnector.executeSQLQuery(QUERY)) {
			while (true) {
				StationModel station = new StationModel();
				if (!resultSet.next()) {
					queueService.publishMessage("END", Queues.DATA_COLLECTION_DISPATCHER);
					break;
				}
				station.setId(resultSet.getLong("id"));
				station.setDbUrl(resultSet.getString("db_url"));
				station.setLat(resultSet.getDouble("lat"));
				station.setLng(resultSet.getDouble("lng"));
				queueService.publishMessage(station.toCsv() + "," + customerId, Queues.DATA_COLLECTION_DISPATCHER);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			databaseConnector.disconnect();
		}
	}
}
