package com.github.mirs.banxiaoxiao.framework.elasticsearch.constans;

/**
 * @类名: ElasticsearchConstants
 * @描述:
 * @作者: bc
 * @日期: 2021/07/19 11:34
 */
public class ElasticsearchConstants {

    public static final int NUMBER_OF_SHARDS = 3;

    public static final int NUMBER__OF_REPLICAS = 1;

    public static final long MAX_RESULT_WINDOW = 10000000;

    public static final Integer DEFAULT_RESULT_SIZE = 27;
    public static final Integer DEFAULT_MAX_RESULT_SIZE = 10000;

    public static final int REST_STATUS_OK = 200;
    public static final int REST_STATUS_CREATED = 201;

    //查询超时TimeValue
    public static final int SEARCH_DURATION = 60;

}
