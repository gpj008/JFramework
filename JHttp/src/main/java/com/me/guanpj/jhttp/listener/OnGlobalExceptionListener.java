package com.me.guanpj.jhttp.listener;

import com.me.guanpj.jhttp.error.AppException;

/**
 * Created by Jie on 2017/4/9.
 */

public interface OnGlobalExceptionListener {
    boolean handlerException(AppException e);
}
