package com.mapping.db.utils;
/**
 * 类描述
 * 创建人 wanggq
 * 创建时间 2015/6/16 17:49.
 */
//// 1.先建立临时表
////  database.execSQL("create table student_temp (uid integer primary key not null,name text,pwd text,addressId)");
//​
//        // 2.把之前表的数据（SQL语句的细节，同学们可以上网查询下）
//// database.execSQL("insert into student_temp (uid,name,pwd,addressid) " + " select uid,name,pwd,addressid from student");
//        ​
//        // 3.删除student 旧表
//// database.execSQL("drop table student");
//        ​
//        // 4.修改 临时表 为 新表 student
//// database.execSQL("alter table student_temp rename to student");
//        ————————————————
//        版权声明：本文为CSDN博主「安卓Framework」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//        原文链接：https://blog.csdn.net/qq_44035463/article/details/128470352
import android.database.Cursor;

import com.mapping.db.annotation.Column;
import com.mapping.db.annotation.PrimaryKey;
import com.mapping.db.annotation.TableName;
import com.mapping.db.annotation.Transient;
import com.mapping.db.bean.HashMapping;
import com.mapping.db.builder.EntityBuilder;
import com.mapping.db.cache.TableInfoCacheFactory;
import com.mapping.db.entity.PKProperyEntity;
import com.mapping.db.entity.PropertyEntity;
import com.mapping.db.entity.TableInfoEntity;
import com.mapping.db.exception.DBException;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class DBUtils implements Serializable {

    private static final long serialVersionUID = 1904592041950251207L;

    /**
     * 通过Cursor获取一个实体数组
     *
     * @param clazz  实体类型
     * @param cursor 数据集合
     * @return 相应实体List数组
     */
    public static <T> List<T> getListEntity(Class<T> clazz, Cursor cursor) {
        List<T> queryList = EntityBuilder.buildQueryList(clazz, cursor);
        return queryList;
    }

    /**
     * 返回数据表中一行的数据
     *
     * @param cursor 数据集合
     * @return TAHashMap类型数据
     */
    public static HashMapping<String> getRowData(Cursor cursor) {
        if (cursor != null && cursor.getColumnCount() > 0) {
            HashMapping<String> hashMap = new HashMapping<String>();
            int columnCount = cursor.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                hashMap.put(cursor.getColumnName(i), cursor.getString(i));
            }
            return hashMap;
        }
        return null;
    }

    /**
     * 根据实体类 获得 实体类对应的表名
     *
     * @param clazz
     * @return
     */
    public static String getTableName(Class<?> clazz) {
        TableName table = clazz.getAnnotation(TableName.class);
        if (table == null || StringUtils.isEmpty(table.name())) {
            // 当没有注解的时候默认用类的名称作为表名,并把点（.）替换为下划线(_)
            return clazz.getName().toLowerCase().replace('.', '_');
        }
        return table.name();
    }

    /**
     * 构建创建表的sql语句
     *
     * @param clazz 实体类型
     * @return 创建表的sql语句
     */
    public static String creatTableSql(Class<?> clazz) throws DBException {
        TableInfoEntity tableInfoEntity = TableInfoCacheFactory.getInstance().getTableInfoEntity(clazz);
        PKProperyEntity pkProperyEntity = tableInfoEntity.getPkProperyEntity();
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("CREATE TABLE IF NOT EXISTS ");
        strSQL.append(tableInfoEntity.getTableName());
        strSQL.append(" ( ");

        if (pkProperyEntity != null) {
            Class<?> primaryClazz = pkProperyEntity.getType();
            if (primaryClazz == int.class || primaryClazz == Integer.class)
                if (pkProperyEntity.isAutoIncrement()) {
                    strSQL.append("\"").append(pkProperyEntity.getColumnName())
                            .append("\"    ")
                            .append("INTEGER PRIMARY KEY AUTOINCREMENT,");
                } else {
                    strSQL.append("\"").append(pkProperyEntity.getColumnName())
                            .append("\"    ").append("INTEGER PRIMARY KEY,");
                }
            else
                strSQL.append("\"").append(pkProperyEntity.getColumnName())
                        .append("\"    ").append("TEXT PRIMARY KEY,");
        } else {
            strSQL.append("\"").append("id").append("\"    ")
                    .append("INTEGER PRIMARY KEY AUTOINCREMENT,");
        }

        Collection<PropertyEntity> propertys = tableInfoEntity.getPropertieArrayList();
        for (PropertyEntity property : propertys) {
            strSQL.append("\"").append(property.getColumnName()).append("\" " + DataTypeUtils.getDbType(property.getType()));
            strSQL.append(" ,");
        }
        strSQL.deleteCharAt(strSQL.length() - 1);
        strSQL.append(" )");
        return strSQL.toString();
    }

    /**
     * 检测 字段是否已经被标注为 非数据库字段
     *
     * @param field
     * @return
     */
    @Deprecated
    public static boolean isTransient(Field field) {
        return field.getAnnotation(Transient.class) != null;
    }

    /**
     * 检查是否是主键
     *
     * @param field
     * @return
     */
    @Deprecated
    public static boolean isPrimaryKey(Field field) {
        return field.getAnnotation(PrimaryKey.class) != null;
    }

    /**
     * 检查是否自增
     *
     * @param field
     * @return
     */
    public static boolean isAutoIncrement(Field field) {
        PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
        if (null != primaryKey) {
            return primaryKey.autoIncrement();
        }
        return false;
    }

    /**
     * 获取某个列
     *
     * @param field
     * @return
     */
    public static String getColumnByField(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null && column.name().trim().length() != 0) {
            return column.name();
        }
        PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
        if (primaryKey != null && primaryKey.name().trim().length() != 0)
            return primaryKey.name();

        return field.getName();
    }

    /**
     * 获得默认值
     *
     * @param field
     * @return
     */
    public static String getPropertyDefaultValue(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null && column.defaultValue().trim().length() != 0) {
            return column.defaultValue();
        }
        return null;
    }
}

