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

import java.util.ArrayList;
import java.util.List;

import cn.mappingdb.bean.Property;
import cn.mappingdb.cache.EntifyInfoCacheFactory;
import cn.mappingdb.exception.DaoException;
import cn.mappingdb.exception.StaleDataException;
import cn.mappingdb.internal.EntifyConfig;
import cn.mappingdb.internal.SqlUtils;
import cn.mappingdb.log.DaoLog;

/**
 * 使用约束和参数而不使用SQL构建自定义实体查询（QueryBuilder为您创建SQL）。到
 * 获取一个QueryBuilder，使用｛@link AbstractDao#QueryBuilder（）｝或｛@linkAbstractDooSession#queryBuild（Class）｝。
 * 实体财产由生成的DAO的“财产”内部类中的字段引用。这种方法
 * 允许在编译时进行检查，并防止在编译时出现打字错误<br/＞
 * <br/>
 * 示例：按姓氏顺序查询所有名为“Joe”的用户。（类财产是一个内部
 * 类，并且应该在之前导入。）<br/＞
 * ＜代码＞
 * List<User>joes=dao.queryBuilder（）.where（财产.FirstName.eq（“Joe”））.orderAsc（财产.LastName）.List（）；
 * </代码>
 *
 * @param＜T＞要为其创建查询的实体类。
 */
public class QueryBuilder {

    /**
     * Set to true to debug the SQL.
     */
    public static boolean LOG_SQL;

    /**
     * Set to see the given values.
     */
    public static boolean LOG_VALUES;
    private WhereCollector whereCollector;

    private StringBuilder orderBuilder;

    private final List<Object> values;
    //    private final List<Join<T, ?>> joins;
//    private final AbstractDao<T, ?> dao;
    private final String tablePrefix;

    private Integer limit;
    private Integer offset;
    private boolean distinct;

    private Class<?> table;
    private EntifyConfig config = null;

    /**
     * For internal use by greenDAO only.
     */
    public static QueryBuilder initBuild(Class<?> table) {
        return new QueryBuilder(table);
    }

    public static QueryBuilder initCreate(Class<?> table) {
        return new QueryBuilder(table);
    }

    public Property property(String columnName) {
        Property p = config.propertyHashMap.get(columnName);
        if (p == null) {
            throw new StaleDataException(config.tablename + " 此entify 没有 " + columnName + "属性字段，请检查");
        }
        return p;
    }

    public Property column(String columnName) {
        return property(columnName);
    }

    public Class<?> getTable() {
        return table;
    }

    protected QueryBuilder(Class<?> table) {
        this(table, "T");
    }

    protected QueryBuilder(Class<?> table, String tablePrefix) {
        this.table = table;
        this.tablePrefix = tablePrefix;
        values = new ArrayList<>();
        // joins = new ArrayList<Join<T, ?>>();
        whereCollector = new WhereCollector(tablePrefix);
        config = EntifyInfoCacheFactory.getInstance().getTableInfoEntity(table);
    }

    private void checkOrderBuilder() {
        if (orderBuilder == null) {
            orderBuilder = new StringBuilder();
        } else if (orderBuilder.length() > 0) {
            orderBuilder.append(",");
        }
    }

    /**
     * Use a SELECT DISTINCT to avoid duplicate entities returned, e.g. when doing joins.
     */
    public QueryBuilder distinct() {
        distinct = true;
        return this;
    }

    /**
     * Adds the given conditions to the where clause using an logical AND. To create new conditions, use the properties
     * given in the generated dao classes.
     */
    public QueryBuilder where(WhereCondition cond, WhereCondition... condMore) {
        whereCollector.add(cond, condMore);
        return this;
    }

    /**
     * Adds the given conditions to the where clause using an logical OR. To create new conditions, use the properties
     * given in the generated dao classes.
     */
    public QueryBuilder whereOr(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        whereCollector.add(or(cond1, cond2, condMore));
        return this;
    }

