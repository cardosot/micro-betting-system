# Micro Betting System

Spring Boot microservices to exemplify Jaeger integration on a very simple betting system.

AccountService operations:
 - createAccount(account)
 - getAllAccounts
 - getAccount(accountId)
 - withdraw(accountId, amount)
 - deposit(accountId, amount)

BetService operations
 - createBet(bet) - calls AccountService `withdraw` endpoint and in case of success publishes the Bet on Kafka `{kafka.topic}`
 - getAllBets
 - getBetsFromUser(accountId)

BetConsumerService reads each Bet from Kafka `{kafka.topic}` and stores on ElasticSearch `{elastic.index}`.


# Architecture

![GitHub Logo](docs/mbs.png)

# How to run

## 1. Run Jaeger All-In-One, Elastic, Kafka and Zookeeper

Deploy [docker-compose.yml](infra/docker/docker-compose.yml):

```
docker-compose up
```

Jaeger UI is available at [http://127.0.0.1:16686/](http://127.0.0.1:16686/)

## 2. Run AccountService, BetService and ConsumerService

## 3. REST API

Swagger UI:
 - [BetService](http://127.0.0.1:8081/swagger-ui/index.html)
 - [AccountService](http://127.0.0.1:8082/swagger-ui.html)

Postman collection available [here](tools/postman/MBS.postman_collection.json).
