
package com.spt.carengine.fm;

import android.util.Log;

public class FMServer {

    private static boolean isDebug = true;
    private static String tag = FMServer.class.getSimpleName();

    // native function
    private native int nativeOpenFM();

    private native int nativeCloseFM();

    private native int nativeGetRadioInfo();

    private native int nativeSetFreq(int freq);

    private native int nativeFreqSeek(int up_down);

    private native int nativeFreqTrim(int up_down);

    private native int nativeSetStereo(int on_off);

    private native int nativeSetRegion(String region);

    private native int nativeSetSignalQuality(String grade);

    private native int nativeFmToAm();

    private native int nativeAmToFm();

    private native int fm_power_on(); // 打开FM；

    private native int fm_power_off(); // 关掉FM；

    private native int fm_search_freq(int frq, char up); // FM自动搜台，并跳到下一个台的位置；

    private native int fm_get_freq(); // FM得到当前电台的频率；

    private native int fm_set_mute(); // 设置FM静音；

    private native int fm_set_volue(int value); // 设置FM的音量大小

    private int openFM() {
        print("-openFM-");
        return 0;
    }

    private int closeFM() {
        print("-closeFM-");
        return 0;
    }

    private int getRadioInfo() {
        print("-getRadioInfo-");
        return 0;
    }

    private int setFreq(int freq) {
        print("-setFreq-");
        return 0;
    }

    private int freqSeek(int up_down) {
        print("-freqSeek-");
        return 0;
    }

    private int freqTrim(int up_down) {
        print("-freqTrim-");
        return 0;
    }

    private int setStereo(int on_off) {
        print("-setStereo-");
        return 0;
    }

    private int setRegion(String region) {
        print("-setRegion-");
        return 0;
    }

    private int setSignalQuality(String grade) {
        print("-setSignalQuality-");
        return 0;
    }

    private int fmToAm() {
        print("-fmToAm-");
        return 0;
    }

    private int amToFm() {
        print("-amToFm-");
        return 0;
    }

    static void print(Object o) {
        // System.out.println( String.valueOf(o) );
        Log.d(tag, String.valueOf(o));
    }

}
