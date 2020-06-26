package com.ashwashing.pro.api.bean;

public class OrderInfo {
    /**
     * created_at : 2019-06-16 22:02:28
     * updated_at : 2019-06-16 22:02:28
     * pay_status : 未支付
     * id : 5
     * order_id : undefined_20190616220227
     * order_type : alipay
     * order_price : 12.00
     * order_name : 一月时长计划
     * extension : 测试
     * redirect_url : http://s19.natfrp.org:12935/api/account/paymentSuccess
     * qr_url : alipays://platformapi/startapp?appId=20000691&url=http%3A%2F%2F120.77.245.8%3A12345%2Falipay.html%3Fu%3D2088012074949282%26a%3D11.90
     * qr_price : 11.90
     */

    private String created_at;
    public long local_create_timestamp;
    private String updated_at;
    private String pay_status;
    private int id;
    public String order_id;
    private String order_type;
    public float order_price;
    private String order_name;
    private String extension;
    private String redirect_url;
    public String qr_url;
    public float qr_price;

}
