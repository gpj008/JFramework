package com.me.guanpj.jdatabase.core;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.me.guanpj.jdatabase.annotation.Table;

/**
 * Created by Jie on 2017/4/17.
 */

public class DBUtil {

    public static void createTable(SQLiteDatabase db, Class<?> clz) throws SQLiteException{
        db.execSQL(getCreateTableStatement(clz));
    }

    public static void dropTable(SQLiteDatabase db, Class<?> clz) throws SQLiteException{
        db.execSQL(getDropTableStatement(clz));
    }

    private static String getCreateTableStatement(Class<?> clz) {
        return null;
    }

    private static String getDropTableStatement(Class<?> clz) {
        return "drop table if exists " + getTableName(clz);
    }

    private static String getTableName(Class<?> clz) {
        if(clz.isAnnotationPresent(Table.class)){

        }
        return null;
    }

}
