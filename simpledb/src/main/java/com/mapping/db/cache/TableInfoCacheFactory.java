package com.mapping.db.cache;
/**
 * 类描述
 * 创建人 wanggq
 * 创建时间 2015/6/16 17:59.
 */

import com.mapping.db.annotation.PrimaryKey;
import com.mapping.db.entity.PKProperyEntity;
import com.mapping.db.entity.PropertyEntity;
import com.mapping.db.entity.TableInfoEntity;
import com.mapping.db.exception.DBException;
import com.mapping.db.utils.DBUtils;
import com.mapping.db.utils.DataTypeUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class TableInfoCacheFactory implements Serializable {


    private static final long serialVersionUID = 1905122041950369877L;

    private static TableInfoCacheFactory instance;

    /**
     * android studio = "View"--"Tools Window"--"Profiler"  --->>  点击"Memory"，可以查看详细的进程memory相关信息。如下图，点击左上角"垃圾桶"图标，就是强制调用GC
     * 注意：System.gc()并不一定可以工作,建议使用Android Studio的Force GC
     * <p>
     * 表名为键，表信息为值的HashMap
     */
    private final WeakHashMap<CacheWeakKey, TableInfoEntity> tableInfoCacheMap = new WeakHashMap<>();

    private WeakHashMap<CacheWeakKey, TableInfoEntity> getTableInfoCacheMap() {
        return tableInfoCacheMap;
    }

    private TableInfoCacheFactory() {
        // todo nothing
    }

    /**
     * 获得数据库表工厂
     *
     * @return 数据库表工厂
     */
    public static TableInfoCacheFactory getInstance() {
        synchronized (TableInfoCacheFactory.class) {
            if (instance == null) {
                instance = new TableInfoCacheFactory();
            }
        }
        return instance;
    }

    /**
     * 获得表信息
     *
     * @param clazz 实体类型
     * @return 表信息
     * @throws DBException
     */
    public TableInfoEntity getTableInfoEntity(Class<?> clazz) throws DBException {
        if (clazz == null)
            throw new DBException("表信息获取失败，应为class为null");
        TableInfoEntity tableInfoEntity = tableInfoCacheMap.get(new CacheWeakKey(clazz.getName()));
        if (tableInfoEntity == null) {
            tableInfoEntity = new TableInfoEntity();
            tableInfoEntity.setTableName(DBUtils.getTableName(clazz));
            tableInfoEntity.setClassName(clazz.getName());
            Field idField = getPrimaryKeyField(clazz);
            if (idField != null) {
                PKProperyEntity pkProperyEntity = new PKProperyEntity();
                pkProperyEntity.setColumnName(DBUtils.getColumnByField(idField));
                pkProperyEntity.setName(idField.getName());
                pkProperyEntity.setType(idField.getType());
                pkProperyEntity.setAutoIncrement(DBUtils.isAutoIncrement(idField));
                tableInfoEntity.setPkProperyEntity(pkProperyEntity);
            } else {
                tableInfoEntity.setPkProperyEntity(null);
            }
            List<PropertyEntity> propertyList = getPropertyList(clazz);
            if (propertyList != null) {
                tableInfoEntity.setPropertieArrayList(propertyList);
            }
            tableInfoCacheMap.put(new CacheWeakKey(clazz.getName()), tableInfoEntity);
        }
        if (tableInfoEntity == null || tableInfoEntity.getPropertieArrayList() == null || tableInfoEntity.getPropertieArrayList().size() == 0) {
            throw new DBException("不能创建+" + clazz + "的表信息");
        }
        return tableInfoEntity;
    }

    /**
     * 返回数据库字段数组
     *
     * @param clazz 实体类型
     * @return 数据库的字段数组
     */
    private static List<PropertyEntity> getPropertyList(Class<?> clazz) {

        List<PropertyEntity> plist = new ArrayList<PropertyEntity>();
        try {
            Field[] fields = clazz.getDeclaredFields();
            String primaryKeyFieldName = getPrimaryKeyFieldName(clazz);
            for (Field field : fields) {
                if (!DBUtils.isTransient(field)) {
                    if (DataTypeUtils.isBaseDateType(field)) {

                        if (field.getName().equals(primaryKeyFieldName)) // 过滤主键
                            continue;

                        PKProperyEntity property = new PKProperyEntity();

                        field.setAccessible(true);
                        property.setField(field);

                        property.setColumnName(DBUtils.getColumnByField(field));
                        property.setName(field.getName());
                        property.setType(field.getType());
                        // property.setDefaultValue(DBUtils.getPropertyDefaultValue(field));
                        plist.add(property);
                    }
                }
            }
            return plist;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 返回主键名
     *
     * @param clazz 实体类型
     * @return
     */
    private static String getPrimaryKeyFieldName(Class<?> clazz) {
        Field f = getPrimaryKeyField(clazz);
        return f == null ? "id" : f.getName();
    }

    /**
     * 返回主键字段
     *
     * @param clazz 实体类型
     * @return
     */
    private static Field getPrimaryKeyField(Class<?> clazz) {
        Field primaryKeyField = null;
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null) {

            for (Field field : fields) { // 获取ID注解
                if (field.getAnnotation(PrimaryKey.class) != null) {
                    primaryKeyField = field;
                    break;
                }
            }
            if (primaryKeyField == null) { // 没有ID注解
                for (Field field : fields) {
                    if ("_id".equals(field.getName())) {
                        primaryKeyField = field;
                        break;
                    }
                }
                if (primaryKeyField == null) { // 如果没有_id的字段
                    for (Field field : fields) {
                        if ("id".equals(field.getName())) {
                            primaryKeyField = field;
                            break;
                        }
                    }
                }
            }
        } else {
            throw new RuntimeException("this model[" + clazz + "] has no field");
        }
        return primaryKeyField;
    }

}
