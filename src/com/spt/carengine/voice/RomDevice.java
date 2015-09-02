package com.spt.carengine.voice;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class RomDevice {
	public static final String TAG = "RomDevice";
	private static final String INVALID_IMEI = "000000000000000";

	public static String getDeviceId(Context context) {
		String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

		if (deviceId == null) {
			deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		}

		return deviceId == null ? INVALID_IMEI : deviceId;
	}

	public static String getAppVersionName(Context context) {
		String versionName = "";

		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

			versionName = packageInfo.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return versionName;
	}
}
