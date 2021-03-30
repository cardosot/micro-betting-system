package com.tiagocardoso.mbs.bets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BetApplication {

    static Logger LOGGER = LoggerFactory.getLogger(BetApplication.class);

    public static void main(String[] args) {
        LOGGER.info("Starting Bet Application");
        SpringApplication.run(BetApplication.class, args);
    }
}