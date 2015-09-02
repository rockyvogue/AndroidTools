
package com.spt.carengine.playvideo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import com.spt.carengine.utils.VideoUtils;

import java.io.IOException;

/**
 * @author Rocky
 * @Time 2015年8月6日 下午3:19:05
 * @description HardVideoHandler
 */
public class HardVideoHandler implements VideoHandler {

    public static final String TAG = "HardVideoHandler";

    // 视频渲染类
    private SurfaceHolder mSurfaceHolder;

    // 播放视频类
    public MediaPlayer mMediaPlayer = null;

    // 视频播放地址
    private String mPlayingUrl = "";

    // 更新UI的消息机制handler
    private Handler updateMsgHandler;

    /**
     * 构造方法
     * 
     * @param mSurfaceHolder
     * @param mVideoPlayerHandler
     */
    @SuppressLint("NewApi")
    public HardVideoHandler(Context mContext, SurfaceHolder mSurfaceHolder,
            Handler mVideoPlayerHandler) {
        this.mSurfaceHolder = mSurfaceHolder;
        this.updateMsgHandler = mVideoPlayerHandler;
        this.mMediaPlayer = new MediaPlayer();
    }

    /**
     * 视频尺寸变化监听器
     */
    OnVideoSizeChangedListener onVideoSizeChangedListener = new OnVideoSizeChangedListener() {

        @SuppressLint("NewApi")
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        }
    };

    /**
     * 准备好了的监听器
     */
    OnPreparedListener mPreparedListener = new OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            mMediaPlayer.start();
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.setVolume(1.0f, 1.0f);

            VideoUtils.handlerMsgToUI(updateMsgHandler, Constant.VIDEO_PLAY);
            startPlayThread();
        }
    };

    /**
     * 视频播放完成的监听器
     */
    OnCompletionListener mCompletionListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer arg0) {
            updateMsgHandler.sendEmptyMessage(Constant.VIDEO_PLAY_COMPLETION);
        }
    };

    /**
     * 播放的错误监听器
     */
    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer mp, int what, int extra) {
            if ((what == MediaPlayer.MEDIA_ERROR_UNKNOWN)
                    && (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED)) {
                updateMsgHandler.sendEmptyMessage(Constant.VIDEO_PLAY_ERROR);
            }
            return false;
        }
    };

    /**
     * 视频相关的处理
     */
    private MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {
        public boolean onInfo(MediaPlayer mp, int what, final int extra) {
            if (what == /* MediaPlayer.MEDIA_INFO_BUFFERING_START */701) { // 开始缓冲
                updateMsgHandler
                        .sendEmptyMessage(Constant.VIDEO_PLAY_PROGRESSBAR_LOADING_VISIBLE);

            } else if (what == 702/* MediaPlayer.MEDIA_INFO_BUFFERING_END */
                    || what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {// 结束缓冲
                updateMsgHandler
                        .sendEmptyMessage(Constant.VIDEO_PLAY_PROGRESSBAR_LOADING_GONE);
            }
            return false;
        }
    };

    /**
     * 缓冲监听器
     */
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (percent >= 10) {
                updateMsgHandler
                        .sendEmptyMessage(Constant.VIDEO_PLAY_PROGRESSBAR_LOADING_GONE);

            } else {
                Message msg = updateMsgHandler
                        .obtainMessage(Constant.VIDEO_PLAY_PROGRESSBAR_LOADING_VISIBLE);
                msg.arg1 = percent;
                updateMsgHandler.sendMessage(msg);
            }
        }
    };

    /**
     * 更新进度条的方法
     */
    public void startSeek() {
        updateUIHandler.sendEmptyMessage(UPDATE_UI_MSG);
    }

    private static final int UPDATE_UI_MSG = 0;
    private Handler updateUIHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_UI_MSG:
                    if (updateMsgHandler != null) {
                        updateMsgHandler
                                .sendEmptyMessage(Constant.HANDLER_MSG_DISPLAY_CURRENT_PLAY_TIME);
                    }
                    updateUIHandler.sendEmptyMessageDelayed(UPDATE_UI_MSG, 500);
                    break;

                default:
                    break;
            }
        };
    };

    /**
     * 开始播放
     */
    @SuppressLint("NewApi")
    @Override
    public void start() {
        // mPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 设置需要播放的视频
        try {
            mMediaPlayer.setDataSource(mPlayingUrl);
            // 把视频画面输出到SurfaceView
            mMediaPlayer.setDisplay(this.mSurfaceHolder);
            mMediaPlayer
                    .setOnVideoSizeChangedListener(onVideoSizeChangedListener);
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.prepareAsync();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停播放
     */
    @Override
    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            VideoUtils.handlerMsgToUI(updateMsgHandler, Constant.VIDEO_PAUSE);
        }
    }

    /**
     * 播放
     */
    @Override
    public void play() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            VideoUtils.handlerMsgToUI(updateMsgHandler, Constant.VIDEO_PLAY);
        }
    }

    /**
     * 停止播放
     */
    @Override
    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    /**
     * 得到视频的总时间
     */
    @Override
    public long getDuration() {
        int duration = 0;
        if (mMediaPlayer != null) {
            duration = mMediaPlayer.getDuration();
        }
        return duration;
    }

    /**
     * 得到视频播放的当前时间
     */
    @Override
    public long getCurrentPosition() {
        int currentPosition = 0;
        if (mMediaPlayer != null) {
            currentPosition = mMediaPlayer.getCurrentPosition();
        }
        return currentPosition;
    }

    /**
     * 跳转到哪个时间点
     */
    @Override
    public void seekTo(int pos) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(pos);
        }
    }

    /**
     * 当前视频正在播放
     */
    @Override
    public boolean isPlaying() {
        boolean isPlaying = false;
        if (mMediaPlayer != null) {
            isPlaying = mMediaPlayer.isPlaying();
        }
        return isPlaying;
    }

    /**
     * 是否可以暂停
     */
    @Override
    public boolean canPause() {
        boolean isPause = false;
        if (mMediaPlayer != null) {
            isPause = mMediaPlayer.isPlaying();
        }
        return isPause;
    }

    /**
     * 快退
     */
    @Override
    public boolean moveSeekBackward() {
        int movePosition = (int) (getCurrentPosition() - Constant.BACKWARD_DEAFAULT_TIME);
        if (movePosition < 0) {
            movePosition = 0;
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(movePosition);
            VideoUtils
                    .handlerMsgToUI(updateMsgHandler, Constant.VIDEO_BACKWARD);
        }
        return false;
    }

    /**
     * 快进
     */
    @Override
    public boolean moveSeekForward() {
        int movePosition = (int) (getCurrentPosition() + Constant.BACKWARD_DEAFAULT_TIME);
        if (movePosition > getDuration()) {
            movePosition = (int) getDuration();
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(movePosition);
            VideoUtils.handlerMsgToUI(updateMsgHandler, Constant.VIDEO_FORWARD);
        }
        return false;
    }

    /**
     * 打开视频播放类。开始播放
     */
    @Override
    public void openVideo(String url) {
        this.mPlayingUrl = VideoUtils.getUTF8CodeInfoFromURL(url);
        start();
    }

    @Override
    public void destory() {
        updateMsgHandler = null;
        if (mMediaPlayer != null) {
            mMediaPlayer.setScreenOnWhilePlaying(false);
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        mSurfaceHolder = null;
    }

    @Override
    public void startPlayThread() {
        startSeek();
    }

}
