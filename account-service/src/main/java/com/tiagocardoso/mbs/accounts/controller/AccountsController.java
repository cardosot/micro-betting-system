package com.tiagocardoso.mbs.accounts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.tiagocardoso.mbs.datasources.service.ElasticSearchService;
import com.tiagocardoso.mbs.domain.Account;
import com.tiagocardoso.mbs.domain.OperationResult;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static com.tiagocardoso.mbs.domain.OperationResult.Result.*;

@RestController
@RequestMapping("/api/v1")
public class AccountsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountsController.class);

    @Autowired
    ElasticSearchService<Account> elasticSearchService;

    @Autowired
    private Tracer tracer;

    /**
     * Get all accounts
     * @return all accounts
     */
    @GetMapping("/accounts")
    public List<Account> getAllAccounts(@RequestHeader HttpHeaders headers) {
        LOGGER.info("operation=getAllAccounts");
        try {
            return elasticSearchService.findAll();
        } catch (IOException e) {
            tracer.activeSpan().setTag("error", true);
            tracer.activeSpan().log(e.getMessage());
            return Lists.newArrayList();
        }
    }

    /**
     * Get account
     *
     * @param headers
     * @param accountId
     * @return the Account
     */
    @GetMapping("/account")
    public Account getAccount(@RequestHeader HttpHeaders headers, String accountId) {
        LOGGER.info("operation=getAccount, accountId={}", accountId);
        try {
            return elasticSearchService.findById(accountId);
        } catch (IOException e) {
            tracer.activeSpan().setTag("error", true);
            tracer.activeSpan().log(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Create account.
     *
     * @param account
     * @return the bet
     */
    @PostMapping("/createAccount")
    public OperationResult createAccount(@RequestHeader HttpHeaders headers, @Valid @RequestBody Account account) {
        LOGGER.info("operation=createAccount, account={}", account);

        try {

            boolean result = elasticSearchService.create(String.valueOf(account.getAccountId()), account);

            return handleResult(result, String.valueOf(account.getAccountId()));
        } catch (JsonProcessingException e) {
            LOGGER.error("operation=createBet, msg='error converting to json'");
            tracer.activeSpan().setTag("error", true);
            tracer.activeSpan().log(e.getMessage());
            return new OperationResult(FAILURE, "error converting to json");
        } catch (IOException e) {
            tracer.activeSpan().setTag("error", true);
            tracer.activeSpan().log(e.getMessage());
            return new OperationResult(FAILURE, e.getMessage());
        }
    }

    /**
     * Withdraw a given amount from account
     *
     * @param headers
     * @param accountId
     * @return the Account
     */
    @GetMapping("/withdraw")
    public OperationResult withdraw(@RequestHeader HttpHeaders headers, String accountId, double amount) {
        LOGGER.info("operation=withDraw, accountId={}, amount={}", accountId, amount);
        try {
            final String script = "if (ctx._source.balance - " + amount + " < 0) { ctx.op = 'noop' } else { ctx._source.balance-=" + amount + " }";
            boolean result = elasticSearchService.updateByScript(accountId, script);
            return handleResult(result, accountId);
        } catch (IOException e) {
            tracer.activeSpan().setTag("error", true);
            tracer.activeSpan().log(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Deposit a given amount into account
     *
     * @param headers
     * @param accountId
     * @return the Account
     */
    @GetMapping("/deposit")
    public OperationResult deposit(@RequestHeader HttpHeaders headers, String accountId, double amount) {
        LOGGER.info("operation=deposit, accountId={}, amount={}", accountId, amount);
        try {
            final String script = "ctx._source.balance+=" + amount + ";";
            boolean result = elasticSearchService.updateByScript(accountId, script);
            return handleResult(result, accountId);
        } catch (IOException e) {
            tracer.activeSpan().setTag("error", true);
            tracer.activeSpan().log(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private OperationResult handleResult(boolean result, String accountId) {
        LOGGER.info("operation=handleResult, result={}", result);
        if (result) {
            return new OperationResult(SUCCESS, "accountId=" + accountId);
        }
        return new OperationResult(FAILURE, "accountId=" + accountId);
    }

}