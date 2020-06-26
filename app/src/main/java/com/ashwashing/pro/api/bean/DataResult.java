package com.ashwashing.pro.api.bean;

public class DataResult<T> extends SimpleResult {
    private T data;

    public T getData() {
        return data;
    }
}
