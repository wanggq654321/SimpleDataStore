package cn.mappingdb.utils;

import java.lang.reflect.Field;

import cn.mappingdb.annotation.Entity;
import cn.mappingdb.annotation.Id;
import cn.mappingdb.annotation.Property;
import cn.mappingdb.annotation.Transient;

public class ReflectUtils {

    /**
     * 根据实体类 获得 实体类对应的表名
     *
     * @param clazz
     * @return
     */
    public static String getTableName(Class<?> clazz) {
        Entity table = clazz.getAnnotation(Entity.class);
        if (table == null || StringUtils.isEmpty(table.nameInDb())) {
            // 当没有注解的时候默认用类的名称作为表名,并把点（.）替换为下划线(_)
            return clazz.getName().toLowerCase().replace('.', '_');
        }
        return table.nameInDb();
    }

    /**
     * 检测 字段是否已经被标注为 非数据库字段
     *
     * @param field
     * @return
     */
    public static boolean isTransient(Field field) {
        return field.getAnnotation(Transient.class) != null;
    }

    /**
     * 获取某个列
     *
     * @param field
     * @return
     */
    public static String getColumnByField(Field field) {
        Property column = field.getAnnotation(Property.class);
        if (column != null && column.nameInDb().trim().length() != 0) {
            return column.nameInDb();
        }
//        Id primaryKey = field.getAnnotation(Id.class);
//        if (primaryKey != null)
//            return "id";
        return field.getName();
    }

    /**
     * 获取某个列
     *
     * @param field
     * @return
     */
    public static boolean isPk(Field field) {
        Id primaryKey = field.getAnnotation(Id.class);
        return primaryKey != null;
    }

}
