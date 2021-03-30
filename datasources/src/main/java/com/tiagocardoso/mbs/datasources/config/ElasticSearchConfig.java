package com.tiagocardoso.mbs.datasources.config;

import io.opentracing.Tracer;
import io.opentracing.contrib.elasticsearch.common.TracingHttpClientConfigCallback;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

@Configuration
@PropertySource("classpath:elastic.properties")
@ComponentScan(basePackages = {"com.tiagocardoso.mbs.datasources"})
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {

    @Autowired
    Tracer tracer;

    @Value("${elastic.host}" )
    private String hostName;

    @Value("${elastic.port}" )
    private int port;

    @Value("${elastic.scheme}" )
    private String scheme;

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        RestClientBuilder builder = RestClient.builder(
                new HttpHost(hostName, port, scheme))
                .setHttpClientConfigCallback(new TracingHttpClientConfigCallback(tracer));
        return new RestHighLevelClient(builder);
    }
}
