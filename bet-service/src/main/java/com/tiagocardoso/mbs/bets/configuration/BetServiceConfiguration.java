package com.tiagocardoso.mbs.bets.configuration;

import com.tiagocardoso.mbs.bets.converter.BetConverter;
import com.tiagocardoso.mbs.datasources.config.ElasticSearchConfig;
import com.tiagocardoso.mbs.datasources.converter.Converter;
import com.tiagocardoso.mbs.domain.Bet;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Configuration
@PropertySource("classpath:application.properties")
@Import(value = {ElasticSearchConfig.class})
public class BetServiceConfiguration {

    @Bean
    public RestTemplate restTemplate(
            RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    @Bean
    public Converter<Map, Bet> getElasticDocumentConverter() {
        return new BetConverter();
    }
}
