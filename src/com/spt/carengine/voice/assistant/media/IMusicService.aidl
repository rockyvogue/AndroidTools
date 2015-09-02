package com.spt.carengine.voice.assistant.media;
import com.spt.carengine.voice.assistant.media.TrackInfo;

interface IMusicService {
	void setPlayList(in List<TrackInfo> playList);
	void play();
	void pause();
	void stop();
	void prev();
	void next();
	void skipTo(int index);
	
	boolean isPlaying();
	TrackInfo getCurrentTrack();
	int getCurrentTrackIndex();	
	List<TrackInfo> getPlayList();
	
}