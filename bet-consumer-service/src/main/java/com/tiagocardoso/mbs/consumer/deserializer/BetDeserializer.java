package com.tiagocardoso.mbs.consumer.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiagocardoso.mbs.domain.Bet;
import org.apache.kafka.common.serialization.Deserializer;

public class BetDeserializer implements Deserializer<Bet> {

    private static final String UTF_8 = "UTF-8";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Bet deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(new String(data, UTF_8), Bet.class);
        } catch (Exception e) {
            return null;
        }
    }

}
