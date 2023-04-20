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

import cn.mappingdb.exception.DaoException;

/**
 * A repeatable query returning entities.
 * <p>
 * param The entity class the query will return results for.
 *
 * @author wanggq
 */
// TODO support long, double and other types, not just Strings, for parameters
// TODO Make parameters setable by Property (if unique in parameters)
// TODO Make query compilable
abstract class AbstractQuery {

    protected final String sql;
    protected final String[] parameters;
    protected final Thread ownerThread;

    protected static String[] toStringArray(Object[] values) {
        int length = values.length;
        String[] strings = new String[length];
        for (int i = 0; i < length; i++) {
            Object object = values[i];
            if (object != null) {
                strings[i] = object.toString();
            } else {
                strings[i] = null;
            }
        }
        return strings;
    }

    protected AbstractQuery(String sql, String[] parameters) {
        this.sql = sql;
        this.parameters = parameters;
        ownerThread = Thread.currentThread();
    }

    /**
     * Sets the parameter (0 based) using the position in which it was added during building the query.
     */
    public AbstractQuery setParameter(int index, Object parameter) {
        checkThread();
        if (parameter != null) {
            parameters[index] = parameter.toString();
        } else {
            parameters[index] = null;
        }
        return this;
    }

    /**
     * @see #setParameter(int, Object)
     */
    public AbstractQuery setParameter(int index, Date parameter) {
        Long converted = parameter != null ? parameter.getTime() : null;
        return setParameter(index, converted);
    }

    /**
     * @see #setParameter(int, Object)
     */
    public AbstractQuery setParameter(int index, Boolean parameter) {
        Integer converted = parameter != null ? (parameter ? 1 : 0) : null;
        return setParameter(index, converted);
    }

    protected void checkThread() {
        if (Thread.currentThread() != ownerThread) {
            throw new DaoException(
                    "Method may be called only in owner thread, use forCurrentThread to get an instance for this thread");
        }
    }

    public String getSql() {
        return sql;
    }

    public String[] getParameters() {
        return parameters;
    }

}
