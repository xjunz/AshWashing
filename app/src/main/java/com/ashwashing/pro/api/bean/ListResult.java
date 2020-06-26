package com.ashwashing.pro.api.bean;

import java.util.List;

public class ListResult<T> extends SimpleResult {
    private List<T> data;

    public List<T> getAll() {
        return data;
    }
}
