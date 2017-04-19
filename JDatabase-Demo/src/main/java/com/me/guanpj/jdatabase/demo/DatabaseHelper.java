package com.me.guanpj.jdatabase.demo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.me.guanpj.jdatabase.core.DBUtil;
import com.me.guanpj.jdatabase.demo.model.Developer;

/**
 * Created by Jie on 2017/4/19.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "gpj.db";
    public static final int DB_VERSION = 1;

    public DatabaseHelper(Context context){
        this(context, DB_NAME, null, DB_VERSION);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DBUtil.createTable(db, Developer.class);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DBUtil.dropTable(db, Developer.class);
    }
}
