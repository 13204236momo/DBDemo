package com.example.db_library.annotation;

public interface IBaseDao<T> {
    long insert(T entity);
}
