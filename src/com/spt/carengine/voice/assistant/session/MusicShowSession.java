/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : MusicShowSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-3
 */
package com.spt.carengine.voice.assistant.session;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

import com.spt.carengine.R;
import com.spt.carengine.activity.MusicCenterActivity;
import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.assistant.MessageSender;
import com.spt.carengine.voice.assistant.media.TrackInfo;
import com.spt.carengine.voice.assistant.preference.CommandPreference;
import com.spt.carengine.voice.assistant.preference.SessionPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-3
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-3
 * @Modified:
 * 2013-9-3: 实现基本功能
 */
public class MusicShowSession extends BaseSession {
	public static final String TAG = "MusicShowSession";
	private String mMusicData = "";
	private MessageSender mMessageSender;
	String mSinger, mSong;
	/**@purpose: fix Intent overload data when play music by Leon***/
	static ArrayList<TrackInfo> mMuisicList = new ArrayList<TrackInfo>();
	
	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-3
	 * @param context
	 * @param sessionManagerHandler
	 */
	public MusicShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
		if (CommandPreference.MUSIC_CUSTOM) {
			mMessageSender = new MessageSender(context);
		}
	}
	
	/**
	 * @author: Leon
	 * @CreateDate: 2015-4-1
	 * @purpose: fix Intent overload data when play music 
	 * @return
	 */
	public static ArrayList<TrackInfo> getMusicList(){
		return mMuisicList;
	}
	
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		LOG.writeMsg(this, LOG.MODE_VOICE, "--jsonProtocol-->" + jsonProtocol);
		addQuestionViewText(mQuestion);
		JSONObject resultObject = getJSONObject(mDataObject, "result");
		JSONArray musicArray = getJsonArray(resultObject, "musicData");
//		ArrayList<TrackInfo> list = new ArrayList<TrackInfo>();
		if (CommandPreference.MUSIC_CUSTOM) {
			if (musicArray != null && musicArray.length() >= 0) {
				JSONObject item = getJSONObject(musicArray, 0);
				if (item != null) {
					mMusicData = getJsonValue(item, "title", "");
					LOG.writeMsg(this, LOG.MODE_VOICE, "mMusicData : " + mMusicData);
					Intent intent = new Intent(CommandPreference.ACTION_MUSIC_DATA);
					intent.putExtra(CommandPreference.MUSCIC_DATA, mMusicData);
					mMessageSender.sendOrderedMessage(intent, null);
				}
			} else {
				mAnswer = mContext.getString(R.string.music_search_failed);
			}
			addAnswerViewText(mAnswer);
			playTTS(mTTS);
			
		} else {
			if (musicArray != null) {
				for (int i = 0; i < musicArray.length(); i++) {
					JSONObject item = getJSONObject(musicArray, i);
					if (item != null) {
						String title = getJsonValue(item, "title", "");
						String artist = getJsonValue(item, "artist", "");
						String album = getJsonValue(item, "album", "");
						int duration = getJsonValue(item, "duration", 0);
						String image = getJsonValue(item, "imageUrl", "");
						String link = getJsonValue(item, "url", "");

						TrackInfo track = new TrackInfo();
						track.setTitle(title);
						track.setArtist(artist);
						track.setImgUrl(image);
						track.setAlbum(album);
						track.setDuration(duration);
						track.setUrl(link);
						mMuisicList.add(track);

						if (i == 0) {
							mSong = title;
							mSinger = artist;
						}
					}
				}
			}

			if (mMuisicList.size() == 0) {
				mAnswer = mContext.getString(R.string.music_search_failed);
				addAnswerViewText(mAnswer);
				LOG.writeMsg(this, LOG.MODE_VOICE, "--MusicShowSession mAnswer : " + mAnswer + "--");
				playTTS(mTTS);
			} else {
				addAnswerViewText(mAnswer);
				
				LOG.writeMsg(this, LOG.MODE_VOICE, "--MusicShowSession mAnswer : " + mAnswer + "--");
				playTTS(mTTS);

				Intent i = new Intent(mContext, MusicCenterActivity.class);
				LOG.writeMsg(this, LOG.MODE_VOICE, "MusicCenterActivity.EXTRA_DATA: " + mMuisicList.toString());
//				i.putParcelableArrayListExtra(MusicCenterActivity.EXTRA_DATA, mMuisicList);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(i);				
			}
		}

		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SHOW_HELP_VIEW);
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}

	private void notifyMediaScanner() {
		if (android.os.Build.VERSION.SDK_INT < 19) {
			mContext.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED",
				Uri.parse("file://" + Environment.getExternalStorageDirectory())));
			mContext.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse("file:///Removable")));
			mContext.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse("file:///Removable/SD")));
			mContext.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse("file:///Removable/MicroSD")));
			mContext.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse("file:///mnt/Removable/MicroSD")));
			mContext.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse("file:///mnt")));
			mContext.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse("file:///storage")));
			mContext.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse("file:///Removable")));

		} else {
			try {
				Runtime.getRuntime().exec(
					"am broadcast -a android.intent.action.MEDIA_MOUNTED -d file://"
							+ Environment.getExternalStorageDirectory());
				Runtime.getRuntime().exec("am broadcast -a android.intent.action.MEDIA_MOUNTED -d file:///Removable");
				Runtime.getRuntime().exec("am broadcast -a android.intent.action.MEDIA_MOUNTED -d file:///mnt");
				Runtime.getRuntime().exec("am broadcast -a android.intent.action.MEDIA_MOUNTED -d file:///storage");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
