package com.tiagocardoso.mbs.accounts.configuration;

import com.tiagocardoso.mbs.accounts.converter.AccountConverter;
import com.tiagocardoso.mbs.datasources.config.ElasticSearchConfig;
import com.tiagocardoso.mbs.datasources.converter.Converter;
import com.tiagocardoso.mbs.domain.Account;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Configuration
@PropertySource("classpath:application.properties")
@Import(value = {ElasticSearchConfig.class})
public class AccountConfiguration {

    @Bean
    public Converter<Map, Account> getElasticDocumentConverter() {
        return new AccountConverter();
    }
}