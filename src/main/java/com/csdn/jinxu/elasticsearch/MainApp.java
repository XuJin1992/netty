package com.csdn.jinxu.elasticsearch;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.net.UnknownHostException;
import java.util.Map;

/**
 * 实现描述：es　main方法
 *
 * @author jin.xu
 * @version v1.0.0
 * @see
 * @since 16-8-24 下午9:17
 */
public class MainApp {

    public static void main(String[] args) throws UnknownHostException {
        String clusterName = "person";
        String nodeName = "jin.xu.es";
        String host = "localhost";
        Integer port = 9300;
        String username = "admin";
        String password = "123456";

        String indexName = "jin.xu.es";
        String indexType = "article";

        ESClient esClient = new ESClient();
        esClient.createESClient(clusterName, nodeName, host, port, username, password);

        String id = "1";
        Map<String, String> dataMap = Maps.newHashMap();
        dataMap.put("title", "线段树");
        dataMap.put("content", "线段树是一种基于二分思想的快速检索的二叉树结构");
        esClient.createESIndex(indexName, indexType, id, dataMap);

        id = "2";
        dataMap.clear();
        dataMap.put("title", "avl树");
        dataMap.put("content", "avl树是一种左右子树高度不超过1的二叉树结构");
        esClient.createESIndex(indexName, indexType, id, dataMap);


        String fieldName = "content";
        String fieldKeyValue = "第一次我";
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(fieldName, fieldKeyValue);
        SearchHits hits = esClient.query(indexName, indexType, matchQueryBuilder);

        System.out.println("max score:" + hits.getMaxScore() + ", hits count:" + hits.getTotalHits());
        for (SearchHit hit : hits.getHits()) {
            Map<String, Object> result = hit.getSource();
            System.out.println("id:" + hit.getId() + ",score:" + hit.getScore() + ",source:" + JSON.toJSONString(result));
        }
    }
}
