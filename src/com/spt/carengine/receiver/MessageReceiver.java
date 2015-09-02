
package com.spt.carengine.receiver;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.spt.carengine.define.Define;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 信鸽消息接收
 * 
 * @author Administrator
 */
public class MessageReceiver extends XGPushBaseReceiver {

    public static final String LogTag = "TPushReceiver";

    /**
     * 删除标签结果
     * 
     * @param context ：上下文
     * @param errorCode ：错误码
     * @param tagName ：标签名
     */
    @Override
    public void onDeleteTagResult(Context context, int errorCode, String tagName) {
        // TODO Auto-generated method stub
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"删除成功";
        } else {
            text = "\"" + tagName + "\"删除失败,错误码：" + errorCode;
        }
        Log.d(LogTag, text);
        show(context, text);
    }

    /**
     * 通知被打开触发的结果
     * 
     * @param context ：上下文
     * @param message ：被点击的通知对象
     */
    @Override
    public void onNotifactionClickedResult(Context context,
            XGPushClickedResult message) {
        // TODO Auto-generated method stub
        if (context == null || message == null) {
            return;
        }
        String text = "";
        if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
            // 通知在通知栏被点击啦。。。。。
            // APP自己处理点击的相关动作
            // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
            text = "通知被打开 :" + message;
        } else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
            // 通知被清除啦。。。。
            // APP自己处理通知被清除后的相关动作
            text = "通知被清除 :" + message;
        }

        // 获取自定义key-value
        String customContent = message.getCustomContent();
        if (customContent != null && customContent.length() != 0) {
            try {
                JSONObject obj = new JSONObject(customContent);
                // key1为前台配置的key
                if (!obj.isNull("key")) {
                    String value = obj.getString("key");
                    Log.d(LogTag, "get custom value:" + value);
                }
                // ...
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // APP自主处理的过程。。。
        Log.d(LogTag, text);
        show(context, text);
    }

    /**
     * 收到服务器消息通知
     * 
     * @param context ：上下文
     * @param notifiShowedRlt ：被展示的通知对象
     */
    @Override
    public void onNotifactionShowedResult(Context context,
            XGPushShowedResult notifiShowedRlt) {
        // TODO Auto-generated method stub
        if (context == null || notifiShowedRlt == null) {
            return;
        }
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        String Content = notifiShowedRlt.getContent();

        if (Content.isEmpty()) {
            Log.e(LogTag, "内容为空");
            return;
        }

        String[] queryStringSplit = Content.split("&");
        Map<String, String> queryStringMap = new HashMap<String, String>(
                queryStringSplit.length);
        String[] queryStringParam;
        for (String qs : queryStringSplit) {
            queryStringParam = qs.split("=");
            queryStringMap.put(queryStringParam[0], queryStringParam[1]);
        }

        if (queryStringMap.isEmpty()) {
            Log.e(Define.TAG, "收到空数据");
            return;
        }

        Intent intent = new Intent();
        if (queryStringMap.get("cmd").equals(
                Define.API_FUNC_SERVER_TO_CAR_PHOTO)) {// 照片
            intent.setAction(Define.ACTION_PHOTO_UPLOAD);
        } else if (queryStringMap.get("cmd").equals(
                Define.API_FUNC_SERVER_TO_CAR_VIDEO)) {// 视频
            intent.setAction(Define.ACTION_VIDEO_UPLOAD);
        } else if (queryStringMap.get("cmd").equals(
                Define.API_FUNC_SERVER_TO_CAR_LOCATION)) {// 位置
            intent.setAction(Define.ACTION_GPS_UPLOAD);
        } else if (queryStringMap.get("cmd").equals(
                Define.API_FUNC_SERVER_TO_CAR_SETTINGS)) {// 设置设备相关参数

            intent.setAction(Define.ACTION_DEVICE_SETTINGS);
        }
        String sToken = queryStringMap.get("token");
        if (!sToken.isEmpty()) {
            intent.putExtra("token", sToken);
        }

        context.sendOrderedBroadcast(intent, null);

        Log.e(Define.TAG, "通知被展示 ， " + Content);
    }

    /**
     * 注册结果
     * 
     * @param context ：上下文
     * @param errorCode ：错误码
     * @param message ：注册结果对象
     */
    @Override
    public void onRegisterResult(Context context, int errorCode,
            XGPushRegisterResult message) {
        // TODO Auto-generated method stub
        if (context == null || message == null) {
            return;
        }
        // String text = "";
        // if (errorCode == XGPushBaseReceiver.SUCCESS) {
        // text = message + "注册成功";
        // // 在这里拿token
        // // String token = message.getToken();
        //
        // } else {
        // text = message + "注册失败，错误码：" + errorCode;
        // }

        Intent intent = new Intent();
        intent.setAction(Define.ACTION_XG_REGSTATE);
        intent.putExtra("data", errorCode == XGPushBaseReceiver.SUCCESS);
        context.sendOrderedBroadcast(intent, null);

    }

    /**
     * 设置标签结果
     * 
     * @param context ：上下文
     * @param errorCode ：错误码
     * @param tagName ：标签名
     */
    @Override
    public void onSetTagResult(Context context, int errorCode, String tagName) {
        // TODO Auto-generated method stub
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"设置成功";
        } else {
            text = "\"" + tagName + "\"设置失败,错误码：" + errorCode;
        }
        Log.d(LogTag, text);
        show(context, text);
    }

    /**
     * 收到消息
     * 
     * @param context ：上下文
     * @param message ：消息对象
     */
    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {
        // TODO Auto-generated method stub
        String text = "收到消息:" + message.toString();
        // 获取消息内容
        String Content = message.getContent();
        if (Content != null && Content.length() != 0) {
            if (Content.equals("getPhoto")) {// 照片
                Intent intent = new Intent();
                intent.setAction(Define.ACTION_PHOTO_UPLOAD);
                context.sendOrderedBroadcast(intent, null);

            } else if (Content.equals("getVideo")) {// 视频
                Intent intent = new Intent();
                intent.setAction(Define.ACTION_VIDEO_UPLOAD);
                context.sendOrderedBroadcast(intent, null);
            } else if (Content.equals("getLocation")) {// 位置
                Intent intent = new Intent();
                intent.setAction(Define.ACTION_GPS_UPLOAD);
                context.sendOrderedBroadcast(intent, null);
            }
            Log.d(LogTag, Content);
        }
        show(context, text);
    }

    /***
     * 反注册结果
     * 
     * @param context ：上下文
     * @param errorCode ：错误码
     */
    @Override
    public void onUnregisterResult(Context context, int errorCode) {
        // TODO Auto-generated method stub
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "反注册成功";
        } else {
            text = "反注册失败" + errorCode;
        }

        Log.d(LogTag, text);
        show(context, text);
    }

    private void show(Context context, String text) {
        // Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

        // Log.d(LogTag, "信鸽："+text);
    }

}
