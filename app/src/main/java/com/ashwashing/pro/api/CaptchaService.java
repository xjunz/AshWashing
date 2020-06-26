package com.ashwashing.pro.api;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;

public interface CaptchaService {
    @GET(Constants.API_GET_AUTH_IMAGE)
    Call<ResponseBody> getCaptcha(@HeaderMap Map<String, String> map, @Query("TelPhone") String phone);
}
