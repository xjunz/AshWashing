package com.ashwashing.pro.api.bean;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

public class UserQueryInfo {

    /**
     * data : {"PrjName":"无锡职业技术学院","TelPhone":15685167152,"alipay_user_id":null,"loginCode":"596337","AccMoney":19050,"GivenAccMoney":0,"AccStatusID":0,"GroupID":2,"tags":"alLyrelease,72","agreement_no":null,"alias":"15685167152","AccID":1447,"PrjID":72}
     * error_code : 0
     * message : 登录成功
     */
    public static int CODE_SUCCESS = 0;
    public static int CODE_UNBIND = 3;
    public static int CODE_FAIL = -1;
    private UserInfo data;
    @SerializedName("error_code")
    public int errorCode;
    public String message;

    public UserInfo getData() {
        return data;
    }


    public static class UserInfo {
        /**
         * PrjName : 无锡职业技术学院
         * TelPhone : 15685167152
         * alipay_user_id : null
         * loginCode : 596337
         * AccMoney : 19050
         * GivenAccMoney : 0
         * AccStatusID : 0
         * GroupID : 2
         * tags : alLyrelease,72
         * agreement_no : null
         * alias : 15685167152
         * AccID : 1447
         * PrjID : 72
         */

        public String PrjName;
        public long TelPhone;
        public String loginCode;
        public int AccMoney;
        public int GivenAccMoney;
        public int AccStatusID;
        public int GroupID;
        public String tags;
        public String alias;
        public int AccID;
        public int PrjID;

        @NotNull
        @Override
        public String toString() {
            return "AccountInfo{PrjName='" +
                    this.PrjName +
                    '\'' +
                    ", TelPhone=" +
                    this.TelPhone +
                    ", loginCode='" +
                    this.loginCode +
                    '\'' +
                    ", AccMoney=" +
                    this.AccMoney +
                    ", alias='" +
                    this.alias +
                    '\'' +
                    ", GivenAccMoney=" +
                    this.GivenAccMoney +
                    ", AccStatusID=" +
                    this.AccStatusID +
                    ", AccID=" +
                    this.AccID +
                    ", tags='" +
                    this.tags +
                    '\'' +
                    ", PrjID=" +
                    this.PrjID +
                    ", GroupID=" +
                    this.GroupID +
                    '}';
        }


    }


}
