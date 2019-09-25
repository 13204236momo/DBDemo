package com.example.dbdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.db_library.BaseDao;
import com.example.db_library.BaseDaoFactory;
import com.example.dbdemo.entity.UserEntity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void insert(View view) {
        BaseDao baseDao = BaseDaoFactory.getInstance().getBaseDao(UserEntity.class);
        long zhoumohan = baseDao.insert(new UserEntity(1, "zhoumohan", "24", 1));
        Toast.makeText(this,zhoumohan+"",Toast.LENGTH_LONG).show();
    }
}
