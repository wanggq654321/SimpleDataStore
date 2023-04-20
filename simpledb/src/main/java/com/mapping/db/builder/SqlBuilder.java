package com.mapping.db.builder;

import android.os.Build;

import com.mapping.db.bean.ArrayListPair;
import com.mapping.db.bean.NameValuePairDB;
import com.mapping.db.cache.TableInfoCacheFactory;
import com.mapping.db.entity.PropertyEntity;
import com.mapping.db.entity.TableInfoEntity;
import com.mapping.db.exception.DBException;
import com.mapping.db.utils.DBUtils;
import com.mapping.db.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 类描述
 * 创建人 wanggq
 * 创建时间 2015/6/16 18:02.
 */

public abstract class SqlBuilder {
    protected Boolean distinct;
    protected String where;
    protected String groupBy;
    protected String having;
    protected String orderBy;
    protected int limit = -1;
    protected Class<?> clazz = null;
    protected String tableName = null;
    protected Object entity;
    protected ArrayListPair updateFields;

    public SqlBuilder(Object entity) {
        this.entity = entity;
        setClazz(entity.getClass());
    }

    public Object getEntity() {
        return entity;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
        setClazz(entity.getClass());
    }

    public void setCondition(boolean distinct, String where, String groupBy,
                             String having, String orderBy, int limit) {
        this.distinct = distinct;
        this.where = where;
        this.groupBy = groupBy;
        this.having = having;
        this.orderBy = orderBy;
        this.limit = limit;
    }

    public ArrayListPair getUpdateFields() {
        return updateFields;
    }

    public void setUpdateFields(ArrayListPair updateFields) {
        this.updateFields = updateFields;
    }

    public SqlBuilder() {
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
        try {
            TableInfoEntity tableInfoEntity = TableInfoCacheFactory.getInstance().getTableInfoEntity(clazz);
            setTableName(tableInfoEntity.getTableName());
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取sql语句
     *
     * @return
     * @throws DBException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public String getSqlStatement() throws DBException,
            IllegalArgumentException, IllegalAccessException {
        onPreGetStatement();
        return buildSql();
    }

    /**
     * 构建sql语句前执行方法
     *
     * @return
     * @throws DBException
     */
    public void onPreGetStatement() throws DBException,
            IllegalArgumentException, IllegalAccessException {

    }

    /**
     * 构建sql语句
     *
     * @return
     * @throws DBException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public abstract String buildSql() throws DBException,
            IllegalArgumentException, IllegalAccessException;

    /**
     * 创建条件字句
     *
     * @return 返回条件Sql
     */
    protected String buildConditionString() {
        StringBuilder query = new StringBuilder(120);
        appendClause(query, " WHERE ", where);
        appendClause(query, " GROUP BY ", groupBy);
        appendClause(query, " HAVING ", having);
        appendClause(query, " ORDER BY ", orderBy);
        if (limit > 0) {
            appendClause(query, " LIMIT ", limit + "");
        }
        return query.toString();
    }

    public boolean isInteger(String value) {
        for (int i = value.length(); --i >= 0; ) {
            int chr = value.charAt(i);
            if (chr < 48 || chr > 57)
                return false;
        }
        return true;
    }

    public static boolean isEmpty(String value) {
        if (value == null || "".equals(value.trim()))
            return true;
        return false;
    }

    protected void appendClause(StringBuilder s, String name, String clause) {
        if (name.trim().equals("WHERE")) {
            if (!isEmpty(clause)) {
                s.append(name);
                s.append(clause);
            }
        } else {
            if (!isEmpty(clause)) {
                s.append(name);
                s.append(clause);
            }
        }
    }

    /**
     * 插入和更新使用
     * 从实体加载,更新的数据
     *
     * @return
     * @throws DBException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    protected ArrayListPair getFieldsAndValue(Object entity)
            throws DBException, IllegalArgumentException, IllegalAccessException {
        // TODO Auto-generated method stub
        ArrayListPair arrayList = new ArrayListPair();
        if (entity == null) {
            throw new DBException("没有加载实体类！");
        }
        TableInfoEntity tableInfoEntity = TableInfoCacheFactory.getInstance().getTableInfoEntity(entity.getClass());
        setTableName(tableInfoEntity.getTableName());
        ArrayList<PropertyEntity> propertyEntityArrayList = tableInfoEntity.getPropertieArrayList();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            propertyEntityArrayList.forEach(propertyEntity -> {
                if (propertyEntity.isPrimaryKey()) {
                    // TODO NOTHING
                } else {
                    Field field = propertyEntity.getField();
                    String columnName = propertyEntity.getColumnName();
                    Object value = null;
                    try {
                        value = field.get(entity);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if (!StringUtils.isBlankOrEmpty(value)) {
                        arrayList.add(StringUtils.isEmptyNull(columnName) ? propertyEntity.getName() : columnName, value.toString(), propertyEntity.getType());
                    }
                }
            });
        } else {
            for (PropertyEntity propertyEntity : propertyEntityArrayList) {
                if (propertyEntity.isPrimaryKey()) {
                    // TODO NOTHING
                } else {
                    Field field = propertyEntity.getField();
                    String columnName = propertyEntity.getColumnName();
                    Object value = field.get(entity);
                    arrayList.add(StringUtils.isEmptyNull(columnName) ? propertyEntity.getName() : columnName,
                            value == null ? null : value.toString(), propertyEntity.getType());
                }
            }
        }
        return arrayList;
    }

    /**
     * 构建where子句
     *
     * @param conditions TAArrayList类型的where数据
     * @return 返回where子句
     */
    public String buildWhere(ArrayListPair conditions) {
        // TODO Auto-generated method stub
        StringBuilder stringBuilder = new StringBuilder(256);
        if (conditions != null) {
            stringBuilder.append(" WHERE ");
            Iterator<NameValuePairDB> var0 = conditions.iterator();
            int i = 0;
            while (var0.hasNext()) {
                i++;
                NameValuePairDB nameValuePair = var0.next();
                stringBuilder
                        .append(nameValuePair.getName())
                        .append(" = ")
                        .append(String.class.equals(nameValuePair.getType()) ? nameValuePair
                                .getValue() : "'" + (nameValuePair.getValue() == null ? "" : nameValuePair.getValue()) + "'");
                if (i + 1 < conditions.size()) {
                    stringBuilder.append(" AND ");
                }
            }
        }
        return stringBuilder.toString();
    }
}
