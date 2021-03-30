package com.tiagocardoso.mbs.bets.serializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiagocardoso.mbs.domain.Bet;
import org.apache.kafka.common.serialization.Serializer;

public class BetSerializer implements Serializer<Bet> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, Bet data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
