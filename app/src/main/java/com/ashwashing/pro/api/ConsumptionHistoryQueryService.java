package com.ashwashing.pro.api;

import com.ashwashing.pro.api.bean.Consumption;
import com.ashwashing.pro.api.bean.ListResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;

public interface ConsumptionHistoryQueryService {
    @GET(Constants.API_GET_CONSUME_HISTORY)
    Call<ListResult<Consumption>> getAll(@HeaderMap Map<String, String> headerMap);
}
