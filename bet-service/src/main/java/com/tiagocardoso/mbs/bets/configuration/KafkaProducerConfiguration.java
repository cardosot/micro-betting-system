package com.tiagocardoso.mbs.bets.configuration;

import com.tiagocardoso.mbs.bets.serializers.BetSerializer;
import com.tiagocardoso.mbs.domain.Bet;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.spring.TracingProducerFactory;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfiguration {

    @Autowired
    Tracer tracer;

    @Value("${kafka.bootstrap.servers}" )
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, Bet> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, BetSerializer.class);
        return new TracingProducerFactory<>(new DefaultKafkaProducerFactory<>(props), tracer);
    }

    @Bean
    public KafkaTemplate<String, Bet> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
