package cn.mappingdb.utils;

import android.database.sqlite.SQLiteProgram;

import java.io.Serializable;

import cn.mappingdb.bean.Property;
import cn.mappingdb.cache.EntifyInfoCacheFactory;
import cn.mappingdb.exception.DaoException;
import cn.mappingdb.internal.EntifyConfig;

/**
 * 类描述
 * 创建人 wanggq
 * 创建时间 2015/6/16 17:49.
 */

public class DBUtils implements Serializable {

    private static final long serialVersionUID = 1904592041950251207L;

    /**
     * 构建创建表的sql语句
     *
     * @param tableInfoEntity 实体类型
     * @return 创建表的sql语句
     */
    public static String creatTableSql(EntifyConfig tableInfoEntity) throws DaoException {
        // EntifyConfig tableInfoEntity = EntifyInfoCacheFactory.getInstance().getTableInfoEntity(clazz);
        Property pkProperyEntity = tableInfoEntity.pkProperty;
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("CREATE TABLE IF NOT EXISTS ");
        strSQL.append(tableInfoEntity.tablename);
        strSQL.append(" ( ");

        if (pkProperyEntity != null) {
            Class<?> primaryClazz = pkProperyEntity.type;
            if (DataTypeUtils.isNum(primaryClazz))
                // if (pkProperyEntity.isAutoIncrement()) {
                strSQL.append("\"").append(pkProperyEntity.columnName)
                        .append("\"    ")
                        .append("INTEGER PRIMARY KEY AUTOINCREMENT,");
//                } else {
//                    strSQL.append("\"").append(pkProperyEntity.getColumnName())
//                            .append("\"    ").append("INTEGER PRIMARY KEY,");
//                }
            else
                strSQL.append("\"").append(pkProperyEntity.columnName)
                        .append("\"    ").append("TEXT PRIMARY KEY,");
        } else {
            strSQL.append("\"").append("id").append("\"    ")
                    .append("INTEGER PRIMARY KEY AUTOINCREMENT,");
        }

        Property[] propertys = tableInfoEntity.properties;
        for (Property property : propertys) {
            if (!property.primaryKey) {
                strSQL.append("\"").append(property.columnName).append("\" " + DataTypeUtils.getDbType(property.type));
                strSQL.append(" ,");
            }
        }
        strSQL.deleteCharAt(strSQL.length() - 1);
        strSQL.append(" )");
        return strSQL.toString();
    }

    /**
     * 参考： DatabaseUtils.bindObjectToProgram
     *
     * @param prog
     * @param index
     * @param value
     */
    public static void bindObjectToProgram(SQLiteProgram prog, int index,
                                           Object value) {
        if (value == null) {
            prog.bindNull(index);
        } else if (value instanceof Double || value instanceof Float) {
            prog.bindDouble(index, ((Number) value).doubleValue());
        } else if (value instanceof Number) {
            prog.bindLong(index, ((Number) value).longValue());
        } else if (value instanceof Boolean) {
            Boolean bool = (Boolean) value;
            if (bool) {
                prog.bindLong(index, 1);
            } else {
                prog.bindLong(index, 0);
            }
        } else if (value instanceof byte[]) {
            prog.bindBlob(index, (byte[]) value);
        } else {
            prog.bindString(index, value.toString());
        }
    }


}

