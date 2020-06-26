package com.ashwashing.pro.api;

import com.ashwashing.pro.api.bean.SimpleResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;

public interface ClientCheckService {
    @GET(Constants.API_QUERY_PLATFORM)
    Call<SimpleResult> check(@HeaderMap Map<String, String> headerMap, @Query("DeviceMac") String mac, @Query("AccID") int accID, @Query("PrjID") int prjID, @Query("DevID") int devID);
}
