package com.ashwashing.pro.api;

import com.ashwashing.pro.api.bean.AccountInfo;
import com.ashwashing.pro.api.bean.DataResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;

public interface AccountInfoQueryService {
    @GET(Constants.API_GET_USER_INFO)
    Call<DataResult<AccountInfo>> queryAccountInfo(@HeaderMap Map<String, String> headerMap);
}
