package com.ashwashing.pro.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

import com.ashwashing.pro.api.bean.SimpleResult;

import java.util.Map;

/**
 * Retrofit interface for verifying account and constructing {@link com.ashwashing.pro.api.bean.UserQueryInfo}
 */
public interface VerifyService {
    @GET(Constants.API_VERIFY_REGISTER)
    Call<SimpleResult> verify(@HeaderMap Map<String, String> map, @Query("TelPhone") String phoneNum, @Query("UserType") int type, @Query("Passwd") String pwd);
}
