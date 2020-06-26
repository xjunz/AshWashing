package com.ashwashing.pro.api;

import androidx.annotation.StringRes;

import com.ashwashing.pro.api.bean.DataResult;
import com.ashwashing.pro.api.bean.SimpleResult;
import com.ashwashing.pro.api.bean.SprayInfo;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface SprayService {
    @FormUrlEncoded
    @POST(Constants.API_START_SPRAY)
    Call<DataResult<SprayInfo>> start(@HeaderMap Map<String, String> map,
                                      @Field("DevMac") String mac);

    @FormUrlEncoded
    @POST(Constants.API_END_SPRAY)
    Call<SimpleResult> end(@HeaderMap Map<String, String> map,
                           @Field("DevMac") String mac, @Field("ConsumeDT") String consumeDT, @Field("UsedMoney") String cost);

}
