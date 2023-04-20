package com.mapping.db.builder;
/**
 * 类描述
 * 创建人 wanggq
 * 创建时间 2015/6/16 18:02.
 */

import android.text.TextUtils;

import com.mapping.db.exception.DBException;

import java.util.regex.Pattern;

public class QuerySqlBuilder extends SqlBuilder {

    @Override
    public String buildSql() throws DBException, IllegalArgumentException,
            IllegalAccessException {
        // TODO Auto-generated method stub
        return buildQueryString();
    }

    /**
     * 创建查询的字段
     */
    public String buildQueryString() {
        if (TextUtils.isEmpty(groupBy) && !TextUtils.isEmpty(having)) {
            throw new IllegalArgumentException(
                    "HAVING clauses are only permitted when using a groupBy clause");
        }

        StringBuilder query = new StringBuilder(120);
        query.append("SELECT ");
        if (distinct) {
            query.append(" DISTINCT ");
        }
        query.append(" * ");
        query.append("FROM ");
        query.append(tableName);
        appendClause(query, " WHERE ", where);
        appendClause(query, " GROUP BY ", groupBy);
        appendClause(query, " HAVING ", having);
        appendClause(query, " ORDER BY ", orderBy);
        if (limit > 0) {
            appendClause(query, " LIMIT ", limit + "");
        }
//        Log.w("sql:"+query.toString());
        return query.toString();
    }


}
