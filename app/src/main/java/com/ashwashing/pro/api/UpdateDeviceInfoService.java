package com.ashwashing.pro.api;

import com.ashwashing.pro.api.bean.SimpleResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface UpdateDeviceInfoService {
    @FormUrlEncoded
    @POST(Constants.API_SET_DEVICE_INFO)
    Call<SimpleResult> update(@HeaderMap Map<String, String> map,
                              @Field("PrjID") int prjId, @Field("PerMoney") String perMoney, @Field("Rate") String rate, @Field("ChargeMethod") int chargeMethod);

}
