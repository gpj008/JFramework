package com.me.guanpj.jdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.me.guanpj.jdatabase.annotation.Column;
import com.me.guanpj.jdatabase.annotation.Table;
import com.me.guanpj.jdatabase.core.DBUtil;
import com.me.guanpj.jdatabase.utility.SerializeUtil;
import com.me.guanpj.jdatabase.utility.TextUtil;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Created by Jie on 2017/4/19.
 */

public class DBManager {
    private Context mContext;
    private static DBManager mInstance;
    private static SQLiteOpenHelper mHelper;
    private static SQLiteDatabase mDatabase;

    private DBManager(Context context, SQLiteOpenHelper heler) {
        mContext = context;
        mHelper = heler;
        mDatabase = mHelper.getWritableDatabase();
    }

    public static void init(Context context, SQLiteOpenHelper helper) {
        if (mInstance == null) {
            mInstance = new DBManager(context, helper);
        }
    }

    public static DBManager getInstance() {
        return mInstance;
    }

    public <T> void newOrUpdate(T t) {
        if (t.getClass().isAnnotationPresent(Table.class)) {
            Field[] fields = t.getClass().getDeclaredFields();
            ContentValues values = new ContentValues();
            try {
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Column.class)) {
                        field.setAccessible(true);
                        Class<?> fieldType = field.getType();
                        if (fieldType == String.class) {
                            Object value = field.get(t);
                            if (value != null) {
                                values.put(DBUtil.getColumnName(field), value.toString());
                            }
                        } else if (fieldType == int.class || fieldType == Integer.class) {
                            values.put(DBUtil.getColumnName(field), field.getInt(t));
                        } else {
                            Column column = field.getAnnotation(Column.class);
                            Column.ColumnType myType = column.type();
                            if (!TextUtil.isValidate(myType.name())) {
                                throw new IllegalArgumentException("you should set myType to the special column:" + t.getClass().getSimpleName() + "."
                                        + field.getName());
                            }
                            if (myType == Column.ColumnType.SERIALIZABLE) {
                                values.put(DBUtil.getColumnName(field), SerializeUtil.serialize(field.get(t)));
                            } else if (myType == Column.ColumnType.TONE) {
                                Object tone = field.get(t);
                                if(tone == null){
                                    continue;
                                }
                                if(tone.getClass().isAnnotationPresent(Table.class)) {
                                    String idFieldNameName = DBUtil.getIdFieldName(tone.getClass());
                                    Field toneIdField = tone.getClass().getDeclaredField(idFieldNameName);
                                    if(toneIdField != null) {
                                        toneIdField.setAccessible(true);
                                        values.put(DBUtil.getColumnName(field), toneIdField.get(tone).toString());
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mDatabase.replace(DBUtil.getTableName(t.getClass()), null, values);
        }
    }

    public <T> void delete(T t) {
        try {
            String idColumnName = DBUtil.getIdColumnName(t.getClass());
            Field idField = t.getClass().getDeclaredField(idColumnName);
            if (idField != null) {
                idField.setAccessible(true);
                String idValue = idField.get(t).toString();
                mDatabase.delete(DBUtil.getTableName(t.getClass()), idColumnName + "=?", new String[]{idValue});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> T queryById(Class<T> clz, String id) {
        String queryStr = "select * from " + DBUtil.getTableName(clz)
                + " where " + DBUtil.getIdColumnName(clz) + "=?";
        Cursor cursor = mDatabase.rawQuery(queryStr, new String[]{id});
        T t = null;
        if (cursor.moveToNext()) {
            try {
                t = clz.newInstance();
                Field[] fields = t.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Column.class)) {
                        field.setAccessible(true);
                        Class<?> fieldType = field.getType();
                        int columnIndex = cursor.getColumnIndex(DBUtil.getColumnName(field));
                        if (fieldType == String.class) {
                            field.set(t, cursor.getString(columnIndex));
                        } else if (fieldType == int.class || fieldType == Integer.class) {
                            field.setInt(t, cursor.getInt(columnIndex));
                        } else {
                            Column column = field.getAnnotation(Column.class);
                            Column.ColumnType myType = column.type();
                            if (myType == Column.ColumnType.SERIALIZABLE) {
                                field.set(t, SerializeUtil.deserialize(cursor.getBlob(columnIndex)));
                            } else if(myType == Column.ColumnType.TONE) {
                                String toneId = cursor.getString(columnIndex);
                                if(!TextUtil.isValidate(toneId)){
                                    continue;
                                }
                                Object tone = null;
                                if(column.autofresh()){
                                    tone = queryById(tone.getClass(), toneId);
                                } else {
                                    if(fieldType.isAnnotationPresent(Table.class)){
                                        tone = fieldType.newInstance();
                                        String idFieldName = DBUtil.getIdFieldName(tone.getClass());
                                        Field toneIdField = tone.getClass().getDeclaredField(idFieldName);
                                        toneIdField.setAccessible(true);
                                        toneIdField.set(tone, toneId);
                                    }
                                }
                                field.set(t, tone);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return t;
    }
}
