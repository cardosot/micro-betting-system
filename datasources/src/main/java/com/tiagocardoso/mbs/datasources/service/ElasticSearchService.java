package com.tiagocardoso.mbs.datasources.service;

import com.tiagocardoso.mbs.datasources.converter.Converter;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;

@Service
public class ElasticSearchService<T> {

    @Value("${elastic.index}" )
    private String index;

    @Qualifier("elasticsearchClient")
    @Autowired
    RestHighLevelClient highLevelClient;

    @Autowired
    Converter<Map, T> elasticDocumentConverter;

    public T findById(String id) throws IOException {
        final GetRequest request = new GetRequest(index, id);
        final GetResponse getResponse = highLevelClient.get(request, RequestOptions.DEFAULT);
        return elasticDocumentConverter.convertFrom(getResponse.getSourceAsMap());
    }

    public List<T> findByQuery(String field, String value) throws IOException {
        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery(field, value));
        final SearchRequest searchRequest = new SearchRequest(index).source(searchSourceBuilder);

        final SearchResponse response = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        return Arrays.stream(response.getHits().getHits())
                .map(SearchHit::getSourceAsMap)
                .map( hit -> elasticDocumentConverter.convertFrom(hit))
                .collect(toList());
    }

    public List<T> findAll() throws IOException {
        final SearchRequest searchRequest = new SearchRequest(index);
        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        final SearchResponse response = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        return Arrays.stream(response.getHits().getHits())
                .map(SearchHit::getSourceAsMap)
                .map( hit -> elasticDocumentConverter.convertFrom(hit))
                .collect(toList());
    }

    public boolean create(String id, T document) throws IOException {
        final Map documentMap = elasticDocumentConverter.convertTo(document);
        final IndexRequest indexRequest = new IndexRequest(index).id(id).source(documentMap);
        final IndexResponse indexResponse = highLevelClient.index(indexRequest, RequestOptions.DEFAULT);

        return RestStatus.CREATED.equals(indexResponse.status());
    }

    public boolean update(String id, T document) throws IOException {

        final UpdateRequest updateRequest = new UpdateRequest(index, id);
        updateRequest.doc(document);

        final UpdateResponse updateResponse = highLevelClient.update(updateRequest, RequestOptions.DEFAULT);

        return RestStatus.CREATED.equals(updateResponse.status());
    }

    public boolean updateByScript(String id, String script) throws IOException {

        final UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest(index);
        updateByQueryRequest.setQuery(new TermQueryBuilder("_id", id));
        updateByQueryRequest.setScript(new Script(ScriptType.INLINE, "painless", script, emptyMap()));

        final BulkByScrollResponse bulkByScrollResponse = highLevelClient.updateByQuery(updateByQueryRequest, RequestOptions.DEFAULT);
        return bulkByScrollResponse.getStatus().getNoops() == 0;
    }
}
