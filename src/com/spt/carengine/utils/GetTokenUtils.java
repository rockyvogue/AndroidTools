
package com.spt.carengine.utils;

import android.util.Log;

import com.spt.carengine.define.Define;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Rocky
 * @Time 2015年7月13日 下午5:45:43
 * @descrition 获取信鸽Token的值
 */
public class GetTokenUtils {

    /**
     * 获取token
     * 
     * @return
     */
    public static String getToken() {
        String secret = Define.XG_APP_SECRET_KEY;
        SimpleDateFormat sdf = new SimpleDateFormat("ddHHyyyyMM",
                Locale.getDefault());
        String date = sdf.format(new java.util.Date());
        String stoken = String.format("%s%s", secret, date);
        Log.d(Define.TAG, "stoken1:" + stoken);
        stoken = getMD5(stoken);
        Log.d(Define.TAG, "stoken2:" + stoken);
        return stoken;
    }

    /**
     * 将字符串MD5加密
     * 
     * @param content
     * @return
     */
    private static String getMD5(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(content.getBytes());
            return getHashString(digest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * MD5加密
     * 
     * @param digest
     * @return
     */
    private static String getHashString(MessageDigest digest) {

        StringBuilder builder = new StringBuilder();
        for (byte b : digest.digest()) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString().toLowerCase(Locale.getDefault());
    }
}
