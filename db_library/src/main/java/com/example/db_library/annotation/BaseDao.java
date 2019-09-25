package com.example.db_library.annotation;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;

public class BaseDao<T> implements IBaseDao {

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
                    tableName = entityClass.getName();
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
                if (columnName.equals(fieldName)){
                    columnField = field;
                    break;
                }
            }
            if (columnField!=null){
                cacheMap.put(columnName,columnField);
            }

        }
    }

    private String createTableSQL() {
        StringBuilder builder = new StringBuilder();
        builder.append("create table if not exists ");
        builder.append(tableName + "(");
        Field[] fields = entityClass.getFields();
        for (Field field : fields) {
            Class type = field.getType();

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
    public long insert(Object entity) {
        return 0;
    }
}
