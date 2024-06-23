package com.fhtechnikum.project;


import com.fhtechnikum.project.rabbitmq.RabbitMQService;
import com.fhtechnikum.project.rabbitmq.Queues;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Business logic for the data collection Spring Boot App.
 */
@Slf4j
@Service
public class RestEndpointService {
    private RabbitMQService messagePublisher;

    @Autowired
    public RestEndpointService(@Qualifier("dataCollectionDispatcherQueue") RabbitMQService messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    /**
     * Checks if customerId is valid and exists in the database
     */
    public boolean isCustomerIdValid(String customerId) {
        return customerId != null && !customerId.isEmpty() && customerId.matches("\\d+");
    }

    /**
     * Starts the data gathering job
     * @param customerId customer id for the data gathering job
     */
    public void publishDataGatheringJob(String customerId) {
        messagePublisher.setQueueName(Queues.DATA_COLLECTION_DISPATCHER);  // Set the appropriate queue name
        messagePublisher.initialize();
        messagePublisher.publishMessage(customerId);
    }

    /**
     * Gets PDF from PDF generator
     * @param customerId customer id for the data gathering job
     * @return true if PDF exists and is downloaded, false otherwise
     */
    public boolean downloadInvoice(String customerId, HttpServletResponse response) {
        String fileName = "invoice" + customerId + ".pdf";
        String relativePath = "files" + File.separator + fileName;
        Path absolutePath = Paths.get(System.getProperty("user.dir"), relativePath);
        File invoiceFile = absolutePath.toFile();

        if (invoiceFile.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(invoiceFile)) {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                response.setContentLength((int) invoiceFile.length());

                OutputStream outputStream = response.getOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                log.error("Failed to download invoice for customer ID: {}", customerId, e);
                return false;
            }
        } else {
            log.warn("Invoice file not found for customer ID: {}", customerId);
            return false;
        }
    }
}

