
package com.spt.carengine.service;

import android.R.integer;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import com.spt.carengine.constant.Constant;
import com.spt.carengine.entertainment.interfaces.IPlayControl;
import com.spt.carengine.entertainment.interfaces.OnScanFinishListener;
import com.spt.carengine.entertainment.interfaces.OnPlayIndexChangeListener;
import com.spt.carengine.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * <功能描述> 播放音乐服务
 * 
 * @author ymm
 */
public class MusicService extends Service {

    private MediaPlayer mPlayer;
    private ArrayList<File> mSongList;
    private OnScanFinishListener mScanFinishListener;
    private OnPlayIndexChangeListener mOnPlayIndexChangeListener;
    private int mCurIndex;
    private int mPlayMode;



    @Override
    public void onCreate() {
        super.onCreate();

        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型

        mPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // 播放完成，根据播放模式继续播放

                switch (mPlayMode) {
                    case Constant.LIST_MODE:// 列表循环播放模式
                        listPlayMode();
                        break;
                    case Constant.ORDER_MODE:// 顺序播放模式
                        orderPlayMode();
                        break;
                    case Constant.RANDOM_MODE:// 随机播放模式
                        randomPlayMode();
                        break;
                    case Constant.SINGLE_MODE:// 单曲循环播放模式
                        siglePlayMode();
                        break;

                    default:
                        break;
                }

            }

        });

        mPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                // 缓冲
            }
        });

        mPlayer.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mPlayer.start();
            }
        });
    }

    private void siglePlayMode() {

    }

    private void randomPlayMode() {

    }

    private void orderPlayMode() {

    }

    private void listPlayMode() {

    }
    
    //读取音乐相关信息
    private class ReadMusicFromNetTask extends AsyncTask<Object, Integer, ArrayList<File>> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(ArrayList<File> result) {
            // dialog.dismiss();
            mSongList = result;
            mScanFinishListener.scanFinish(result);
        }

        @Override
        protected ArrayList<File> doInBackground(Object... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<File> container = new ArrayList<File>();
            FileUtils.scanDir(Environment.getExternalStorageDirectory(), ".mp3", container);
            return container;
        }

    }

    private void playSong(String url) {
        try {
            mPlayer.reset();
            mPlayer.setDataSource(url);
            mPlayer.prepare();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MusicBinder extends Binder implements IPlayControl {

        @Override
        public void play(String url) {
            playSong(url);
        }

        @Override
        public void pause() {
            mPlayer.pause();
        }

        @Override
        public void resume() {
            mPlayer.start();
        }

        @Override
        public ArrayList<File> getSongList() {
            return mSongList;
        }

        @Override
        public void setOnScanFinishListener(OnScanFinishListener listener) {
            mScanFinishListener = listener;
        }

        @Override
        public boolean isPlaying() {
            return mPlayer.isPlaying();
        }

        @Override
        public int getCurProgress() {
            return mPlayer.getCurrentPosition();
        }

        @Override
        public int getMax() {
            return mPlayer.getDuration();
        }

        @Override
        public void seekTo(int progress) {
            mPlayer.seekTo(progress);
        }

        @Override
        public void setonPlayIndexChangeListener(
                OnPlayIndexChangeListener listener) {
            mOnPlayIndexChangeListener = listener;
        }

        @Override
        public int getSongIndex() {
            return mCurIndex;
        }

    }

    @Override
    public IBinder onBind(Intent arg0) {

        return new MusicBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mPlayer != null) {
            // 释放相关的硬件资源
            mPlayer.release();
            mPlayer = null;
        }
    }

}
