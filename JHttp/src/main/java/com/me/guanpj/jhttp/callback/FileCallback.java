package com.me.guanpj.jhttp.callback;

import com.me.guanpj.jhttp.error.AppException;

/**
 * Created by Jie on 2017/4/9.
 */

public abstract class FileCallback extends AbstractCallback<String> {

    @Override
    protected String bindData(String result) throws AppException {
        return result;
    }
}
