package com.csdn.jinxu.elasticsearch;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.SearchHits;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * 实现描述：es　client
 *
 * @author jin.xu
 * @version v1.0.0
 * @see
 * @since 16-8-24 下午9:18
 */
public class ESClient {

    private static Integer TIME_OUT_MILLIS = 1000;

    private TransportClient transportClient;

    private String username;

    private String password;


    public void createESClient(String clusterName, String nodeName, String host, Integer port, String username, String password) throws UnknownHostException {
        Settings settings = Settings.settingsBuilder().put("cluster.name", clusterName).put("node.name", nodeName).build();
        transportClient = TransportClient.builder().settings(settings).build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
        this.username = username;
        this.password = password;
    }

    public SearchHits query(String indexName, String indexType, MatchQueryBuilder matchQueryBuilder) {
        SearchResponse actionGet = transportClient
                .prepareSearch(indexName)
                .setTypes(indexType)
                .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
                .setFrom(0)
                .setSize(10)
                .setTimeout(TimeValue.timeValueMillis(TIME_OUT_MILLIS))
                .setQuery(matchQueryBuilder)
                        //.putHeader(username, password)
                .setNoFields()
                .execute().actionGet();

        SearchHits hits = actionGet.getHits();

        return hits;
    }

    public BulkResponse createESIndex(String indexName, String indexType, String id, Map<String, String> dataMap) {
        BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
        bulkRequestBuilder.add(transportClient.prepareIndex(indexName, indexType)
                .setId(String.valueOf(id)).setSource(JSON.toJSONString(dataMap)));
        // .putHeader(username, password));

        return bulkRequestBuilder.execute().actionGet();
    }

    public DeleteResponse deleteESIndex(String indexName, String indexType, String id) {
        return transportClient.prepareDelete(indexName, indexType, id)
                //.putHeader(username, password)
                .execute().actionGet();
    }

    public void close() throws Exception {
        if (transportClient != null) {
            transportClient.close();
            transportClient = null;
        }
    }
}
