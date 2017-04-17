package com.me.guanpj.jhttp.callback;

import com.google.gson.Gson;
import com.me.guanpj.jhttp.error.AppException;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Jie on 2017/4/9.
 */

public abstract class JsonCallback<T> extends AbstractCallback<T> {

    @Override
    protected T bindData(String result) throws AppException {
        try {
            /*JSONObject object = new JSONObject(result);
            JSONObject data = object.optJSONObject("data");
            Gson gson = new Gson();
            Type type = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            return gson.fromJson(data.toString(), type);*/

            JSONObject json = new JSONObject(result);
            Object data = json.opt("data");
            Gson gson = new Gson();
            Type type = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            return gson.fromJson(data.toString(), type);
        } catch (JSONException e) {
            throw new AppException(AppException.ErrorType.JSON, e.getMessage());
        }
    }
}
