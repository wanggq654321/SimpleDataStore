package cn.mappingdb.cache;

import java.io.Serializable;
import java.util.WeakHashMap;

import cn.mappingdb.database.DBTool;
import cn.mappingdb.exception.DaoException;
import cn.mappingdb.internal.EntifyConfig;
import cn.mappingdb.log.DaoLog;
import cn.mappingdb.utils.DBUtils;

public class EntifyInfoCacheFactory implements Serializable {

    private static final long serialVersionUID = 1905122041950369877L;

    private static EntifyInfoCacheFactory instance;

    /**
     * android studio = "View"--"Tools Window"--"Profiler"  --->>  点击"Memory"，可以查看详细的进程memory相关信息。如下图，点击左上角"垃圾桶"图标，就是强制调用GC
     * 注意：System.gc()并不一定可以工作,建议使用Android Studio的Force GC
     * <p>
     * 表名为键，表信息为值的HashMap
     */
    private final WeakHashMap<CacheWeakKey, EntifyConfig> tableInfoCacheMap = new WeakHashMap<>();

    private EntifyInfoCacheFactory() {
        // todo nothing
    }

    /**
     * 获得数据库表工厂
     *
     * @return 数据库表工厂
     */
    public static EntifyInfoCacheFactory getInstance() {
        synchronized (EntifyInfoCacheFactory.class) {
            if (instance == null) {
                instance = new EntifyInfoCacheFactory();
            }
        }
        return instance;
    }

    /**
     * 获取缓存的 Entity 结构配置信息
     *
     * @param clazz Entity
     * @return Entity 结构配置信息
     */
    public EntifyConfig getTableInfoEntity(Class<?> clazz) {
        if (clazz == null)
            throw new DaoException("DaoConfig 表信息获取失败，应为class为null");
        EntifyConfig tableInfoEntity = tableInfoCacheMap.get(new CacheWeakKey(clazz.getName()));
        if (tableInfoEntity == null) {

            /**
             * 获取 Entity 类信息，字段信息,缓存起来便于使用
             */
            tableInfoEntity = new EntifyConfig(clazz);
            tableInfoCacheMap.put(new CacheWeakKey(clazz.getName()), tableInfoEntity);

            /**
             * 校验表并且生成
             */
            String sqlString = DBUtils.creatTableSql(tableInfoEntity);
            DaoLog.d("creatTableSql: " + sqlString);
            DBTool.getInstance().getDb().openWritable(null).execSQL(sqlString);
        }
        return tableInfoEntity;
    }

}
