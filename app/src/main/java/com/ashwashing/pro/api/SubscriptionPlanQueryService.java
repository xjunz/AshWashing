package com.ashwashing.pro.api;

import com.ashwashing.pro.api.bean.ListResult;
import com.ashwashing.pro.api.bean.Plan;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;

public interface SubscriptionPlanQueryService {
    @GET(Constants.API_GET_PAY_PLAN)
    Call<ListResult<Plan>> query(@HeaderMap Map<String, String> headerMap);
}
