package com.tiagocardoso.mbs.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BetConsumerApplication {

    static Logger LOGGER = LoggerFactory.getLogger(BetConsumerApplication.class);

    public static void main(String[] args) {
        LOGGER.info("Starting Consumer Application");
        SpringApplication.run(BetConsumerApplication.class, args);
    }
}