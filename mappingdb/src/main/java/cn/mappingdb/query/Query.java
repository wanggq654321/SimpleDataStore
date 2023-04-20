/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.mappingdb.query;

import java.util.Date;
import java.util.List;

/**
 * A repeatable query returning entities.
 * <p>
 * param <T> The entity class the query will return results for.
 *
 * @author Markus
 */
public class Query extends AbstractQuery {

    protected int limitPosition;
    protected int offsetPosition;

    private final static class QueryData extends AbstractQueryData {
        private final int limitPosition;
        private final int offsetPosition;

        QueryData(String sql, String[] initialValues, int limitPosition, int offsetPosition) {
            super(sql, initialValues);
            this.limitPosition = limitPosition;
            this.offsetPosition = offsetPosition;
        }

        @Override
        protected Query createQuery() {
            return new Query(this, sql, initialValues.clone(), limitPosition, offsetPosition);
        }

    }

    /**
     * For internal use by greenDAO only.
     */
    public static Query internalCreate(String sql, Object[] initialValues) {
        return create(sql, initialValues, -1, -1);
    }

    static Query create(String sql, Object[] initialValues, int limitPosition,
                        int offsetPosition) {
        QueryData queryData = new QueryData(sql, toStringArray(initialValues), limitPosition,
                offsetPosition);
        return (Query) queryData.forCurrentThread();
    }

    private final QueryData queryData;


    private Query(QueryData queryData, String sql, String[] initialValues, int limitPosition,
                  int offsetPosition) {
        super(sql, initialValues);
        this.limitPosition = limitPosition;
        this.offsetPosition = offsetPosition;
        this.queryData = queryData;
    }

//    /**
//     * Note: all parameters are reset to their initial values specified in {@link QueryBuilder}.
//     */
//    public Query forCurrentThread() {
//        return queryData.forCurrentThread(this);
//    }

//    /**
//     * Executes the query and returns the result as a list containing all entities loaded into memory.
//     */
//    public List list() {
//        checkThread();
//        Cursor cursor = dao.getDatabase().rawQuery(sql, parameters);
//        return daoAccess.loadAllAndCloseCursor(cursor);
//    }


    @Override
    public Query setParameter(int index, Object parameter) {
        return (Query) super.setParameter(index, parameter);
    }

    @Override
    public Query setParameter(int index, Date parameter) {
        return (Query) super.setParameter(index, parameter);
    }

    @Override
    public Query setParameter(int index, Boolean parameter) {
        return (Query) super.setParameter(index, parameter);
    }


}
