package com.tiagocardoso.mbs.consumer.service;

import com.tiagocardoso.mbs.datasources.service.ElasticSearchService;
import com.tiagocardoso.mbs.domain.Bet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerService.class);

    @Autowired
    ElasticSearchService<Bet> elasticSearchService;

    @KafkaListener(topics = "${kafka.topic}", containerFactory = "betKafkaListenerContainerFactory")
    public void betListener(Bet bet) {
        try {
            elasticSearchService.create(String.valueOf(bet.getBetId()), bet);
        } catch (IOException e) {
            LOGGER.error("operation=betListener, error={}", e.getMessage());
        }
    }
}
