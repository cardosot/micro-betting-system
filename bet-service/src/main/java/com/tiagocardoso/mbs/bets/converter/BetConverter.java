package com.tiagocardoso.mbs.bets.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiagocardoso.mbs.datasources.converter.Converter;
import com.tiagocardoso.mbs.domain.Bet;

import java.util.Map;

public class BetConverter implements Converter<Map, Bet> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Bet convertFrom(Map document) {
        return objectMapper.convertValue(document, Bet.class);
    }

    @Override
    public Map convertTo(Bet bet) {
        return objectMapper.convertValue(bet, Map.class);
    }
}
