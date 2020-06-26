package com.ashwashing.pro.api.bean;

import com.google.gson.annotations.SerializedName;

public class Plan {
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