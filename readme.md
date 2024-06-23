# Distributed Systems Semester Project
This repository contains the semester project for the Distributed Systems course at the University of Applied Sciences FH Technikum Wien from Matthias Huber, David Berger and Gregoire Bartek.
The readme contains all necessary information for setting up the project and running it.

## Responsibilities

- Java FX (Matthias Huber)
- Spring Boot (Matthias Huber)
- Messaging Queue (David Berger)
- PDF Generation (Gregoire Bartek)

## Architecture

![architecture.png](architecture.png)
## Services

Frontend (Java FX):
- Calling invoice generation to endpoint http://localhost:8080/invoices/ + customerId.
  - The customerId is stored as an String in the frontend. 

Backend Services:
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

## Start
```shell
docker-compose up
```

## RabbitMQ-Dashboard
- [RabbitMQ-Dashboard](http://localhost:30083)
- Username: guest
- Password: guest


## Documentations
- [RabbitMQ](https://www.rabbitmq.com/tutorials/tutorial-one-java.html)

