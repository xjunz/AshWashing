package com.ashwashing.pro.api;

import com.ashwashing.pro.api.bean.DataResult;
import com.ashwashing.pro.api.bean.OrderInfo;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;

public interface OrderQueryService {
    @GET(Constants.API_GET_ORDER)
    Call<DataResult<OrderInfo>> query(@HeaderMap Map<String, String> headerMap, @Query("PlanType")int type);
}
