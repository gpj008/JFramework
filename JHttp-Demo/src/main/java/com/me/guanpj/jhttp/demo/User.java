package com.me.guanpj.jhttp.demo;

import com.google.gson.stream.JsonReader;
import com.me.guanpj.jhttp.JsonReadable;
import com.me.guanpj.jhttp.error.AppException;

import java.io.IOException;

/**
 * Created by Jie on 2017/4/9.
 */

public class User implements JsonReadable {

    public String id;
    public String account;
    public String email;
    public String username;
    public String token;

    @Override
    public String toString() {
        return username + " : " + email;
    }

    @Override
    public void readFromJson(JsonReader reader) throws AppException {
        try {
            reader.beginObject();
            String node ;
            while (reader.hasNext()) {
                node = reader.nextName();
                if ("username".equalsIgnoreCase(node)) {
                    username = reader.nextString();
                } else if ("email".equalsIgnoreCase(node)) {
                    email = reader.nextString();
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
