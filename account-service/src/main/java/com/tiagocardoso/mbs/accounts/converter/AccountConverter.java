package com.tiagocardoso.mbs.accounts.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiagocardoso.mbs.datasources.converter.Converter;
import com.tiagocardoso.mbs.domain.Account;

import java.util.Map;

public class AccountConverter implements Converter<Map, Account> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Account convertFrom(Map document) {
        return objectMapper.convertValue(document, Account.class);
    }

    @Override
    public Map convertTo(Account account) {
        return objectMapper.convertValue(account, Map.class);
    }
}
