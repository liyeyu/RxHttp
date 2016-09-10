package com.liyeyu.rxhttp;

import java.io.Serializable;

/**
 * 实体基类
 * Created by liyeyu on 2016/5/16.
 */
public class BaseInfo implements Serializable {

    public BaseInfo(int code) {
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
