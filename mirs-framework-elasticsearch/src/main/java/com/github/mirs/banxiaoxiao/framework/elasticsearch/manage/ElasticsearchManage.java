package com.github.mirs.banxiaoxiao.framework.elasticsearch.manage;

import com.alibaba.fastjson.JSONObject;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.elasticsearch.common.ElasticsearchPage;
import com.github.mirs.banxiaoxiao.framework.elasticsearch.config.ElasticsearchProperties;
import com.github.mirs.banxiaoxiao.framework.elasticsearch.constans.ElasticsearchConstants;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.PutIndexTemplateRequest;
import org.elasticsearch.client.tasks.TaskSubmissionResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.engine.VersionConflictEngineException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.*;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.github.mirs.banxiaoxiao.framework.elasticsearch.constans.ElasticsearchConstants.*;

/**
 * ES ??????
 *
 * @author: bc
 * @date: 2021-07-16 16:46
 **/
@Slf4j
public final class ElasticsearchManage implements DisposableBean {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ElasticsearchProperties elasticsearchProperties;

    private static RestHighLevelClient client;

    private static ElasticsearchProperties properties;

    @PostConstruct
    public void init() {
        client = this.restHighLevelClient;
        properties = this.elasticsearchProperties;
        log.info("Elasticsearch init");
    }

    @Override
    public void destroy() throws Exception {
        client.close();
        restHighLevelClient.close();
    }

