package com.fhtechnikum.project.project.rabbitmq;

import lombok.Getter;

@Getter
public enum Queues {
    //RED MESSAGE
    SPRING_BOOT_APP("springBootAppQueue"),
    //GREEN MESSAGE
    DATA_COLLECTION_DISPATCHER("dataCollectionDispatcherQueue"),
    //BLUE MESSAGE
    STATION_DATA_COLLECTOR("deleteCharQueue"),
    //PURPLE MESSAGE
    DATA_COLLECTION_RECEIVER("removeSpacesQueue"),
    //YELLOW MESSAGE
    PDF_GENERATOR("pdfGeneratorQueue");

    private final String queueName;

    Queues(String queueName) {
        this.queueName = queueName;
    }

}
