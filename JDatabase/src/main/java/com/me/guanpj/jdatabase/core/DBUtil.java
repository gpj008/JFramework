package com.me.guanpj.jdatabase.core;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.me.guanpj.jdatabase.annotation.Column;
import com.me.guanpj.jdatabase.annotation.Table;
import com.me.guanpj.jdatabase.utility.TextUtil;

import java.lang.reflect.Field;

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
        StringBuilder sb = new StringBuilder();
        if(clz.isAnnotationPresent(Table.class)){
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) {
                if(field.isAnnotationPresent(Column.class)){
                    sb.append(getColumnStatement(field));
                }
            }
        }
        return null;
    }

    private static String getDropTableStatement(Class<?> clz) {
        return "drop table if exists " + getTableName(clz);
    }

    private static String getColumnStatement(Field field) {
        Column column = field.getAnnotation(Column.class);
        String name = column.name();
        String type = null;
        Class<?> clz = field.getType();
        if(TextUtil.isValidate(name)){
            name = "[" + name + "]";
        } else {
            name = "[" + field.getName() + "]";
        }
        return name;
    }

    private static String getTableName(Class<?> clz) {
        if(clz.isAnnotationPresent(Table.class)){
            String name = clz.getAnnotation(Table.class).name();
            if(TextUtil.isValidate(name)){
                return name;
            } else {
                return clz.getSimpleName().toLowerCase();
            }
        }
        throw new IllegalArgumentException("the class " + clz.getSimpleName() + " can't map to the table");
    }

}
