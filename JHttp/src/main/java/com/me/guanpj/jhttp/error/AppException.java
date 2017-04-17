package com.me.guanpj.jhttp.error;

/**
 * Created by Jie on 2017/4/9.
 */

public class AppException extends Exception {

    public int status;
    public String responsMessage;
    public enum ErrorType {CANCEL, TIMEOUT, SERVER, JSON, IO, FILE_NOT_FOUND, UPLOAD, MANUAL}
    public ErrorType type;

    public AppException(int status, String responseMessage) {
        super(responseMessage);
        this.type = ErrorType.SERVER;
        this.status = status;
        this.responsMessage = responseMessage;
    }

    public AppException(ErrorType type, String message) {
        super(message);
        this.type = type;
    }
}

