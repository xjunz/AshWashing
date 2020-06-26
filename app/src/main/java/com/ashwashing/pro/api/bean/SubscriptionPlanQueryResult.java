package com.ashwashing.pro.api.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubscriptionPlanQueryResult {

    /**
     * code : 200
     * msg : 查询成功
     * plans : [{"PlanName":"一月时长计划","orginalPrice":"15.00","spotPrice":"12.00","Duration":"30"},{"PlanName":"四月时长计划","orginalPrice":"60.00","spotPrice":"45.00","Duration":"120"},{"PlanName":"一年时长计划","orginalPrice":"180.00","spotPrice":"135.00","Duration":"365"}]
     */

    public int code;
    public String msg;
    @SerializedName("data")
    private List<Plan> plans;


    public List<Plan> getPlans() {
        return plans;
    }


    public static class Plan {
        /**
         * PlanName : 一月时长计划
         * orginalPrice : 15.00
         * spotPrice : 12.00
         * Duration : 30
         */
        private String PlanName;
        @SerializedName("OPrice")
        public float originalPrice;
        @SerializedName("SPrice")
        public float spotPrice;
        @SerializedName("Duration")
        public int durationDayUnited;
        @SerializedName("PlanType")
        public int type;

    }
}
