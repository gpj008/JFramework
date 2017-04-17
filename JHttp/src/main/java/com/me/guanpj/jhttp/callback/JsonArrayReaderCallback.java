package com.me.guanpj.jhttp.callback;

import com.google.gson.stream.JsonReader;
import com.me.guanpj.jhttp.JsonReadable;
import com.me.guanpj.jhttp.error.AppException;

import java.io.FileReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Jie on 2017/4/9.
 */

public abstract class JsonArrayReaderCallback<T extends JsonReadable> extends AbstractCallback<ArrayList<T>> {

    @Override
    protected ArrayList<T> bindData(String path) throws AppException {
        try {
            Type type = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            ArrayList<T> ts = new ArrayList<T>();

            FileReader fr = new FileReader(path);
            JsonReader jr = new JsonReader(fr);
            String node;
            jr.beginObject();
            while (jr.hasNext()){
                node = jr.nextName();
                if("data".equals(node)){
                    jr.beginArray();
                    while (jr.hasNext()){
                        T t = ((Class<T>)type).newInstance();
                        t.readFromJson(jr);
                        ts.add(t);
                    }
                    jr.endArray();
                }else {
                    jr.skipValue();
                }
            }
            jr.endObject();
            return ts;
        } catch (Exception e) {
            throw new AppException(AppException.ErrorType.JSON, e.getMessage());
        }
    }
}
