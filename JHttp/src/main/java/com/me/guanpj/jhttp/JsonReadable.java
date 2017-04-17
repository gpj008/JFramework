package com.me.guanpj.jhttp;

import com.google.gson.stream.JsonReader;
import com.me.guanpj.jhttp.error.AppException;

/**
 * Created by Jie on 2017/4/10.
 */

public interface JsonReadable {
    void readFromJson(JsonReader reader) throws AppException, AppException;
}
