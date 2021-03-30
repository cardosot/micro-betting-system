package com.tiagocardoso.mbs.bets.controller;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.google.common.collect.Lists;
import com.tiagocardoso.mbs.datasources.service.ElasticSearchService;
import com.tiagocardoso.mbs.domain.Bet;
import com.tiagocardoso.mbs.domain.OperationResult;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1")
public class BetsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BetsController.class);

    @Value("${kafka.topic}" )
    private String topic;

    @Autowired
    private Tracer tracer;

    @Autowired
    ElasticSearchService<Bet> elasticSearchService;

    @Autowired
    private KafkaTemplate<String, Bet> kafkaTemplate;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Get all bets
     * @return all bets
     */
    @GetMapping("/bets")
    public List<Bet> getAllBets(@RequestHeader HttpHeaders headers) {
        LOGGER.info("operation=getAllBets");
        try {
            return elasticSearchService.findAll();
        } catch (IOException e) {
            LOGGER.error("operation=getAllBets, error={}", e.getMessage());
            tracer.activeSpan().setTag("error", true);
            tracer.activeSpan().log(e.getMessage());
            return Lists.newArrayList();
        }
    }

    /**
     * Get bets from user
     * @return get bet from user
     */
    @GetMapping("/betsFromUser")
    public List<Bet> getBetsFromUser(@RequestHeader HttpHeaders headers, String accountId) {
        LOGGER.info("operation=getBetsFromUser");

        try {
            return elasticSearchService.findByQuery("accountId", accountId);
        } catch (IOException e) {
            LOGGER.error("operation=getBetsFromUser, error={}", e.getMessage());
            tracer.activeSpan().setTag("error", true);
            tracer.activeSpan().log(e.getMessage());
            return Lists.newArrayList();
        }
    }

    /**
     * Create bet
     *
     * @param bet
     * @return the bet
     */
    @PostMapping(value = "/createBet")
    public OperationResult createBet(@RequestHeader HttpHeaders headers, @Valid @RequestBody Bet bet) {
        LOGGER.info("operation=createBet, bet={}", bet);

        double totalSkate = bet.getLegs().stream().mapToDouble(leg -> leg.getSelection().getStake()).sum();

        Span span = tracer.activeSpan();

        try {
            OperationResult accountWithdrawResult = restTemplate.getForEntity(
                    "http://localhost:8082/api/v1/withdraw?accountId=" + bet.getAccountId() + "&amount=" + totalSkate ,
                    OperationResult.class,
                    headers).getBody();

            if(accountWithdrawResult != null && accountWithdrawResult.getResult().equals(OperationResult.Result.FAILURE)) {
                LOGGER.error("operation=createBet, error='Account has no balance'");
                span.setTag("error", true);
                span.log("Account has no balance");
                return new OperationResult(OperationResult.Result.FAILURE, "No balance");
            }
        } catch (Exception e) {
            LOGGER.error("operation=createBet, error={}", e.getMessage());
            span.setTag("error", true);
            return new OperationResult(OperationResult.Result.FAILURE, e.getMessage());
        }

        try {
            ProducerRecord<String, Bet> betRecord = new ProducerRecord<>(topic, String.valueOf(bet.getBetId()), bet);
            kafkaTemplate.send(betRecord).get();
            return new OperationResult(OperationResult.Result.SUCCESS, "Success");
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("operation=createBet, error={}", e.getMessage());
            span.setTag("error", true);
            span.log(e.getMessage());
            return new OperationResult(OperationResult.Result.FAILURE, e.getMessage());
        }
    }
}