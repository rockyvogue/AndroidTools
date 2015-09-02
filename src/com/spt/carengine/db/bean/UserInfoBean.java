
package com.spt.carengine.db.bean;

//用户信息结构体
public class UserInfoBean {

    public String activate = "0";
    public String token = "";
    public String imei = "";
    public String mobile = "";
    public String dversion = "";
    public String sversion = "";
    public String dtoken = "";

    public void CleanData() {
        activate = "0";
        token = "";
        imei = "";
        mobile = "";
        dversion = "";
        sversion = "";
        dtoken = "";
    }
}
