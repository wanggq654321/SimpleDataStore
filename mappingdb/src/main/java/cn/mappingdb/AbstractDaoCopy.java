///*
// * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package cn.mappingdb;
//
//import android.database.CrossProcessCursor;
//import android.database.Cursor;
//import android.database.CursorWindow;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteStatement;
//
//import org.greenrobot.greendao.database.DatabaseStatement;
//import org.greenrobot.greendao.internal.DaoConfig;
//import org.greenrobot.greendao.internal.FastCursor;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.List;
//
//import cn.mappingdb.exception.DaoException;
//import cn.mappingdb.internal.SqlUtils;
//import cn.mappingdb.log.DaoLog;
//import cn.mappingdb.query.Query;
//import cn.mappingdb.query.QueryBuilder;
//
///**
// * Base class for all DAOs: Implements entity operations like insert, load, delete, and query.
// * <p>
// * This class is thread-safe.
// *
// * @param <T> Entity type
// * @param <K> Primary key (PK) type; use Void if entity does not have exactly one PK
// * @author Markus
// */
///*
// * When operating on TX, statements, or identity scope the following locking order must be met to avoid deadlocks:
// *
// * 1.) If not inside a TX already, begin a TX to acquire a DB connection (connection is to be handled like a lock)
// *
// * 2.) The DatabaseStatement
// *
// * 3.) identityScope
// */
//public abstract class AbstractDaoCopy {
//
//    protected SQLiteDatabase db;
//
//    public AbstractDaoCopy() {
//        // todo nothing
//    }
//
//    @SuppressWarnings("unchecked")
//    public AbstractDaoCopy(DaoConfig config) {
//        db = config.db;
//    }
//
//    /**
//     * Loads the entity for the given PK.
//     *
//     * @param key a PK value or null
//     * @return The entity or null, if no entity matched the PK value
//     */
//    public <T> T load(K key) {
//        String sql = statements.getSelectByKey();
//        String[] keyArray = new String[]{key.toString()};
//        Cursor cursor = db.rawQuery(sql, keyArray);
//        return loadUniqueAndCloseCursor(cursor);
//    }
//
//    public T loadByRowId(long rowId) {
//        String[] idArray = new String[]{Long.toString(rowId)};
//        Cursor cursor = db.rawQuery(statements.getSelectByRowId(), idArray);
//        return loadUniqueAndCloseCursor(cursor);
//    }
//
//    protected <T> T loadUniqueAndCloseCursor(Cursor cursor) {
//        try {
//            return loadUnique(cursor);
//        } finally {
//            cursor.close();
//        }
//    }
//
//    protected <T> T loadUnique(Cursor cursor) {
//        boolean available = cursor.moveToFirst();
//        if (!available) {
//            return null;
//        } else if (!cursor.isLast()) {
//            throw new DaoException("Expected unique result, but count was " + cursor.getCount());
//        }
//        return loadCurrent(cursor, 0, true);
//    }
//
//    /**
//     * Loads all available entities from the database.
//     */
//    public List<T> loadAll() {
//        String selectAll = SqlUtils.createSqlSelect(tablename, "T", allColumns, false);
//        Cursor cursor = db.rawQuery(selectAll, null);
//        return loadAllAndCloseCursor(cursor);
//    }
//
//    protected List<T> loadAllAndCloseCursor(Cursor cursor) {
//        try {
//            return loadAllFromCursor(cursor);
//        } finally {
//            cursor.close();
//        }
//    }
//
//    /**
//     * Inserts the given entities in the database using a transaction.
//     *
//     * @param entities The entities to insert.
//     */
//    public void insertInTx(Iterable<T> entities) {
//        insertInTx(entities, isEntityUpdateable());
//    }
//
//    /**
//     * Inserts the given entities in the database using a transaction.
//     *
//     * @param entities The entities to insert.
//     */
//    public void insertInTx(T... entities) {
//        insertInTx(Arrays.asList(entities), isEntityUpdateable());
//    }
//
//    /**
//     * Inserts the given entities in the database using a transaction. The given entities will become tracked if the PK
//     * is set.
//     *
//     * @param entities      The entities to insert.
//     * @param setPrimaryKey if true, the PKs of the given will be set after the insert; pass false to improve
//     *                      performance.
//     */
//    public void insertInTx(Iterable<T> entities, boolean setPrimaryKey) {
//        DatabaseStatement stmt = statements.getInsertStatement();
//        executeInsertInTx(stmt, entities, setPrimaryKey);
//    }
//
//    /**
//     * Inserts or replaces the given entities in the database using a transaction. The given entities will become
//     * tracked if the PK is set.
//     *
//     * @param entities      The entities to insert.
//     * @param setPrimaryKey if true, the PKs of the given will be set after the insert; pass false to improve
//     *                      performance.
//     */
//    public void insertOrReplaceInTx(Iterable<T> entities, boolean setPrimaryKey) {
//        DatabaseStatement stmt = statements.getInsertOrReplaceStatement();
//        executeInsertInTx(stmt, entities, setPrimaryKey);
//    }
//
//    /**
//     * Inserts or replaces the given entities in the database using a transaction.
//     *
//     * @param entities The entities to insert.
//     */
//    public void insertOrReplaceInTx(Iterable<T> entities) {
//        insertOrReplaceInTx(entities, isEntityUpdateable());
//    }
//
//    /**
//     * Inserts or replaces the given entities in the database using a transaction.
//     *
//     * @param entities The entities to insert.
//     */
//    public void insertOrReplaceInTx(T... entities) {
//        insertOrReplaceInTx(Arrays.asList(entities), isEntityUpdateable());
//    }
//
//    private void executeInsertInTx(DatabaseStatement stmt, Iterable<T> entities, boolean setPrimaryKey) {
//        db.beginTransaction();
//        try {
//            synchronized (stmt) {
//                try {
//                    if (isStandardSQLite) {
//                        SQLiteStatement rawStmt = (SQLiteStatement) stmt.getRawStatement();
//                        for (T entity : entities) {
//                            bindValues(rawStmt, entity);
//                            if (setPrimaryKey) {
//                                long rowId = rawStmt.executeInsert();
//                                updateKeyAfterInsertAndAttach(entity, rowId, false);
//                            } else {
//                                rawStmt.execute();
//                            }
//                        }
//                    } else {
//                        for (T entity : entities) {
//                            bindValues(stmt, entity);
//                            if (setPrimaryKey) {
//                                long rowId = stmt.executeInsert();
//                                updateKeyAfterInsertAndAttach(entity, rowId, false);
//                            } else {
//                                stmt.execute();
//                            }
//                        }
//                    }
//                } finally {
//                    if (identityScope != null) {
//                        identityScope.unlock();
//                    }
//                }
//            }
//            db.setTransactionSuccessful();
//        } finally {
//            db.endTransaction();
//        }
//    }
//
//    /**
//     * Insert an entity into the table associated with a concrete DAO.
//     *
//     * @return row ID of newly inserted entity
//     */
//    public long insert(T entity) {
//        return executeInsert(entity, statements.getInsertStatement(), true);
//    }
//
//    /**
//     * Insert an entity into the table associated with a concrete DAO <b>without</b> setting key property.
//     * <p>
//     * Warning: This may be faster, but the entity should not be used anymore. The entity also won't be attached to
//     * identity scope.
//     *
//     * @return row ID of newly inserted entity
//     */
//    public long insertWithoutSettingPk(T entity) {
//        return executeInsert(entity, statements.getInsertOrReplaceStatement(), false);
//    }
//
//    /**
//     * Insert an entity into the table associated with a concrete DAO.
//     *
//     * @return row ID of newly inserted entity
//     */
//    public long insertOrReplace(T entity) {
//        return executeInsert(entity, statements.getInsertOrReplaceStatement(), true);
//    }
//
//    private long executeInsert(T entity, DatabaseStatement stmt, boolean setKeyAndAttach) {
//        long rowId;
//        if (db.isDbLockedByCurrentThread()) {
//            rowId = insertInsideTx(entity, stmt);
//        } else {
//            // Do TX to acquire a connection before locking the stmt to avoid deadlocks
//            db.beginTransaction();
//            try {
//                rowId = insertInsideTx(entity, stmt);
//                db.setTransactionSuccessful();
//            } finally {
//                db.endTransaction();
//            }
//        }
//        if (setKeyAndAttach) {
//            updateKeyAfterInsertAndAttach(entity, rowId, true);
//        }
//        return rowId;
//    }
//
//    private long insertInsideTx(T entity, DatabaseStatement stmt) {
//        synchronized (stmt) {
//            if (isStandardSQLite) {
//                SQLiteStatement rawStmt = (SQLiteStatement) stmt.getRawStatement();
//                bindValues(rawStmt, entity);
//                return rawStmt.executeInsert();
//            } else {
//                bindValues(stmt, entity);
//                return stmt.executeInsert();
//            }
//        }
//    }
//
//    protected void updateKeyAfterInsertAndAttach(T entity, long rowId, boolean lock) {
//        if (rowId != -1) {
//            K key = updateKeyAfterInsert(entity, rowId);
//            attachEntity(key, entity, lock);
//        } else {
//            // TODO When does this actually happen? Should we throw instead?
//            DaoLog.w("Could not insert row (executeInsert returned -1)");
//        }
//    }
//
//    /**
//     * "Saves" an entity to the database: depending on the existence of the key property, it will be inserted
//     * (key is null) or updated (key is not null).
//     * <p>
//     * This is similar to {@link #insertOrReplace(Object)}, but may be more efficient, because if a key is present,
//     * it does not have to query if that key already exists.
//     */
//    public void save(T entity) {
//        if (hasKey(entity)) {
//            update(entity);
//        } else {
//            insert(entity);
//        }
//    }
//
//    /**
//     * Saves (see {@link #save(Object)}) the given entities in the database using a transaction.
//     *
//     * @param entities The entities to save.
//     */
//    public void saveInTx(T... entities) {
//        saveInTx(Arrays.asList(entities));
//    }
//
//    /**
//     * Saves (see {@link #save(Object)}) the given entities in the database using a transaction.
//     *
//     * @param entities The entities to save.
//     */
//    public void saveInTx(Iterable<T> entities) {
//        int updateCount = 0;
//        int insertCount = 0;
//        for (T entity : entities) {
//            if (hasKey(entity)) {
//                updateCount++;
//            } else {
//                insertCount++;
//            }
//        }
//        if (updateCount > 0 && insertCount > 0) {
//            List<T> toUpdate = new ArrayList<>(updateCount);
//            List<T> toInsert = new ArrayList<>(insertCount);
//            for (T entity : entities) {
//                if (hasKey(entity)) {
//                    toUpdate.add(entity);
//                } else {
//                    toInsert.add(entity);
//                }
//            }
//
//            db.beginTransaction();
//            try {
//                updateInTx(toUpdate);
//                insertInTx(toInsert);
//                db.setTransactionSuccessful();
//            } finally {
//                db.endTransaction();
//            }
//        } else if (insertCount > 0) {
//            insertInTx(entities);
//        } else if (updateCount > 0) {
//            updateInTx(entities);
//        }
//    }
//
//    /**
//     * Reads all available rows from the given cursor and returns a list of entities.
//     */
//    protected List<T> loadAllFromCursor(Cursor cursor) {
//        int count = cursor.getCount();
//        if (count == 0) {
//            return new ArrayList<T>();
//        }
//        List<T> list = new ArrayList<T>(count);
//        CursorWindow window = null;
//        boolean useFastCursor = false;
//        if (cursor instanceof CrossProcessCursor) {
//            window = ((CrossProcessCursor) cursor).getWindow();
//            if (window != null) { // E.g. Robolectric has no Window at this point
//                if (window.getNumRows() == count) {
//                    cursor = new FastCursor(window);
//                    useFastCursor = true;
//                } else {
//                    DaoLog.d("Window vs. result size: " + window.getNumRows() + "/" + count);
//                }
//            }
//        }
//
//        if (cursor.moveToFirst()) {
//            if (identityScope != null) {
//                identityScope.lock();
//                identityScope.reserveRoom(count);
//            }
//
//            try {
//                if (!useFastCursor && window != null && identityScope != null) {
//                    loadAllUnlockOnWindowBounds(cursor, window, list);
//                } else {
//                    do {
//                        list.add(loadCurrent(cursor, 0, false));
//                    } while (cursor.moveToNext());
//                }
//            } finally {
//                if (identityScope != null) {
//                    identityScope.unlock();
//                }
//            }
//        }
//        return list;
//    }
//
//    private void loadAllUnlockOnWindowBounds(Cursor cursor, CursorWindow window, List<T> list) {
//        int windowEnd = window.getStartPosition() + window.getNumRows();
//        for (int row = 0; ; row++) {
//            list.add(loadCurrent(cursor, 0, false));
//            row++;
//            if (row >= windowEnd) {
//                window = moveToNextUnlocked(cursor);
//                if (window == null) {
//                    break;
//                }
//                windowEnd = window.getStartPosition() + window.getNumRows();
//            } else {
//                if (!cursor.moveToNext()) {
//                    break;
//                }
//            }
//        }
//    }
//
//    /**
//     * Unlock identityScope during cursor.moveToNext() when it is about to fill the window (needs a db connection):
//     * We should not hold the lock while trying to acquire a db connection to avoid deadlocks.
//     */
//    private CursorWindow moveToNextUnlocked(Cursor cursor) {
//        identityScope.unlock();
//        try {
//            if (cursor.moveToNext()) {
//                return ((CrossProcessCursor) cursor).getWindow();
//            } else {
//                return null;
//            }
//        } finally {
//            identityScope.lock();
//        }
//    }
//
//    /**
//     * Internal use only. Considers identity scope.
//     */
//    final protected <T> T loadCurrent(Cursor cursor, int offset, boolean lock) {
//        if (identityScopeLong != null) {
//            if (offset != 0) {
//                // Occurs with deep loads (left outer joins)
//                if (cursor.isNull(pkOrdinal + offset)) {
//                    return null;
//                }
//            }
//
//            long key = cursor.getLong(pkOrdinal + offset);
//            T entity = lock ? identityScopeLong.get2(key) : identityScopeLong.get2NoLock(key);
//            if (entity != null) {
//                return entity;
//            } else {
//                entity = readEntity(cursor, offset);
//                attachEntity(entity);
//                return entity;
//            }
//        } else if (identityScope != null) {
//            K key = readKey(cursor, offset);
//            if (offset != 0 && key == null) {
//                // Occurs with deep loads (left outer joins)
//                return null;
//            }
//            T entity = lock ? identityScope.get(key) : identityScope.getNoLock(key);
//            if (entity != null) {
//                return entity;
//            } else {
//                entity = readEntity(cursor, offset);
//                attachEntity(key, entity, lock);
//                return entity;
//            }
//        } else {
//            // Check offset, assume a value !=0 indicating a potential outer join, so check PK
//            if (offset != 0) {
//                K key = readKey(cursor, offset);
//                if (key == null) {
//                    // Occurs with deep loads (left outer joins)
//                    return null;
//                }
//            }
//            T entity = readEntity(cursor, offset);
//            attachEntity(entity);
//            return entity;
//        }
//    }
//
//    /**
//     * Internal use only. Considers identity scope.
//     */
//    final protected <O> O loadCurrentOther(AbstractDaoCopy<O, ?> dao, Cursor cursor, int offset) {
//        return dao.loadCurrent(cursor, offset, /* TODO check this */true);
//    }
//
//    /**
//     * A raw-style query where you can pass any WHERE clause and arguments.
//     */
//    public List queryRaw(String where, String... selectionArg) {
//        Cursor cursor = db.rawQuery(statements.getSelectAll() + where, selectionArg);
//        return loadAllAndCloseCursor(cursor);
//    }
//
//    /**
//     * Creates a repeatable {@link Query} object based on the given raw SQL where you can pass any WHERE clause and
//     * arguments.
//     */
//    public Query queryRawCreate(String where, Object... selectionArg) {
//        List<Object> argList = Arrays.asList(selectionArg);
//        return queryRawCreateListArgs(where, argList);
//    }
//
//    /**
//     * Creates a repeatable {@link Query} object based on the given raw SQL where you can pass any WHERE clause and
//     * arguments.
//     */
//    public Query queryRawCreateListArgs(String where, Collection<Object> selectionArg) {
//        return Query.internalCreate(this, statements.getSelectAll() + where, selectionArg.toArray());
//    }
//
////    public void deleteAll() {
////        // String sql = SqlUtils.createSqlDelete(config.tablename, null);
////        // db.execSQL(sql);
////        db.execSQL("DELETE FROM '" + config.tablename + "'");
////    }
////
////    /** Deletes the given entity from the database. Currently, only single value PK entities are supported. */
////    public void delete(T entity) {
////        assertSinglePk();
////        K key = getKeyVerified(entity);
////        deleteByKey(key);
////    }
////
////    /** Deletes an entity with the given PK from the database. Currently, only single value PK entities are supported. */
////    public void deleteByKey(K key) {
////        assertSinglePk();
////        DatabaseStatement stmt = statements.getDeleteStatement();
////        if (db.isDbLockedByCurrentThread()) {
////            synchronized (stmt) {
////                deleteByKeyInsideSynchronized(key, stmt);
////            }
////        } else {
////            // Do TX to acquire a connection before locking the stmt to avoid deadlocks
////            db.beginTransaction();
////            try {
////                synchronized (stmt) {
////                    deleteByKeyInsideSynchronized(key, stmt);
////                }
////                db.setTransactionSuccessful();
////            } finally {
////                db.endTransaction();
////            }
////        }
////        if (identityScope != null) {
////            identityScope.remove(key);
////        }
////    }
////
////    private void deleteByKeyInsideSynchronized(K key, DatabaseStatement stmt) {
////        if (key instanceof Long) {
////            stmt.bindLong(1, (Long) key);
////        } else if (key == null) {
////            throw new DaoException("Cannot delete entity, key is null");
////        } else {
////            stmt.bindString(1, key.toString());
////        }
////        stmt.execute();
////    }
////
////    private void deleteInTxInternal(Iterable<T> entities, Iterable<K> keys) {
////        assertSinglePk();
////        DatabaseStatement stmt = statements.getDeleteStatement();
////        List<K> keysToRemoveFromIdentityScope = null;
////        db.beginTransaction();
////        try {
////            synchronized (stmt) {
////                if (identityScope != null) {
////                    identityScope.lock();
////                    keysToRemoveFromIdentityScope = new ArrayList<K>();
////                }
////                try {
////                    if (entities != null) {
////                        for (T entity : entities) {
////                            K key = getKeyVerified(entity);
////                            deleteByKeyInsideSynchronized(key, stmt);
////                            if (keysToRemoveFromIdentityScope != null) {
////                                keysToRemoveFromIdentityScope.add(key);
////                            }
////                        }
////                    }
////                    if (keys != null) {
////                        for (K key : keys) {
////                            deleteByKeyInsideSynchronized(key, stmt);
////                            if (keysToRemoveFromIdentityScope != null) {
////                                keysToRemoveFromIdentityScope.add(key);
////                            }
////                        }
////                    }
////                } finally {
////                    if (identityScope != null) {
////                        identityScope.unlock();
////                    }
////                }
////            }
////            db.setTransactionSuccessful();
////            if (keysToRemoveFromIdentityScope != null && identityScope != null) {
////                identityScope.remove(keysToRemoveFromIdentityScope);
////            }
////        } finally {
////            db.endTransaction();
////        }
////    }
//
//    /**
//     * Deletes the given entities in the database using a transaction.
//     *
//     * @param entities The entities to delete.
//     */
//    public void deleteInTx(Iterable<T> entities) {
//        deleteInTxInternal(entities, null);
//    }
//
//    /**
//     * Deletes the given entities in the database using a transaction.
//     *
//     * @param entities The entities to delete.
//     */
//    public void deleteInTx(T... entities) {
//        deleteInTxInternal(Arrays.asList(entities), null);
//    }
//
//    /**
//     * Deletes all entities with the given keys in the database using a transaction.
//     *
//     * @param keys Keys of the entities to delete.
//     */
//    public void deleteByKeyInTx(Iterable<K> keys) {
//        deleteInTxInternal(null, keys);
//    }
//
//    /**
//     * Deletes all entities with the given keys in the database using a transaction.
//     *
//     * @param keys Keys of the entities to delete.
//     */
//    public void deleteByKeyInTx(K... keys) {
//        deleteInTxInternal(null, Arrays.asList(keys));
//    }
////
////    /** Resets all locally changed properties of the entity by reloading the values from the database. */
////    public void refresh(T entity) {
////        assertSinglePk();
////        K key = getKeyVerified(entity);
////        String sql = statements.getSelectByKey();
////        String[] keyArray = new String[]{key.toString()};
////        Cursor cursor = db.rawQuery(sql, keyArray);
////        try {
////            boolean available = cursor.moveToFirst();
////            if (!available) {
////                throw new DaoException("Entity does not exist in the database anymore: " + entity.getClass()
////                        + " with key " + key);
////            } else if (!cursor.isLast()) {
////                throw new DaoException("Expected unique result, but count was " + cursor.getCount());
////            }
////            readEntity(cursor, entity, 0);
////            attachEntity(key, entity, true);
////        } finally {
////            cursor.close();
////        }
////    }
////
////    public void update(T entity) {
////        assertSinglePk();
////        DatabaseStatement stmt = statements.getUpdateStatement();
////        if (db.isDbLockedByCurrentThread()) {
////            synchronized (stmt) {
////                if (isStandardSQLite) {
////                    updateInsideSynchronized(entity, (SQLiteStatement) stmt.getRawStatement(), true);
////                } else {
////                    updateInsideSynchronized(entity, stmt, true);
////                }
////            }
////        } else {
////            // Do TX to acquire a connection before locking the stmt to avoid deadlocks
////            db.beginTransaction();
////            try {
////                synchronized (stmt) {
////                    updateInsideSynchronized(entity, stmt, true);
////                }
////                db.setTransactionSuccessful();
////            } finally {
////                db.endTransaction();
////            }
////        }
////    }
//
//    public QueryBuilder queryBuilder() {
//        return QueryBuilder.internalCreate(this);
//    }
//
//    /**
//     * See {@link #getKey(Object)}, but guarantees that the returned key is never null (throws if null).
//     */
//    protected K getKeyVerified(T entity) {
//        K key = getKey(entity);
//        if (key == null) {
//            if (entity == null) {
//                throw new NullPointerException("Entity may not be null");
//            } else {
//                throw new DaoException("Entity has no key");
//            }
//        } else {
//            return key;
//        }
//    }
//
//    /**
//     * Gets the SQLiteDatabase for custom database access. Not needed for greenDAO entities.
//     */
//    public SQLiteDatabase getDatabase() {
//        return db;
//    }
//
//    /**
//     * Reads the values from the current position of the given cursor and returns a new entity.
//     */
//    abstract protected <T> T readEntity(Cursor cursor, int offset);
//
//
//    /**
//     * Reads the key from the current position of the given cursor, or returns null if there's no single-value key.
//     */
//    abstract protected <T> T readKey(Cursor cursor, int offset);
//
//    /**
//     * Reads the values from the current position of the given cursor into an existing entity.
//     */
//    abstract protected void readEntity(Cursor cursor, T entity, int offset);
//
//    /** Binds the entity's values to the statement. Make sure to synchronize the statement outside of the method. */
////    abstract protected void bindValues(DatabaseStatement stmt, T entity);
//
//    /**
//     * Binds the entity's values to the statement. Make sure to synchronize the enclosing DatabaseStatement outside
//     * of the method.
//     */
//    protected abstract void bindValues(SQLiteStatement stmt, T entity);
//
//    /**
//     * Updates the entity's key if possible (only for Long PKs currently). This method must always return the entity's
//     * key regardless of whether the key existed before or not.
//     */
//    abstract protected <T> T updateKeyAfterInsert(T entity, long rowId);
//
//    /**
//     * Returns the value of the primary key, if the entity has a single primary key, or, if not, null. Returns null if
//     * entity is null.
//     */
//    abstract protected <T> T getKey(T entity);
//
//    /**
//     * Returns true if the entity is not null, and has a non-null key, which is also != 0.
//     * entity is null.
//     */
//    abstract protected boolean hasKey(T entity);
//
//    /**
//     * Returns true if the Entity class can be updated, e.g. for setting the PK after insert.
//     */
//    abstract protected boolean isEntityUpdateable();
//
//}
