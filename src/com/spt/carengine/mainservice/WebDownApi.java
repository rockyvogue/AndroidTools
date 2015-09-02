
package com.spt.carengine.mainservice;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.spt.carengine.MyApplication;
import com.spt.carengine.db.Provider;
import com.spt.carengine.define.Define;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

//解析从服务器下发的数据
public class WebDownApi {

    private int nRetCode = -1;// 返回码。执行状态0失败1成功
    private String sRetDesc = "";// 执行结果描述

    private Context m_Context = null;

    public WebDownApi(MyApplication context) {
        m_Context = context.getApplicationContext();
    }

    public int GetRetCode() {
        return nRetCode;
    }

    public String GetRetString() {
        return sRetDesc;
    }

    public synchronized JSONObject AnalyData(int cmdType, String sAnalyData,
            Map<String, String> params) {
        if (sAnalyData == null || sAnalyData.length() == 0) {
            return null;
        }
        JSONObject userJson = null;
        try {
            userJson = new JSONObject(sAnalyData);

            if (HasMustParam(userJson) == false) {
                sRetDesc = "请求到的数据缺少必填项";
                nRetCode = -1;
                return null;
            }
            if (userJson.has("msg")) {
                sRetDesc = userJson.getString("msg");
            } else {
                sRetDesc = "";
            }

            if (userJson.has("status")) {
                nRetCode = userJson.getInt("status");
                if (nRetCode == 0) {
                    nRetCode = 1;
                    sRetDesc = "注册成功啊";
                }
            } else {
                nRetCode = -1;
            }

            SavRetValue(cmdType, params);

            return userJson;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存请求网站返回值
     * 
     * @param cmdType
     * @param params
     * @throws Exception
     */
    private void SavRetValue(int cmdType, Map<String, String> params)
            throws Exception {

        if (cmdType == Define.UP_WEB_API.API_ACTIVATE) {// 激活信息保存数据库
            if (nRetCode == 1) {

                if (params.containsKey("token") && params.containsKey("imei")
                        && params.containsKey("mobile")
                        && params.containsKey("dversion")
                        && params.containsKey("sversion")
                        && params.containsKey("dtoken")) {

                    ContentValues values = new ContentValues();
                    values.clear();

                    values.put("activate", "1");
                    values.put("token", params.get("token").toString());
                    values.put("imei", params.get("imei").toString());
                    values.put("mobile", params.get("mobile").toString());
                    values.put("dversion", params.get("dversion").toString());
                    values.put("sversion", params.get("sversion").toString());
                    values.put("dtoken", params.get("dtoken").toString());

                    int ret = m_Context.getContentResolver().update(
                            Provider.UserInfo.CONTENT_URI, values, "_id=1",
                            null);

                } else {
                    Log.e(Define.TAG, "写激活数据失败");
                    return;
                }
            } else {

            }
        } else if (cmdType == Define.UP_WEB_API.API_UPLOAD_PHOTO) {
            if (nRetCode == 1) {
                // 解析数据
            }
        } else if (cmdType == Define.UP_WEB_API.API_UPLOAD_VIDEO) {
            if (nRetCode == 1) {
                // 解析数据
            }
        } else if (cmdType == Define.UP_WEB_API.API_UPLOAD_LOCATION) {
            if (nRetCode == 1) {
                // 解析数据
            }
        }

    }

    private void ParamHideNear(JSONObject userJson) {
        // TODO Auto-generated method stub

    }

    // 终端配置信息
    private void ParamCarIniInfo(JSONObject userJson) {

    }

    /***
     * 返回必填项是否存在
     * 
     * @param json
     * @return 返回值是否正确
     */
    private boolean HasMustParam(JSONObject json) {
        if (json == null) {
            return false;
        }
        if (json.has("status") == false // 状态
                || json.has("msg") == false) {// 状态代码描述
            return false;
        }
        return true;
    }

}
