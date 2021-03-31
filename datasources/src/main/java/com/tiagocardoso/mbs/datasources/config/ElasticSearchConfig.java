package com.tiagocardoso.mbs.datasources.config;

import io.opentracing.Tracer;
import io.opentracing.contrib.elasticsearch.common.TracingHttpClientConfigCallback;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

import java.io.IOException;

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

    @Value("${elastic.index}" )
    private String index;

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        RestClientBuilder builder = RestClient.builder(
                new HttpHost(hostName, port, scheme))
                .setHttpClientConfigCallback(new TracingHttpClientConfigCallback(tracer));
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);

        ensureIndexIsCreated(restHighLevelClient, index);

        return restHighLevelClient;
    }

    /**
     * Lazy method to ensure index is already created
     *
     * @param restHighLevelClient
     * @param index
     */
    private void ensureIndexIsCreated(RestHighLevelClient restHighLevelClient, String index) {
        try {
            GetIndexRequest request = new GetIndexRequest(index);
            if(!restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT)) {
                CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
                restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
