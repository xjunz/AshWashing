package com.ashwashing.pro.api;

import androidx.annotation.Keep;

public class Constants {

    static final String API_GET_ORDER = "account/getOrder";
    static final String API_GET_PAY_PLAN = "account/getPayPlan";
    @Keep
    static String VICTIM = "victim";
    static final String API_VERIFY_REGISTER = "account/checkRegister?";
    static final String API_SET_DEVICE_INFO = "setDeviceInfo";
    static final String API_START_SPRAY = "startSpray";
    static final String API_END_SPRAY = "endSpray";
    static final String API_GET_VERIFY_CODE = "getVerifyCode?";
    static final String API_REGISTER = "account/register";
    static final String API_LOGIN = "account/login?";
    //TODO:replace
    public static final String API_BASE_URL = "http://120.77.245.8/api/";
    //public static final String API_BASE_URL = "http://s19.natfrp.org:12935/api/";
    public static final int USER_TYPE_QZ = 0;
    public static final int USER_TYPE_HY = 1;
    public static final String HEADER_FIELD_AUTH_TOKEN = "X-Auth-Token";
    public static final String HEADER_FIELD_DEVICE_ID = "device-id";
    public static final String HEADER_FIELD_APP_SIGNATURE = "api-sign";
    public static final String HEADER_FIELD_USER_AGENT = "User-Agent";
    public static final String HEADER_FIELD_APP_VERSION = "app-version";
    public static final String HEADER_FIELD_APP_TIME = "app-time";
    public static final int CODE_SUCCESS = 200;
    public static final int CODE_EX_CLIENT = 500;
    public static final int CODE_INVALID_ACCESS_TOKEN = 40001;
    static final String API_GET_USER_INFO = "account/getUserInfo";
    static final String API_GET_AUTH_IMAGE = "getAuthImage?";
    static final String API_QUERY_PLATFORM = "queryPlatform?";
    static final String API_GET_CONSUME_HISTORY = "account/getConsumeHistory";
    static final String API_GET_DEVICE_INFO = "getDeviceInfo?";


}
