package com.ashwashing.pro.api;

import com.ashwashing.pro.api.bean.AccountInfo;
import com.ashwashing.pro.api.bean.DataResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;

public interface LoginService {
    @GET(Constants.API_LOGIN)
    Call<DataResult<AccountInfo>> logIn(@HeaderMap Map<String, String> map, @Query("UserName") String username, @Query("Password") String pwd);
}
