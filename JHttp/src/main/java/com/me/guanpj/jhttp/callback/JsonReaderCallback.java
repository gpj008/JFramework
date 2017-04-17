package com.me.guanpj.jhttp.callback;

import com.google.gson.stream.JsonReader;
import com.me.guanpj.jhttp.JsonReadable;
import com.me.guanpj.jhttp.error.AppException;

import java.io.FileReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Jie on 2017/4/9.
 */

public abstract class JsonReaderCallback<T extends JsonReadable> extends AbstractCallback<T> {

    @Override
    protected T bindData(String path) throws AppException {
        try {
            Type type = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            T t = ((Class<T>)type).newInstance();
            FileReader fr = new FileReader(path);
            JsonReader jr = new JsonReader(fr);
            String node;
            jr.beginObject();
            while (jr.hasNext()){
                node = jr.nextName();
                if("data".equals(node)){
                    t.readFromJson(jr);
                }else {
                    jr.skipValue();
                }
            }
            jr.endObject();
            return t;
        } catch (Exception e) {
            throw new AppException(AppException.ErrorType.JSON, e.getMessage());
        }
    }
}
