package com.spt.carengine.fm;

import com.spt.carengine.fm.IFMRadioServiceCallback;

import android.os.IInterface;
import android.os.RemoteException;


interface IFMRadioService
{
   boolean getAudioMode();


   boolean getAudioType();


   int getBand();


   boolean getCurrentFreq();


   int getMaxFrequence();


   int getMinFrequence();


   String getRDSStationName();


    boolean getRSSI();


    int getRdsPI();


    String getRdsPS();


    int getRdsPTY();


    String getRdsRT();


    String getRdsRTPLUS();


    int getStepUnit();


    boolean getVolume();


    boolean isMute();


    boolean isRdsEnable();


    void registerCallback(IFMRadioServiceCallback paramIFMRadioServiceCallback);


    boolean scan();


    boolean seek(int paramInt);


    boolean setAudioMode(int paramInt);


    boolean setBand(int paramInt);


    boolean setMute(int paramInt);


    boolean setRSSI(int paramInt);


    boolean setRdsEnable(boolean paramBoolean, int paramInt);


    boolean setVolume(int paramInt);


    boolean stopScan();


    boolean stopSeek();


    boolean tune(int paramInt);


    void unregisterCallback(IFMRadioServiceCallback paramIFMRadioServiceCallback);

}
