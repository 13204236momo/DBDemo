package com.example.dbdemo.entity;

import com.example.db_library.annotation.DbFiled;
import com.example.db_library.annotation.DbTable;

@DbTable()
public class UserEntity {

    @DbFiled(value = "u_id")
    private Integer id;

    private String userName;

    private String age;

    private Integer sex;

    public UserEntity(Integer id, String userName, String age, Integer sex) {
        this.id = id;
        this.userName = userName;
        this.age = age;
        this.sex = sex;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", age='" + age + '\'' +
                ", sex=" + sex +
                '}';
    }
}