    /**
     * Creates a WhereCondition by combining the given conditions using OR. The returned WhereCondition must be used
     * inside {@link #where(WhereCondition, WhereCondition...)} or
     * {@link #whereOr(WhereCondition, WhereCondition, WhereCondition...)}.
     */
    public WhereCondition or(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        return whereCollector.combineWhereConditions(" OR ", cond1, cond2, condMore);
    }

    /**
     * Creates a WhereCondition by combining the given conditions using AND. The returned WhereCondition must be used
     * inside {@link #where(WhereCondition, WhereCondition...)} or
     * {@link #whereOr(WhereCondition, WhereCondition, WhereCondition...)}.
     */
    public WhereCondition and(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        return whereCollector.combineWhereConditions(" AND ", cond1, cond2, condMore);
    }

//    /**
//     * Expands the query to another entity type by using a JOIN. The primary key property of the primary entity for
//     * this QueryBuilder is used to match the given destinationProperty.
//     */
//    public <J> Join<T, J> join(Class<J> destinationEntityClass, Property destinationProperty) {
//        return join(dao.getPkProperty(), destinationEntityClass, destinationProperty);
//    }
//
//    /**
//     * Expands the query to another entity type by using a JOIN. The given sourceProperty is used to match the primary
//     * key property of the given destinationEntity.
//     */
//    public <J> Join<T, J> join(Property sourceProperty, Class<J> destinationEntityClass) {
//        AbstractDao<J, ?> destinationDao = (AbstractDao<J, ?>) dao.getSession().getDao(destinationEntityClass);
//        Property destinationProperty = destinationDao.getPkProperty();
//        return addJoin(tablePrefix, sourceProperty, destinationDao, destinationProperty);
//    }
//
//    /**
//     * Expands the query to another entity type by using a JOIN. The given sourceProperty is used to match the given
//     * destinationProperty of the given destinationEntity.
//     */
//    public <J> Join<T, J> join(Property sourceProperty, Class<J> destinationEntityClass, Property destinationProperty) {
//        AbstractDao<J, ?> destinationDao = (AbstractDao<J, ?>) dao.getSession().getDao(destinationEntityClass);
//        return addJoin(tablePrefix, sourceProperty, destinationDao, destinationProperty);
//    }
//
//    /**
//     * Expands the query to another entity type by using a JOIN. The given sourceJoin's property is used to match the
//     * given destinationProperty of the given destinationEntity. Note that destination entity of the given join is used
//     * as the source for the new join to add. In this way, it is possible to compose complex "join of joins" across
//     * several entities if required.
//     */
//    public <J> Join<T, J> join(Join<?, T> sourceJoin, Property sourceProperty, Class<J> destinationEntityClass,
//                               Property destinationProperty) {
//        AbstractDao<J, ?> destinationDao = (AbstractDao<J, ?>) dao.getSession().getDao(destinationEntityClass);
//        return addJoin(sourceJoin.tablePrefix, sourceProperty, destinationDao, destinationProperty);
//    }
//
//    private <J> Join<T, J> addJoin(String sourceTablePrefix, Property sourceProperty, AbstractDao<J, ?> destinationDao,
//                                   Property destinationProperty) {
//        String joinTablePrefix = "J" + (joins.size() + 1);
//        Join<T, J> join = new Join<T, J>(sourceTablePrefix, sourceProperty, destinationDao, destinationProperty,
//                joinTablePrefix);
//        joins.add(join);
//        return join;
//    }

    /**
     * Adds the given properties to the ORDER BY section using ascending order.
     */
    public QueryBuilder orderAsc(Property... properties) {
        orderAscOrDesc(" ASC", properties);
        return this;
    }

    /**
     * Adds the given properties to the ORDER BY section using descending order.
     */
    public QueryBuilder orderDesc(Property... properties) {
        orderAscOrDesc(" DESC", properties);
        return this;
    }

