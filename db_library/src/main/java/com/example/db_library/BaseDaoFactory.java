package com.example.db_library;

import android.database.sqlite.SQLiteDatabase;

public class BaseDaoFactory {

    private static BaseDaoFactory instance;
    private SQLiteDatabase database;
    private static final String sqlitePath = "data/data/com.example.dbdemo/ne.db";


    public static BaseDaoFactory getInstance() {
        if (instance == null) {
            synchronized (BaseDaoFactory.class) {
                if (instance == null) {
                    instance = new BaseDaoFactory();
                }
            }
        }
        return instance;
    }

    public BaseDaoFactory() {
        database = SQLiteDatabase.openOrCreateDatabase(sqlitePath, null);

    }

    //生成baseDao对象
    public <T> BaseDao<T> getBaseDao(Class<T> entityClass){
        BaseDao baseDao = null;
        try {
            baseDao = BaseDao.class.newInstance();
            baseDao.init(database,entityClass);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return baseDao;
    }
}
