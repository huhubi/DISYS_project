# Distributed Systems Semester Project
An example setup for a Distributed Systems project. It contains five databases (PostgreSQL) with example data and a messaging queue (RabbitMQ).

## Services
- Customer Database
	- Contains customer data (id, first name, last name)
	- URL: localhost:30001
- Stations Database
	- Contains station data (id, db_url, latitude, longitude)
	- URL: localhost:30002
- Individual Station Databases
	- Contains customer station data (id, kwh, customer_id)
	- URL Station 1: localhost:30011
	- URL Station 2: localhost:30012
	- URL Station 3: localhost:30013
- Queue
	- URL: localhost:30003
	- Web: localhost:30083

## Requirements
- [Docker](https://docs.docker.com/get-docker/)

## User Guide

Start the databases with the following command in the databases folder:
```shell
docker-compose up
```
After that, start each sub component (DataCollectionDispatcher, DataCollectionService, InvoiceService, Frontend, PDFGenerator, SpringBootApp and StationDataCollector) with opening the project in IntelliJ IDEA and running the main class of each component.
We were folowing the following order:
1. DataCollectionDispatcher
2. DataCollectionReceiver
3. StationDataCollector
4. SpringBootApp
5. PDFGenerator
6. JavaFX

# Generating PDF

1. Once the applications have started, open the GUI for the desired application.
2. Enter the number of customers for whom you want to generate an invoice (1-3).
3. Wait for the PDF to be generated. The progress will be displayed in the application.
4. After the PDF is generated, click the "View" button to open the PDF file. The pdf is saved in the following project path : files/invoice/{customerId}.pdf

If you encounter any issues or have specific questions about the project, refer to the project documentation or consult the project's support resources.
Feel free to contact Matthias Huber (wi22b112@technikum-wien.at) per E-Mail.

For latest updates and news, visit the Github repo at [https://github.com/huhubi/DISYS_project]()
```shell

## RabbitMQ-Dashboard
- [RabbitMQ-Dashboard](http://localhost:30083)
- Username: guest
- Password: guest


## Documentations
- [RabbitMQ](https://www.rabbitmq.com/tutorials/tutorial-one-java.html)