    private void orderAscOrDesc(String ascOrDescWithLeadingSpace, Property... properties) {
        for (Property property : properties) {
            checkOrderBuilder();
            append(orderBuilder, property);
//            if (String.class.equals(property.type) && stringOrderCollation != null) {
//                orderBuilder.append(stringOrderCollation);
//            }
            orderBuilder.append(ascOrDescWithLeadingSpace);
        }
    }

    /**
     * Adds the given properties to the ORDER BY section using the given custom order.
     */
    public QueryBuilder orderCustom(Property property, String customOrderForProperty) {
        checkOrderBuilder();
        append(orderBuilder, property).append(' ');
        orderBuilder.append(customOrderForProperty);
        return this;
    }

    /**
     * Adds the given raw SQL string to the ORDER BY section. Do not use this for standard properties: orderAsc and
     * orderDesc are preferred.
     */
    public QueryBuilder orderRaw(String rawOrder) {
        checkOrderBuilder();
        orderBuilder.append(rawOrder);
        return this;
    }

    protected StringBuilder append(StringBuilder builder, Property property) {
        whereCollector.checkProperty(property);
        builder.append(tablePrefix).append('.').append('\'').append(property.columnName).append('\'');
        return builder;
    }


    /**
     * Limits the number of results returned by queries.
     */
    public QueryBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Sets the offset for query results in combination with {@link #limit(int)}. The first {@code offset} results are
     * skipped and the total number of results will be limited by {@code limit}. You cannot use offset without limit.
     */
    public QueryBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    /**
     * Builds a reusable query object (Query objects can be executed more efficiently than creating a QueryBuilder for
     * each execution.
     */
    public Query build() {
        StringBuilder builder = createSelectBuilder(config);
        int limitPosition = checkAddLimit(builder);
        int offsetPosition = checkAddOffset(builder);

        String sql = builder.toString();
        checkLog(sql);

        // return Query.create(dao, sql, values.toArray(), limitPosition, offsetPosition);
        return Query.create(sql, values.toArray(), limitPosition, offsetPosition);
    }


//    /**
//     * Builds a reusable query object for low level android.database.Cursor access.
//     * (Query objects can be executed more efficiently than creating a QueryBuilder for each execution.
//     */
//    public CursorQuery buildCursor() {
//        StringBuilder builder = createSelectBuilder();
//        int limitPosition = checkAddLimit(builder);
//        int offsetPosition = checkAddOffset(builder);
//
//        String sql = builder.toString();
//        checkLog(sql);
//
//        return CursorQuery.create(dao, sql, values.toArray(), limitPosition, offsetPosition);
//    }

    private StringBuilder createSelectBuilder(EntifyConfig daoConfig) {
//        String select = SqlUtils.createSqlSelect(dao.getTablename(), tablePrefix, dao.getAllColumns(), distinct);
        String select = SqlUtils.createSqlSelect(daoConfig.tablename, tablePrefix, daoConfig.allColumns, distinct);
        StringBuilder builder = new StringBuilder(select);

        appendJoinsAndWheres(builder, tablePrefix);

        if (orderBuilder != null && orderBuilder.length() > 0) {
            builder.append(" ORDER BY ").append(orderBuilder);
        }
        return builder;
    }

    private int checkAddLimit(StringBuilder builder) {
        int limitPosition = -1;
        if (limit != null) {
            builder.append(" LIMIT ?");
            values.add(limit);
            limitPosition = values.size() - 1;
        }
        return limitPosition;
    }

    private int checkAddOffset(StringBuilder builder) {
        int offsetPosition = -1;
        if (offset != null) {
            if (limit == null) {
                throw new IllegalStateException("Offset cannot be set without limit");
            }
            builder.append(" OFFSET ?");
            values.add(offset);
            offsetPosition = values.size() - 1;
        }
        return offsetPosition;
    }

    /**
     * Builds a reusable query object for deletion (Query objects can be executed more efficiently than creating a
     * QueryBuilder for each execution.
     */
    public DeleteQuery buildDelete() {
//        if (!joins.isEmpty()) {
//            throw new DaoException("JOINs are not supported for DELETE queries");
//        }
        String tablename = config.tablename;
        String baseSql = SqlUtils.createSqlDelete(tablename, null);
        StringBuilder builder = new StringBuilder(baseSql);

        // tablePrefix gets replaced by table name below. Don't use tableName here because it causes trouble when
        // table name ends with tablePrefix.
        appendJoinsAndWheres(builder, tablePrefix);

        String sql = builder.toString();
        // Remove table aliases, not supported for DELETE queries.
        // TODO(?): don't create table aliases in the first place.
        sql = sql.replace(tablePrefix + ".\"", '"' + tablename + "\".\"");
        checkLog(sql);

        return DeleteQuery.create(sql, values.toArray());
    }
//
//    /**
//     * Builds a reusable query object for counting rows (Query objects can be executed more efficiently than creating a
//     * QueryBuilder for each execution.
//     */
//    public CountQuery buildCount() {
//        String tablename = dao.getTablename();
//        String baseSql = SqlUtils.createSqlSelectCountStar(tablename, tablePrefix);
//        StringBuilder builder = new StringBuilder(baseSql);
//        appendJoinsAndWheres(builder, tablePrefix);
//
//        String sql = builder.toString();
//        checkLog(sql);
//
//        return CountQuery.create(dao, sql, values.toArray());
//    }

    private void checkLog(String sql) {
        if (LOG_SQL) {
            DaoLog.d("Built SQL for query: " + sql);
        }
        if (LOG_VALUES) {
            DaoLog.d("Values for query: " + values);
        }
    }

    private void appendJoinsAndWheres(StringBuilder builder, String tablePrefixOrNull) {
        values.clear();
//        for (Join<T, ?> join : joins) {
//            builder.append(" JOIN ");
//            builder.append('"').append(join.daoDestination.getTablename()).append('"').append(' ');
//            builder.append(join.tablePrefix).append(" ON ");
//            SqlUtils.appendProperty(builder, join.sourceTablePrefix, join.joinPropertySource).append('=');
//            SqlUtils.appendProperty(builder, join.tablePrefix, join.joinPropertyDestination);
//        }
        boolean whereAppended = !whereCollector.isEmpty();
        if (whereAppended) {
            builder.append(" WHERE ");
            whereCollector.appendWhereClause(builder, tablePrefixOrNull, values);
        }
//        for (Join<T, ?> join : joins) {
//            if (!join.whereCollector.isEmpty()) {
//                if (!whereAppended) {
//                    builder.append(" WHERE ");
//                    whereAppended = true;
//                } else {
//                    builder.append(" AND ");
//                }
//                join.whereCollector.appendWhereClause(builder, join.tablePrefix, values);
//            }
//        }
    }

//    private void appendJoinsAndWheres(StringBuilder builder, String tablePrefixOrNull) {
//        values.clear();
//        for (Join<T, ?> join : joins) {
//            builder.append(" JOIN ");
//            builder.append('"').append(join.daoDestination.getTablename()).append('"').append(' ');
//            builder.append(join.tablePrefix).append(" ON ");
//            SqlUtils.appendProperty(builder, join.sourceTablePrefix, join.joinPropertySource).append('=');
//            SqlUtils.appendProperty(builder, join.tablePrefix, join.joinPropertyDestination);
//        }
//        boolean whereAppended = !whereCollector.isEmpty();
//        if (whereAppended) {
//            builder.append(" WHERE ");
//            whereCollector.appendWhereClause(builder, tablePrefixOrNull, values);
//        }
//        for (Join<T, ?> join : joins) {
//            if (!join.whereCollector.isEmpty()) {
//                if (!whereAppended) {
//                    builder.append(" WHERE ");
//                    whereAppended = true;
//                } else {
//                    builder.append(" AND ");
//                }
//                join.whereCollector.appendWhereClause(builder, join.tablePrefix, values);
//            }
//        }
//    }
//
//
//    public long count() {
//        return buildCount().count();
//    }

}
