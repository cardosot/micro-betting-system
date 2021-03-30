package com.tiagocardoso.mbs.accounts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AccountApplication {

    static Logger LOGGER = LoggerFactory.getLogger(AccountApplication.class);

    public static void main(String[] args) {
        LOGGER.info("Starting Account Application");
        SpringApplication.run(AccountApplication.class, args);
    }
}