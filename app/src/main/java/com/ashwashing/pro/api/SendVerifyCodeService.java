package com.ashwashing.pro.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;
import com.ashwashing.pro.api.bean.SimpleResult;

import java.util.Map;

public interface SendVerifyCodeService {
    @GET(Constants.API_GET_VERIFY_CODE)
    Call<SimpleResult> trySend(@HeaderMap Map<String, String> map, @Query("TelPhone") String phone, @Query("Code") String captcha);
}
