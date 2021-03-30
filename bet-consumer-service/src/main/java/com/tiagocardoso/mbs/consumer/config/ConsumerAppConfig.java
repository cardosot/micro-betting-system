package com.tiagocardoso.mbs.consumer.config;

import com.tiagocardoso.mbs.consumer.converter.BetConverter;
import com.tiagocardoso.mbs.consumer.deserializer.BetDeserializer;
import com.tiagocardoso.mbs.datasources.config.ElasticSearchConfig;
import com.tiagocardoso.mbs.datasources.converter.Converter;
import com.tiagocardoso.mbs.domain.Bet;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.spring.TracingConsumerFactory;
import io.opentracing.contrib.kafka.spring.TracingKafkaAspect;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@PropertySource("classpath:application.properties")
@Import(value = {ElasticSearchConfig.class})
public class ConsumerAppConfig {

    @Autowired
    Tracer tracer;

    @Value("${kafka.bootstrap.servers}" )
    private String bootstrapServers;

    @Value("${kafka.group.id}" )
    private String groupId;

    @Value("${kafka.auto.offset.reset}" )
    private String autoOffsetReset;

    @Bean
    public ConsumerFactory<String, Bet> betConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, BetDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        return new TracingConsumerFactory<>(new DefaultKafkaConsumerFactory<>(props),  tracer );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Bet> betKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Bet> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(betConsumerFactory());
        return factory;
    }

    @Bean
    public TracingKafkaAspect tracingKafkaAspect() {
        return new TracingKafkaAspect( tracer );
    }

    @Bean
    public Converter<Map, Bet> getElasticDocumentConverter() {
        return new BetConverter();
    }
}
