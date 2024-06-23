package com.fhtechnikum.project.project;

import com.fhtechnikum.project.project.Database.DatabaseConnector;
import com.fhtechnikum.project.project.rabbitmq.RabbitMQService;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.fhtechnikum.project.project.rabbitmq.Queues.*;

/**
 * This class is responsible for collecting the data from the station. <br>
 *
 * <i> <br>
 *     Gathers data for a specific customer from a specific charging station<br>
 *     Sends data to the Data Collection Receiver
 *</i>
 */
@Slf4j
public class StationDataCollector {
	private static final String QUERY = "SELECT * FROM charge WHERE customer_id = ?";
	private final RabbitMQService dispatcherCollectorQueue;
	private final RabbitMQService collectorReceiverQueue;
	private final DatabaseConnector chargeDb;

	public StationDataCollector(DatabaseConnector chargeDb,
								RabbitMQService dispatcherCollectorQueue,
								RabbitMQService collectorReceiverQueue) {
		this.chargeDb = chargeDb;
		this.dispatcherCollectorQueue = dispatcherCollectorQueue;
		this.collectorReceiverQueue = collectorReceiverQueue;
	}


	public void init() {
		gatherDataForSpecificPersonSpecificCharge();
	}

	public static void main(String[] args) {
		// Initialize the Spring ApplicationContext
		ApplicationContext context = new AnnotationConfigApplicationContext("com.fhtechnikum.project.project.rabbitmq", "com.fhtechnikum.project.project.Database");

		// Get the RabbitMQService instances from the context
		RabbitMQService dispatcherCollectorQueue = context.getBean("dataCollectionDispatcherQueue", RabbitMQService.class);
		RabbitMQService collectorReceiverQueue = context.getBean("stationDataCollectorQueue", RabbitMQService.class);

		// Get the DatabaseConnector instance from the context
		DatabaseConnector databaseConnector = context.getBean(DatabaseConnector.class);

		// Initialize the StationDataCollector instance
		StationDataCollector stationDataCollector = new StationDataCollector(databaseConnector, dispatcherCollectorQueue, collectorReceiverQueue);
		stationDataCollector.init();
	}



	/**
	 * Gathers data for a specific customer from a specific charging station
	 * and sends it to the Data Collection Receiver.
	 */
	public void gatherDataForSpecificPersonSpecificCharge() {
		try {
			dispatcherCollectorQueue.initialize();
			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				String receivedMessage = new String(delivery.getBody(), "UTF-8");
				log.info("Received message: {}", receivedMessage);
				collectorReceiverQueue.initialize();
				if (receivedMessage.equals("END")) {
					collectorReceiverQueue.publishMessage("END");
				} else {
					String[] urlAndCustomerId = receivedMessage.split(",");
					String data = getSpecificDataForSpecificUrl(urlAndCustomerId[0], Long.parseLong(urlAndCustomerId[1]));
					collectorReceiverQueue.publishMessage(data);
				}
			};
			dispatcherCollectorQueue.getChannel().basicConsume(dispatcherCollectorQueue.getQueueName(), true, deliverCallback, consumerTag -> {});
		} catch (IOException e) {
			log.error("Error in gathering data", e);
		}
	}

	public String getSpecificDataForSpecificUrl(String url, long customerId) {
		chargeDb.connect(url);
		List<ChargeModel> charges = new ArrayList<>();
		StringBuilder csv = new StringBuilder();
		long totalKwh = 0;

		try (PreparedStatement statement = chargeDb.getConnection().prepareStatement(QUERY)) {
			statement.setLong(1, customerId);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					ChargeModel charge = new ChargeModel();
					charge.setCustomerId(resultSet.getLong("customer_id"));
					charge.setKwh(resultSet.getLong("kwh"));
					charges.add(charge);
					totalKwh += charge.getKwh();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			chargeDb.disconnect();
		}
		csv.append(url).append(",").append(totalKwh);

		return csv.toString();
	}
}
