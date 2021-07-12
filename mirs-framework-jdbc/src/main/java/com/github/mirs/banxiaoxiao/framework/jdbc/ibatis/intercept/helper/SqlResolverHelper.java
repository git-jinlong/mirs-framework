package com.github.mirs.banxiaoxiao.framework.jdbc.ibatis.intercept.helper;

import com.github.mirs.banxiaoxiao.framework.core.spring.SpringContextHolder;
import com.github.mirs.banxiaoxiao.framework.jdbc.ibatis.intercept.BeeJdbcIbatisInterceptException;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.Objects;
import java.util.Set;

/**
 * @类名: SqlResolver
 * @描述: sql处理工具
 * @作者: liudf
 * @日期: 2019/5/16 15:52
 */
public class SqlResolverHelper {

    //主要校验字段
    private static final String LEADER_ORG_ID = "leader_org_id";

    //
    private static final String WHERE = "where";

    private static final String IDIN = "idin";

    private static final String IDDY = "id=";

    private static final String EXCEPTION_MSG = "can not find column `leader_org_id` ,please check your sql : ";

    private static Set<String> blackTables = Sets.newHashSet(
            "account_library", "account_user", "org_extra", "role",
            "live_launch",
            "vag_camera", "vag_camera_group", "vag_snapshot_settings",
            "tag", "tag_analyze", "tag_analyze_snap", "tag_group",
            "polymer_group", "polymer_task", "polymer_group_update_statics", "polymer_search_contrast_box", "polymer_group_compare_config", "polymer_channel_snapshot", "polymer_channel", "polymer_business_task", "polymer_group_tag", "polymer_search_group_result", "polymer_group_item",
            "library",
            "live_history_snapshot",
            "live_static_search_task");

    private static String tableName;

    /**
     * 截取字符串str中指定字符 strStart、strEnd之间的字符串
     *
     * @param str
     * @param strStart
     * @param strEnd
     * @return
     */
    public static String subString(String str, String strStart, String strEnd) {
        int strStartIndex = str.indexOf(strStart);
        int strEndIndex = str.indexOf(strEnd);
        if (strStartIndex < 0) {
            return null;
        }
        if (strEndIndex < 0) {
            return null;
        }
        String result = str.substring(strStartIndex, strEndIndex).substring(strStart.length());
        return result;
    }

    public static int getSubCount(String str, String key) {
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(key, index)) != -1) {
            index = index + key.length();
            count++;
        }
        return count;
    }

    public static void verify(String sql, SqlCommandType sqlCommandType) {
        if (Objects.isNull(blackTables)) {
            if (Objects.isNull(tableName)) {
                tableName = SpringContextHolder.get().getEnvironment().getProperty("bee.jdbc.mybatis.intercept.org.blacks");
                if (StringUtils.isNotBlank(tableName)) {
                    String[] tabless = tableName.split(",");
                    blackTables = Sets.newHashSet(tabless);
                }
            }
        }

        if (CollectionUtils.isEmpty(blackTables)) {
            return;
        }

        boolean isBlack = false;
        for (String ss : blackTables) {
            if (sql.contains(ss) && !sql.contains(ss + "_") && !sql.contains("_" + ss)) {
                isBlack = true;
                break;
            }
        }

        if (!isBlack) {
            return;
        }

        if (sqlCommandType == SqlCommandType.INSERT || sqlCommandType == SqlCommandType.UPDATE) {
            if (!sql.contains(LEADER_ORG_ID)) {
                throw new BeeJdbcIbatisInterceptException(EXCEPTION_MSG + sql);
            }
        } else if (sqlCommandType == SqlCommandType.SELECT) {
            String[] arrayFrom = sql.toLowerCase().split(WHERE);
            if (arrayFrom[1].replaceAll(" ", "").contains(IDIN) || arrayFrom[1].replaceAll(" ", "").contains(IDDY)) {
                return;
            }
            if (!sql.contains(LEADER_ORG_ID) || arrayFrom.length == 1 || arrayFrom.length > 2 || !arrayFrom[1].contains(LEADER_ORG_ID)) {
                throw new BeeJdbcIbatisInterceptException(EXCEPTION_MSG + sql);
            }
        }

        return;
    }
}
