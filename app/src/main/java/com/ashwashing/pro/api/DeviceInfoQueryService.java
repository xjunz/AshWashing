package com.ashwashing.pro.api;

import com.ashwashing.pro.api.bean.DataResult;
import com.ashwashing.pro.api.bean.DeviceInfo;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;


/**
 * Retrofit interface for device query,used for constructing {@link DeviceInfo} beans.
 * Query url example: <href>http://120.77.245.8/api/getDeviceInfo?DeviceMac=00%3A15%3A83%3AD3%3A6A%3A1C</href>
 */
public interface DeviceInfoQueryService {

    @GET(Constants.API_GET_DEVICE_INFO)
    Call<DataResult<DeviceInfo>> query(@HeaderMap Map<String, String> headerMap, @Query("DeviceMac") String mac);
}
