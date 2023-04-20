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

/**
 * A repeatable query for deleting entities.<br/>
 * New API note: this is more likely to change.
 *
 * param <T> The entity class the query will delete from.
 * @author Markus
 */
public class DeleteQuery extends AbstractQuery {


    private final static class QueryData extends AbstractQueryData {

        private QueryData(String sql, String[] initialValues) {
            super(sql, initialValues);
        }

        @Override
        protected DeleteQuery createQuery() {
            return new DeleteQuery(this, sql, initialValues.clone());
        }
    }

    static DeleteQuery create(String sql, Object[] initialValues) {
        QueryData queryData = new QueryData(sql, toStringArray(initialValues));
        return (DeleteQuery) queryData.forCurrentThread();
    }

    private final QueryData queryData;

    private DeleteQuery(QueryData queryData, String sql, String[] initialValues) {
        super(sql, initialValues);
        this.queryData = queryData;
    }

    public DeleteQuery forCurrentThread() {
        return (DeleteQuery) queryData.forCurrentThread(this);
    }

//    /**
//     * Deletes all matching entities without detaching them from the identity scope (aka session/cache). Note that this
//     * method may lead to stale entity objects in the session cache. Stale entities may be returned when loaded by
//     * their
//     * primary key, but not using queries.
//     */
//    public void executeDeleteWithoutDetachingEntities() {
//        checkThread();
//        Database db = dao.getDatabase();
//        if (db.isDbLockedByCurrentThread()) {
//            dao.getDatabase().execSQL(sql, parameters);
//        } else {
//            // Do TX to acquire a connection before locking this to avoid deadlocks
//            // Locking order as described in AbstractDao
//            db.beginTransaction();
//            try {
//                dao.getDatabase().execSQL(sql, parameters);
//                db.setTransactionSuccessful();
//            } finally {
//                db.endTransaction();
//            }
//        }
//    }

    // copy setParameter methods to allow easy chaining
    @Override
    public DeleteQuery setParameter(int index, Object parameter) {
        return (DeleteQuery) super.setParameter(index, parameter);
    }

    @Override
    public DeleteQuery setParameter(int index, Date parameter) {
        return (DeleteQuery) super.setParameter(index, parameter);
    }

    @Override
    public DeleteQuery setParameter(int index, Boolean parameter) {
        return (DeleteQuery) super.setParameter(index, parameter);
    }

}