    /**
     * ????????????????????????
     *
     * @param index
     * @return if success Return true
     */
    public static boolean isIndexExist(String index) {
        try {
            GetIndexRequest request = new GetIndexRequest(index);
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ???????????????????????????
     *
     * @param index
     * @return
     */
    public static boolean createIndex(String index) {
        return createIndex(index, ElasticsearchConstants.NUMBER_OF_SHARDS, properties.isCluster() ? ElasticsearchConstants.NUMBER__OF_REPLICAS : 0, ElasticsearchConstants.MAX_RESULT_WINDOW);
    }

    /**
     * ????????????
     *
     * @param index
     * @return
     */
    public static boolean createIndex(String index, Integer shards, Integer replicas, long maxResult) {
        try {
            if (isIndexExist(index)) {
                log.warn("index: {} already exists", index);
                return true;
            }
            CreateIndexRequest request = new CreateIndexRequest(index);
            request.settings(Settings.builder()
                    .put("index.number_of_shards", shards)
                    .put("index.number_of_replicas", replicas)
                    .put("index.max_result_window", maxResult <= 10000 ? 10000 : maxResult));//?????????1000???
            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
            if (response.isAcknowledged()) {
                log.info("Index :{} create success!", index);
                return true;
            } else {
                TComLogs.error("index : {} create failed,response isNotAcknowledged", index);
            }
        } catch (Exception e) {
            TComLogs.error("index : {} create failed", index);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ????????????: ???????????????????????????
     * ??????????????????????????????OOM?????????
     */
    private static boolean updateIndices(String index, int maxResult) {
        try {
            if (maxResult <= 10000)
                return true;
            PutIndexTemplateRequest request = new PutIndexTemplateRequest(index);
            request.settings(Settings.builder().put("index.max_result_window", maxResult));
            AcknowledgedResponse response = client.indices().putTemplate(request, RequestOptions.DEFAULT);
            if (response.isAcknowledged()) {
                log.info("Index :{} updateIndices :{} success!", index, maxResult);
                return true;
            } else {
                TComLogs.error("index : {} updateIndices :{} failed,response isNotAcknowledged", index, maxResult);
            }
        } catch (Exception e) {
            TComLogs.error("index : {} updateIndices failed", index);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ????????????
     *
     * @param index
     * @return
     */
    public static boolean deleteIndex(String index) {
        try {
            if (!isIndexExist(index)) {
                log.info("Index is not exits!");
            }
            DeleteIndexRequest request = new DeleteIndexRequest(index);
            AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
            if (response.isAcknowledged()) {
                log.info("delete index " + index + "  successfully!");
                return true;
            } else {
                log.error("delete index failed" + index);
            }
        } catch (Exception e) {
            log.error("delete index failed" + index);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ????????????,??????UUID????????????id
     *
     * @param source ??????????????????
     * @param index  ????????????????????????
     * @return
     */
    public static boolean addData(String index, Map<String, Object> source) {
        return addData(index, source, UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
    }

    /**
     * ?????????????????????ID?????????????????????1s??????
     *
     * @param source ??????????????????
     * @param index  ????????????????????????
     * @param id     ??????ID
     * @return
     */
    public static boolean addData(String index, Map<String, Object> source, String id) {
        return addData(index, source, id, WriteRequest.RefreshPolicy.NONE);
    }

    /**
     * ?????????????????????????????????
     * ????????????????????????????????????
     *
     * @param index
     * @param source
     * @param id
     * @param refreshPolicy ??????
     * @return
     */
    public static boolean addData(String index, Map<String, Object> source, String id, WriteRequest.RefreshPolicy refreshPolicy) {
        try {
            IndexRequest request = new IndexRequest(index);
            request.id(id);
            request.source(source);
            request.setRefreshPolicy(refreshPolicy);
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            if (response.status().getStatus() == REST_STATUS_OK || response.status().getStatus() == REST_STATUS_CREATED) {
                log.debug("addData success response status:{},id:{}", response.status().getStatus(), response.getId());
                return true;
            } else {
                log.error("addData error,status: {}", response.status().getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ?????????????????????
     *
     * @param objectList ?????????????????????
     * @param index      ????????????????????????
     * @return
     */
    public static boolean addBatchData(String index, List<JSONObject> objectList) {
        try {
            BulkRequest request = new BulkRequest();
            for (int i = 0; i < objectList.size(); i++) {
                request.add(new IndexRequest(index).id(objectList.get(i).get("id").toString()).source(objectList.get(i)));
            }
            BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
            if (response.status().getStatus() == REST_STATUS_OK || response.status().getStatus() == REST_STATUS_CREATED) {
                log.debug("addBatchData success,index: {}", index);
                return true;
            } else {
                log.error("addBatchData error,status: {}", response.status().getStatus());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * ??????ID????????????
     *
     * @param index ????????????????????????
     * @param id    ??????ID
     */
    public static boolean deleteDataById(String index, String id) {
        return deleteDataById(index, id, WriteRequest.RefreshPolicy.NONE);
    }

    /**
     * ??????ID?????????????????????????????????
     *
     * @param index ????????????????????????
     * @param id    ??????ID
     */
    public static boolean deleteDataById(String index, String id, WriteRequest.RefreshPolicy refreshPolicy) {
        try {
            DeleteRequest request = new DeleteRequest(index);
            request.id(id);
            request.setRefreshPolicy(refreshPolicy);
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
            if (response.status().getStatus() == REST_STATUS_OK) {
                log.debug("deleteDataById response status:{},id:{}", response.status().getStatus(), response.getId());
                return true;
            } else {
                log.error("deleteDataById error, response status:{},index:{},id:{}", response.status().getStatus(), index, id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ????????????id??????
     *
     * @param index
     * @param idList
     * @return
     */
    public static boolean deleteBatchData(String index, List idList) {
        try {
            BulkRequest request = new BulkRequest();
            idList.forEach(id -> request.add(new DeleteRequest(index, id.toString())));
            BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
            if (response.status().getStatus() == REST_STATUS_OK) {
                log.debug("deleteBatchData response status:{},index:{},idList:{}", response.status().getStatus(), index, idList);
                return true;
            } else {
                log.error("deleteBatchData error, response status:{},index:{},idList:{}", response.status().getStatus(), index, idList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ??????id?????????????????????????????????
     *
     * @param index
     * @param source
     * @param id
     */
    public static boolean updateDataById(String index, Map<String, Object> source, String id) {
        return updateDataById(index, source, id, WriteRequest.RefreshPolicy.NONE);
    }

    /**
     * ??????ID ?????????????????????????????????
     *
     * @param index  ????????????????????????
     * @param source ??????????????????
     * @param id     ??????ID
     * @return
     */
    public static boolean updateDataById(String index, Map<String, Object> source, String id, WriteRequest.RefreshPolicy refreshPolicy) {
        try {
            UpdateRequest request = new UpdateRequest(index, id);
            request.doc(source);
            request.retryOnConflict(3);
            request.setRefreshPolicy(refreshPolicy);//????????????
            UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
            if (response.status().getStatus() == REST_STATUS_OK) {
                log.debug("updateDataById response status:{},id:{}", response.status().getStatus(), response.getId());
                return true;
            } else {
                log.error("updateDataById error, response status:{},id:{}", response.status().getStatus(), response.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ??????ID ?????????????????????????????????
     * ??????version?????????????????????,???????????????????????????????????????????????????
     *
     * @param index  ????????????????????????
     * @param source ??????????????????
     * @param id     ??????ID
     * @return
     */
    public static boolean syncUpdateDataById(String index, Map<String, Object> source, String id) {
        try {
            GetRequest getRequest = new GetRequest(index, id);
            GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);

            UpdateRequest updateRequest = new UpdateRequest(index, id);
            updateRequest.doc(source);
            updateRequest.retryOnConflict(3);
            updateRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);//????????????
            updateRequest.version(response.getVersion());
            UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
            if (updateResponse.status().getStatus() == REST_STATUS_OK) {
                log.debug("syncUpdateDataById response status:{},id:{}", updateResponse.status().getStatus(), response.getId());
                return true;
            } else {
                log.error("syncUpdateDataById error, status:{},id:{}", updateResponse.status().getStatus(), response.getId());
            }
        } catch (VersionConflictEngineException e) {
            log.warn("syncUpdateDataById VersionConflictEngineException id:{}", id);
            syncUpdateDataById(index, source, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ??????ID ?????????????????????,???????????????
     * ??????????????????
     *
     * @param source ??????????????????
     * @param index  ????????????????????????
     * @param id     ??????ID
     * @return
     */
    public static void upsertDataById(String index, Map<String, Object> source, String id) {
        upsertDataById(index, source, id, WriteRequest.RefreshPolicy.NONE);
    }

    /**
     * ??????ID ?????????????????????,????????????????????????????????????
     *
     * @param index
     * @param source
     * @param id
     * @param refreshPolicy
     */
    public static boolean upsertDataById(String index, Map<String, Object> source, String id, WriteRequest.RefreshPolicy refreshPolicy) {
        try {
            UpdateRequest request = new UpdateRequest(index, id);
            request.upsert(source);
            request.retryOnConflict(3);
            request.doc(source);
            request.setRefreshPolicy(refreshPolicy);//????????????
            UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
            if (response.status().getStatus() == REST_STATUS_OK || response.status().getStatus() == REST_STATUS_CREATED) {
                log.debug("upsertDataById response status:{},id:{}", response.status().getStatus(), response.getId());
                return true;
            } else {
                log.debug("upsertDataById error, status:{},id:{}", response.status().getStatus(), response.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ??????ID????????????
     *
     * @param index  ????????????????????????
     * @param id     ??????ID
     * @param fields ???????????????????????????????????????????????????????????????
     * @return
     */
    public static Map<String, Object> searchDataById(String index, String id, List<String> fields) {
        try {
            GetRequest request = new GetRequest(index, id);
            if (null != fields) {
                request.storedFields(fields.toArray(new String[fields.size()]));
            }
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            return response.getSource();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ???????????????????????????ID????????????
     *
     * @param indices ??????????????????
     * @param id      ??????ID
     * @return
     */
    public static Map<String, Object> searchDataByIdMulti(List<String> indices, String id) {
        try {
            MultiGetRequest request = new MultiGetRequest();
            indices.forEach(index -> request.add(new MultiGetRequest.Item(index, id)));
            MultiGetResponse response = client.mget(request, RequestOptions.DEFAULT);
            return response.getResponses()[0].getResponse().getSource();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ??????????????????
     *
     * @param indices
     * @param query
     * @return
     */
    public static long getDataCount(List<String> indices, QueryBuilder query) {
        CountRequest request = new CountRequest(indices.toArray(new String[indices.size()]));
        try {
            request.query(query);
            CountResponse response = client.count(request, RequestOptions.DEFAULT);
            return response.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * ??????????????????,?????????????????????????????????
     * ????????????????????????????????????
     *
     * @param indices   ????????????????????????
     * @param startPage ?????????
     * @param pageSize  ??????????????????
     * @param query     ????????????
     * @param sortField ????????????
     * @return
     */
    public static ElasticsearchPage searchDataPage(List<String> indices, int startPage, int pageSize, QueryBuilder query, String sortField, String sortFieldType, SortOrder sortType) {
        return searchDataPage(indices, startPage, pageSize, query, null, sortField, sortFieldType, sortType, null);
    }

    /**
     * ??????????????????,?????????????????????????????????
     *
     * @param indices        ????????????????????????
     * @param startPage      ?????????
     * @param pageSize       ??????????????????
     * @param query          ????????????
     * @param fields         ????????????????????????List???????????????????????????
     * @param sortField      ????????????
     * @param highlightField ????????????
     * @return
     */
    public static ElasticsearchPage searchDataPage(List<String> indices, int startPage, int pageSize, QueryBuilder query, List<String> fields, String sortField, String sortFieldType, SortOrder sortType, String highlightField) {
        try {
            SearchRequest request = new SearchRequest(indices.toArray(new String[indices.size()]));
//            request.searchType(SearchType.QUERY_THEN_FETCH);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
            searchSourceBuilder.query(query);
            if (CollectionUtils.isNotEmpty(fields)) {
                searchSourceBuilder.fetchSource((String[]) fields.toArray(), null);
            }
            //????????????
            if (StringUtils.isNotEmpty(sortField)) {
                searchSourceBuilder.sort(new FieldSortBuilder(sortField).order(sortType).unmappedType(sortFieldType));
            }
            // ????????????
            searchSourceBuilder.from((startPage - 1) * pageSize).size(pageSize);
            searchSourceBuilder.timeout(new TimeValue(SEARCH_DURATION, TimeUnit.SECONDS));
            request.source(searchSourceBuilder);
            //??????????????? ????????? Elasticsearch head ??? Kibana  ???????????????
            log.debug("searchDataPage request: \n{}", searchSourceBuilder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            long totalHits = response.getHits().getTotalHits().value;
            log.debug("searchDataPage status:{} totalHits [{}],cost time???{}", response.status().getStatus(), totalHits, response.getTook().getMillis());
            if (response.status().getStatus() == REST_STATUS_OK) {
                // ????????????
                List<Map<String, Object>> sourceList = setSearchResponse(response, highlightField);
                return new ElasticsearchPage(startPage, pageSize, (int) totalHits, sourceList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ??????????????????????????????
     *
     * @param index
     * @param startPage
     * @param pageSize
     * @param query
     * @return
     */
    public static ElasticsearchPage searchDataPageDefault(String index, int startPage, int pageSize, QueryBuilder query) {
        try {
            SearchRequest request = new SearchRequest(index);
//            request.searchType(SearchType.QUERY_THEN_FETCH);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
            searchSourceBuilder.query(query);
            // ????????????
            searchSourceBuilder.from((startPage - 1) * pageSize).size(pageSize);
            searchSourceBuilder.timeout(new TimeValue(SEARCH_DURATION, TimeUnit.SECONDS));
            request.source(searchSourceBuilder);
            //??????????????? ????????? Elasticsearch head ??? Kibana  ???????????????
            log.debug("searchDataPageDefault request: \n{}", searchSourceBuilder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            long totalHits = response.getHits().getTotalHits().value;
            log.debug("searchDataPageDefault status:{} totalHits [{}],cost time???{}", response.status().getStatus(), totalHits, response.getTook().getMillis());
            if (response.status().getStatus() == REST_STATUS_OK) {
                // ????????????
                List<Map<String, Object>> sourceList = setSearchResponse(response, null);
                return new ElasticsearchPage(startPage, pageSize, (int) totalHits, sourceList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ??????????????????,??????????????????????????????
     *
     * @param indices        ????????????
     * @param startPage      ?????????
     * @param pageSize       ??????????????????
     * @param query          ????????????
     * @param fields         ???????????????????????????????????????????????????????????????
     * @param sortField      ????????????
     * @param highlightField ????????????
     * @return
     */
    public static ElasticsearchPage searchAggregationDataPage(List<String> indices, int startPage, int pageSize, QueryBuilder query, AggregationBuilder aggregation, List<String> fields, String sortField, String sortFieldType, SortOrder sortType, String highlightField) {
        try {
            SearchRequest request = new SearchRequest(indices.toArray(new String[indices.size()]));
//            request.searchType(SearchType.QUERY_THEN_FETCH);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
            searchSourceBuilder.query(query);
            // ???????????????????????????????????????????????????????????????
            if (CollectionUtils.isNotEmpty(fields)) {
                searchSourceBuilder.fetchSource((String[]) fields.toArray(), null);
            }
            //????????????
            if (StringUtils.isNotEmpty(sortField)) {
                searchSourceBuilder.sort(new FieldSortBuilder(sortField).order(sortType).unmappedType(sortFieldType));
            }
            // ????????????
            searchSourceBuilder.from((startPage - 1) * pageSize).size(pageSize);
            if (Objects.nonNull(aggregation)) {
                searchSourceBuilder.aggregation(aggregation);
            }
            searchSourceBuilder.timeout(new TimeValue(SEARCH_DURATION, TimeUnit.SECONDS));
            //??????????????? ????????? Elasticsearch head ??? Kibana  ???????????????
            log.debug("searchAggregatioDataPage request: \n{}", searchSourceBuilder);
            request.source(searchSourceBuilder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            long totalHits = response.getHits().getTotalHits().value;
            long length = response.getHits().getHits().length;
            log.debug("searchAggregatioDataPage status:{} totalHits [{}],getHits:{},cost time???{}", response.status().getStatus(), totalHits, length, response.getTook().getMillis());
            if (response.status().getStatus() == REST_STATUS_OK) {
                List<Map<String, Object>> sourceList = setSearchResponse(response, highlightField);
                return new ElasticsearchPage(startPage, pageSize, (int) totalHits, sourceList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * ??????group?????????????????? @Depracated  searchByGroup
     *
     * @param groupField
     * @param query
     * @param indices
     * @return Map
     */
    public static Map<String, Long> searchAggregationListData(List<String> indices, QueryBuilder query, String groupField) {
        try {
            SearchRequest request = new SearchRequest(indices.toArray(new String[indices.size()]));
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
            searchSourceBuilder.query(query);
            TermsAggregationBuilder aggs = AggregationBuilders.terms("aggs").field(groupField);
            SumAggregationBuilder sum = AggregationBuilders.sum("count").field("1");
            aggs.subAggregation(sum);
            searchSourceBuilder.aggregation(aggs);
            request.source(searchSourceBuilder);
            Map<String, Long> map = new HashMap<>();
            request.source(searchSourceBuilder);
            log.debug("searchAggregationListData Map request: \n{}", searchSourceBuilder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            long totalHits = response.getHits().getTotalHits().value;
            long length = response.getHits().getHits().length;
            log.debug("searchAggregationListData Map status:{} totalHits [{}],getHits:{},cost time???{}", response.status().getStatus(), totalHits, length, response.getTook().getMillis());
            Aggregation agg = response.getAggregations().get("aggs");
            if (response.status().getStatus() == REST_STATUS_OK) {
                if (agg instanceof ParsedLongTerms) {
                    List<? extends Terms.Bucket> buckets = ((ParsedLongTerms) agg).getBuckets();
                    for (int i = 0; i < buckets.size(); i++) {
                        Terms.Bucket bucket = buckets.get(i);
                        long count = bucket.getDocCount();
                        Object key = bucket.getKey();
                        map.put(key.toString(), count);
                    }
                }
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, Object>> searchAggregationListData(List<String> indices, QueryBuilder query, String groupField, String groupFieldBk) {
        return searchAggregationListData(indices, query, groupField, groupFieldBk, null, null, null, null);
    }

    /**
     * @param indices
     * @param query
     * @param groupField
     * @param groupFieldBk
     * @param fields
     * @param sortField
     * @param sortFieldType
     * @param sortType
     * @return List<Map < String, Object>>
     */
    public static List<Map<String, Object>> searchAggregationListData(List<String> indices, QueryBuilder query, String groupField, String groupFieldBk, List<String> fields, String sortField, String sortFieldType, SortOrder sortType) {
        return searchAggregationListData(indices, query, 1, groupField, groupFieldBk, null, null, null, null);
    }

    /**
     * @param indices
     * @param query
     * @param groupField
     * @param groupFieldBk
     * @param fields
     * @param sortField
     * @param sortFieldType
     * @param sortType
     * @return List<Map < String, Object>>
     */
    public static List<Map<String, Object>> searchAggregationListData(List<String> indices, QueryBuilder query, Integer size, String groupField, String groupFieldBk, List<String> fields, String sortField, String sortFieldType, SortOrder sortType) {
        try {
            SearchRequest request = new SearchRequest(indices.toArray(new String[indices.size()]));
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
            searchSourceBuilder.query(query);
            if (null != size) {
                searchSourceBuilder.size(size);
            } else {
                searchSourceBuilder.size(1);
            }
            searchSourceBuilder.aggregation(AggregationBuilders.terms(groupFieldBk).field(groupField));
            // ???????????????????????????????????????????????????????????????
            if (CollectionUtils.isNotEmpty(fields)) {
                searchSourceBuilder.fetchSource((String[]) fields.toArray(), null);
            }
            //????????????
            if (StringUtils.isNotEmpty(sortField)) {
                searchSourceBuilder.sort(new FieldSortBuilder(sortField).order(sortType).unmappedType(sortFieldType));
            }
            searchSourceBuilder.timeout(new TimeValue(SEARCH_DURATION, TimeUnit.SECONDS));
            //??????????????? ????????? Elasticsearch head ??? Kibana  ???????????????
            log.debug("searchAggregationListData List request: \n{}", searchSourceBuilder);
            request.source(searchSourceBuilder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            long totalHits = response.getHits().getTotalHits().value;
            long length = response.getHits().getHits().length;
            log.debug("searchAggregationListData List status:{} totalHits [{}],getHits:{},cost time???{}", response.status().getStatus(), totalHits, length, response.getTook().getMillis());
            if (response.status().getStatus() == REST_STATUS_OK) {
                List<Map<String, Object>> list = new ArrayList<>();
                List<Aggregation> aggregations = response.getAggregations().asList();
                if (CollectionUtils.isEmpty(aggregations)) return Lists.newArrayList();
                Object object = aggregations.get(0);
                if (object instanceof StringTerms) {
                    StringTerms stringTerms = (StringTerms) object;
                    List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
                    buckets.stream().forEach(e -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put(groupField, e.getKey());
                        map.put(groupFieldBk, e.getDocCount());
                        list.add(map);
                    });
                } else if (object instanceof LongTerms) {
                    LongTerms longTerms = (LongTerms) object;
                    List<LongTerms.Bucket> buckets = longTerms.getBuckets();
                    buckets.stream().forEach(e -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put(groupField, e.getKey());
                        map.put(groupFieldBk, e.getDocCount());
                        list.add(map);
                    });
                }
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ??????????????????????????????1??????1??????
     *
     * @param indices
     * @param query
     * @param groupField
     * @return List<Map < String, Object>>
     */
    public static List<Map<String, Object>> searchAggregationDateIntervalListData(List<String> indices, QueryBuilder query, String groupField, DateHistogramInterval dateHistogramInterval, BucketOrder bucketOrder, boolean filteCount) {
        try {
            SearchRequest request = new SearchRequest(indices.toArray(new String[indices.size()]));
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
            searchSourceBuilder.query(query);
            searchSourceBuilder.size(0);
            DateHistogramAggregationBuilder aggregationBuilder = AggregationBuilders.dateHistogram("timeAgg").field(groupField).calendarInterval(dateHistogramInterval).order(bucketOrder);
            searchSourceBuilder.aggregation(aggregationBuilder);
            searchSourceBuilder.timeout(new TimeValue(SEARCH_DURATION, TimeUnit.SECONDS));
            //??????????????? ????????? Elasticsearch head ??? Kibana  ???????????????
            log.debug("searchAggregationListData List request: \n{}", searchSourceBuilder);
            request.source(searchSourceBuilder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            long totalHits = response.getHits().getTotalHits().value;
            long length = response.getHits().getHits().length;
            log.debug("searchAggregationListData List status:{} totalHits [{}],getHits:{},cost time???{}", response.status().getStatus(), totalHits, length, response.getTook().getMillis());
            if (response.status().getStatus() == REST_STATUS_OK) {
                List<Map<String, Object>> list = new ArrayList<>();
                List<Aggregation> aggregations = response.getAggregations().asList();
                if (CollectionUtils.isEmpty(aggregations)) return Lists.newArrayList();
                Object object = aggregations.get(0);
                if (object instanceof ParsedDateHistogram) {
                    ParsedDateHistogram dateHistogram = (ParsedDateHistogram) object;
                    List<? extends Histogram.Bucket> buckets = dateHistogram.getBuckets();
                    buckets.stream().forEach(e -> {
                        Map<String, Object> map = new HashMap<>();
                        if (filteCount) {
                            if (e.getDocCount() > 0) {
                                map.put(groupField, e.getKey());
                                map.put("count", e.getDocCount());
                                list.add(map);
                            }
                        } else {
                            map.put(groupField, e.getKey());
                            map.put("count", e.getDocCount());
                            list.add(map);
                        }
                    });
                }
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ??????????????????????????????1??????1??????
     *
     * @param indices
     * @param query
     * @param groupField
     * @return List<Map < String, Object>>
     */
    public static List<Map<String, Object>> searchAggregationDateIntervalListData(List<String> indices, QueryBuilder query, String groupField, DateHistogramInterval dateHistogramInterval, String offset, BucketOrder bucketOrder, boolean filteCount) {
        try {
            SearchRequest request = new SearchRequest(indices.toArray(new String[indices.size()]));
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
            searchSourceBuilder.query(query);
            searchSourceBuilder.size(0);
            DateHistogramAggregationBuilder aggregationBuilder = AggregationBuilders.dateHistogram("timeAgg").field(groupField).calendarInterval(dateHistogramInterval).order(bucketOrder);
            if (StringUtils.isNotEmpty(offset)) {
                aggregationBuilder.offset(offset);
            }
            searchSourceBuilder.aggregation(aggregationBuilder);
            searchSourceBuilder.timeout(new TimeValue(SEARCH_DURATION, TimeUnit.SECONDS));
            //??????????????? ????????? Elasticsearch head ??? Kibana  ???????????????
            log.debug("searchAggregationListData List request: \n{}", searchSourceBuilder);
            request.source(searchSourceBuilder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            long totalHits = response.getHits().getTotalHits().value;
            long length = response.getHits().getHits().length;
            log.debug("searchAggregationListData List status:{} totalHits [{}],getHits:{},cost time???{}", response.status().getStatus(), totalHits, length, response.getTook().getMillis());
            if (response.status().getStatus() == REST_STATUS_OK) {
                List<Map<String, Object>> list = new ArrayList<>();
                List<Aggregation> aggregations = response.getAggregations().asList();
                if (CollectionUtils.isEmpty(aggregations)) return Lists.newArrayList();
                Object object = aggregations.get(0);
                if (object instanceof ParsedDateHistogram) {
                    ParsedDateHistogram dateHistogram = (ParsedDateHistogram) object;
                    List<? extends Histogram.Bucket> buckets = dateHistogram.getBuckets();
                    buckets.stream().forEach(e -> {
                        Map<String, Object> map = new HashMap<>();
                        if (filteCount) {
                            if (e.getDocCount() > 0) {
                                map.put(groupField, e.getKey());
                                map.put("count", e.getDocCount());
                                list.add(map);
                            }
                        } else {
                            map.put(groupField, e.getKey());
                            map.put("count", e.getDocCount());
                            list.add(map);
                        }
                    });
                }
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ??????List,????????????size?????????null????????????
     *
     * @param query     ????????????
     * @param size      ??????????????????
     * @param fields    ???????????????????????????????????????????????????????????????
     * @param sortField ????????????
     * @return
     */
    public static List<Map<String, Object>> searchListData(List<String> indices, QueryBuilder query, Integer size,
                                                           List<String> fields, String sortField) {
        return searchListData(indices, query, size, fields, sortField, "long", SortOrder.DESC);

    }

    /**
     * ??????List,????????????size?????????null?????????????????????????????????
     *
     * @param indices
     * @param query
     * @param size
     * @param fields
     * @param sortField
     * @param sortFieldType
     * @param sortOrder
     * @return
     */
    public static List<Map<String, Object>> searchListData(List<String> indices, QueryBuilder query, Integer size,
                                                           List<String> fields, String sortField, String sortFieldType, SortOrder sortOrder) {
        try {
            SearchRequest request = new SearchRequest(indices.toArray(new String[indices.size()]));
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
            searchSourceBuilder.query(query);
            if (null != size) {
                searchSourceBuilder.size(size);
            } else {
                searchSourceBuilder.size(DEFAULT_MAX_RESULT_SIZE);
            }
            if (CollectionUtils.isNotEmpty(fields)) {
                searchSourceBuilder.fetchSource((String[]) fields.toArray(), null);
            }
            searchSourceBuilder.fetchSource(true);
            searchSourceBuilder.timeout(new TimeValue(SEARCH_DURATION, TimeUnit.SECONDS));
            if (StringUtils.isNotEmpty(sortField)) {
                searchSourceBuilder.sort(SortBuilders.fieldSort(sortField).order(sortOrder).unmappedType(sortFieldType));
            }
            request.source(searchSourceBuilder);
            log.debug("searchListData request: \n{}", searchSourceBuilder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            long totalHits = response.getHits().getTotalHits().value;
            long length = response.getHits().getHits().length;
            log.debug("searchListData status:{} totalHits [{}],getHits:{},cost time???{}", response.status().getStatus(), totalHits, length, response.getTook().getMillis());
            if (response.status().getStatus() == REST_STATUS_OK) {
                return setSearchResponse(response, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Lists.newArrayList();
    }

    /**
     * ????????????????????????
     *
     * @param indices
     * @param query
     */
    public static void updateByQuery(List<String> indices, QueryBuilder query) {
        try {
            UpdateByQueryRequest request = new UpdateByQueryRequest(indices.toArray(new String[indices.size()]));
            request.setQuery(query);
            request.setConflicts("proceed");//???????????????????????????
            BulkByScrollResponse response = client.updateByQuery(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("updateByQuery Exception???{}", e);
        }
    }

    /**
     * ????????????????????????
     *
     * @param indices
     * @param query
     */
    public static void deleteByQuery(List<String> indices, QueryBuilder query) {
        try {
            DeleteByQueryRequest request = new DeleteByQueryRequest(indices.toArray(new String[indices.size()]));
            request.setQuery(query);
            request.setConflicts("proceed");//???????????????????????????
            BulkByScrollResponse response = client.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("deleteByQuery Exception???{}", e);
        }
    }

    /**
     * ????????????
     *
     * @param sourceIndices
     * @param destIndex
     */
    public static void reindex(List<String> sourceIndices, String destIndex) {
        try {
            ReindexRequest request = new ReindexRequest();
            request.setSourceIndices(sourceIndices.toArray(new String[sourceIndices.size()]));
            request.setDestIndex(destIndex);
            request.setConflicts("proceed");//???????????????????????????
            request.setRefresh(true);
            //??????reindex
            //            BulkByScrollResponse response = client.reindex(request, RequestOptions.DEFAULT);
            //????????????reindex???????????????id??????????????????
            TaskSubmissionResponse reindexSubmission = client.submitReindexTask(request, RequestOptions.DEFAULT);
            String taskId = reindexSubmission.getTask();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("reindex Exception???{}", e);
        }
    }

    /**
     * ??????????????? ????????????
     *
     * @param searchResponse
     * @param highlightField
     */
    private static List<Map<String, Object>> setSearchResponse(SearchResponse searchResponse, String highlightField) {
        List<Map<String, Object>> sourceList = new ArrayList<Map<String, Object>>();
        StringBuffer stringBuffer = new StringBuffer();
        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            searchHit.getSourceAsMap().put("id", searchHit.getId());
            if (StringUtils.isNotEmpty(highlightField)) {
                Text[] text = searchHit.getHighlightFields().get(highlightField).getFragments();
                if (text != null) {
                    for (Text str : text) {
                        stringBuffer.append(str.string());
                    }
                    //?????? ???????????????????????? ???????????????
                    searchHit.getSourceAsMap().put(highlightField, stringBuffer.toString());
                }
            }
            sourceList.add(searchHit.getSourceAsMap());
        }
        return sourceList;
    }


}
