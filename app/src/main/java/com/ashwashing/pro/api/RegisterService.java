package com.ashwashing.pro.api;

import com.ashwashing.pro.api.bean.SimpleResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

/**
 * Retrofit interface for verifying account and constructing {@link com.ashwashing.pro.api.bean.UserQueryInfo}
 */
public interface RegisterService {
    @FormUrlEncoded
    @POST(Constants.API_REGISTER)
    Call<SimpleResult> register(@HeaderMap Map<String, String> map,
                                @Field("TelPhone") String phoneNum, @Field("UserType") int type, @Field("Passwd") String pwd, @Field("UserName") String username, @Field("Password") String password);
}
