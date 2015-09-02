
package com.spt.carengine.entertainment.bean;

import android.R.string;

/**
 * <功能描述> 歌曲详情
 * 
 * @author ymm
 */
public class MusicInfoBean {

    private String musicUrl;
    private String singer;
    private string songTime;
    private String special;// 专辑

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public string getSongTime() {
        return songTime;
    }

    public void setSongTime(string songTime) {
        this.songTime = songTime;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

}
