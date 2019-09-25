package com.example.db_library;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.example.db_library.annotation.DbFiled;
import com.example.db_library.annotation.DbTable;
import com.example.db_library.annotation.IBaseDao;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BaseDao<T> implements IBaseDao<T> {

    //持有数据库操作的引用
    private SQLiteDatabase database;
    //表名
    private String tableName;
    //操作数据库所对应的的java类型
    private Class<T> entityClass;
    //标识是否已经初始化
    private boolean isInit = false;
    //缓存集合 （key 字段名 value 成员变量）
    private HashMap<String, Field> cacheMap;

    protected boolean init(SQLiteDatabase database, Class<T> entityClass) {
        this.database = database;
        this.entityClass = entityClass;

        if (!isInit) {
            //根据传入的class进行数据表创建
            DbTable annotation = entityClass.getAnnotation(DbTable.class);
            if (annotation != null) {
                if (annotation.value() != null && !annotation.value().equalsIgnoreCase("")) {
                    tableName = annotation.value();
                } else {
                    tableName = entityClass.getSimpleName();
                }
                String createTableSQL = createTableSQL();
                database.execSQL(createTableSQL);
                cacheMap = new HashMap<>();
                initCacheMap();
                isInit = true;
            }
        }
        return isInit;
    }

    private void initCacheMap() {
        //取得所有列名
        String sql = "select * from " + tableName + " limit 1,0";
        Cursor cursor = database.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        //获取所有的成员变量
        Field[] columnFields = entityClass.getDeclaredFields();
        for (Field field : columnFields) {
            field.setAccessible(true);
        }
        for (String columnName : columnNames) {
            Field columnField = null;
            for (Field field : columnFields) {
                String fieldName = null;
                fieldName = getColumnName(field);
                if (columnName.equals(fieldName)) {
                    columnField = field;
                    break;
                }
            }
            if (columnField != null) {
                cacheMap.put(columnName, columnField);
            }

        }
    }

    private String createTableSQL() {
        StringBuilder builder = new StringBuilder();
        builder.append("create table if not exists ");
        builder.append(tableName + "(");
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            Class type = field.getType();
            field.setAccessible(true);
            if (type == String.class) {
                builder.append(getColumnName(field) + " TEXT,");
            } else if (type == Integer.class) {
                builder.append(getColumnName(field) + " INTEGER,");
            } else if (type == Long.class) {
                builder.append(getColumnName(field) + " BIGINT,");
            } else if (type == Double.class) {
                builder.append(getColumnName(field) + " DOUBLE,");
            } else if (type == byte[].class) {
                builder.append(getColumnName(field) + " BLOB,");
            } else {
                //不支持的类型
                continue;
            }
        }

        //去掉最后一个逗号
        if (builder.charAt(builder.length() - 1) == ',') {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append(")");
        return builder.toString();
    }

    /**
     * 得到表的属性名
     *
     * @param field
     * @return
     */
    private String getColumnName(Field field) {
        String columnName;
        DbFiled dbField = field.getAnnotation(DbFiled.class);
        if (dbField != null && !dbField.value().equalsIgnoreCase("")) {
            columnName = dbField.value();
        } else {
            columnName = field.getName();
        }
        return columnName;
    }

    @Override
    public long insert(T entity) {
        Map<String, String> map = getValues(entity);
        ContentValues values = getContentValues(map);
        return database.insert(tableName, null, values);
    }

    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Set keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            String value = map.get(key);
            if (value != null){
                contentValues.put(key,value);
            }
        }

        return contentValues;
    }

    private Map<String, String> getValues(T entity) {
        HashMap<String, String> map = new HashMap<>();
        //得到所有的成员变量
        Iterator<Field> fieldIterable = cacheMap.values().iterator();
        while (fieldIterable.hasNext()) {
            Field field = fieldIterable.next();
            field.setAccessible(true);
            try {
                Object object = field.get(entity);
                if (object == null) {
                    continue;
                }
                String value = object.toString();
                //获取列名
                String key = getColumnName(field);
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    map.put(key, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
