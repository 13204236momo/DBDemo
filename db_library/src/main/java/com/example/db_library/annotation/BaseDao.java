package com.example.db_library.annotation;

import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;

public class BaseDao<T> implements IBaseDao {

    //持有数据库操作的引用
    private SQLiteDatabase database;
    //表名
    private String tableName;
    //操作数据库所对应的的java类型
    private Class<T> entityClass;
    //标识是否已经初始化
    private boolean isInit = false;

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
                isInit = true;
            }
        }
        return isInit;
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
    public long inster(Object entity) {
        return 0;
    }
}
