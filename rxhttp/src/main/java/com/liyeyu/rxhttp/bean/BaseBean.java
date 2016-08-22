package com.liyeyu.rxhttp.bean;
public class BaseBean<T> {
    public int code;
    public T msg;

    public T getMsg() {
        return msg;
    }

    public void setMsg(T msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
