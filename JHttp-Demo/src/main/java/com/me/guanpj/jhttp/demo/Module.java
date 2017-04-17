package com.me.guanpj.jhttp.demo;

import com.google.gson.stream.JsonReader;
import com.me.guanpj.jhttp.JsonReadable;
import com.me.guanpj.jhttp.error.AppException;

import java.io.IOException;

/**
 * Created by Jie on 2017/4/10.
 */

public class Module implements JsonReadable {
    public String name;
    public long timestamp;

    @Override
    public void readFromJson(JsonReader reader) throws AppException {
        try {
            reader.beginObject();
            String node ;
            while (reader.hasNext()) {
                node = reader.nextName();
                if ("name".equalsIgnoreCase(node)) {
                    name = reader.nextString();
                } else if ("timestamp".equalsIgnoreCase(node)) {
                    timestamp = reader.nextLong();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            throw new AppException(AppException.ErrorType.JSON, e.getMessage());
        }
    }
}
