package com.spt.carengine.mainservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.spt.carengine.MyApplication;
import com.spt.carengine.define.Define;
import com.spt.carengine.define.EDogCmd;
import com.spt.carengine.port.SerialPort;
import com.spt.carengine.voice.assistant.talk.TalkService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.InvalidParameterException;

public class EDogService extends Service {

	private Thread m_open_thread = null;
	private Thread m_recv_thread = null;
	
	private boolean m_bPortOpen = false;// 串口是否打开
	private long nLastRevSignal = 0;// 最后一次收到握手信号时间
	private long nLastSpeak = 0;// 最后一次语音播报
	

	protected MyApplication mApplication;
	protected SerialPort mSerialPort;
	private InputStream mInputStream;
	


	@Override
	public IBinder onBind(Intent intent) {
		println("-onBind-" + this);
		return null;
	}

	@Override
	public void onCreate() {
		println("-onCreate-EDOG--------" + this);
		super.onCreate();

		mApplication = (MyApplication) getApplication();

		m_open_thread = new Thread(new Runnable() {

			long nLastOpen = System.currentTimeMillis();// 最后一次发送时间

			@Override
			public void run() {
				while (true) {
					// 串口未打开
					if (m_bPortOpen == false
							&& System.currentTimeMillis() - nLastOpen >= 2000) {
						nLastOpen = System.currentTimeMillis();
						Sleep(200);
						tryOpenSerialPort();// 打开串口
					}
					
					if(System.currentTimeMillis() - nLastRevSignal >= 6000){//雷达是否正常
						sendBdcast("请检测雷达是否正常运行");
//						mApplication.mTTSPlayer.play("请检测雷达是否正常运行");			
						Sleep(10000);
					}
					
					Sleep(350);
				}
			}

		});

		m_recv_thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				// 定义一个包的最大长度
				int maxLength = 2048;
				byte[] buffer = new byte[maxLength];
				// 每次收到实际长度
				int available = 0;
				// 当前已经收到包的总长度
				int currentLength = 0;
				/**
				 * 协议格式 Message type Parameter Length Parameters 1 byte 1 byte N
				 * */
				// 协议头长度1个字节（消息类型1字节）
				int headerLength = 1;

				while (true) {

					if (m_bPortOpen == false) {// 串口未打开
						Sleep(200);
						continue;
					}

					try {
						available = mInputStream.available();
						if (available > 0) {
							// 防止超出数组最大长度导致溢出
							if (available > maxLength - currentLength) {
								available = maxLength - currentLength;
							}
							mInputStream.read(buffer, currentLength, available);
							currentLength += available;
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

					int cursor = 0;

					// 如果当前收到包大于头的长度，则解析当前包
					while (currentLength >= headerLength) {

//						println("-收到数据:" + toHexString(buffer, currentLength));

						int nCmd = buffer[cursor] & 0xff;
						// 取到头部第一个字节
						if (nCmd != EDogCmd.RADAR_COMMAND_HEAD) {
							--currentLength;
							++cursor;
							continue;
						}

						int contentLenght = 1;// 55AA
						// 如果内容包的长度大于最大内容长度或者小于等于0，则说明这个包有问题，丢弃
						if (contentLenght > maxLength - headerLength) {
							currentLength = 0;
							break;
						}
						// 如果当前获取到长度小于整个包的长度，则跳出循环等待继续接收数据
						int factPackLen = 2;// contentLenght + headerLength;
						if (currentLength < contentLenght + headerLength) {
							break;
						}
						// 一个完整包即产生
						// proceOnePacket(buffer,i,factPackLen);
						onDataReceived(buffer, cursor, factPackLen);
						currentLength -= factPackLen;
						cursor += factPackLen;
					}
					// 残留字节移到缓冲区首
					if (currentLength > 0 && cursor > 0) {
						System.arraycopy(buffer, cursor, buffer, 0,
								currentLength);
					}

					Sleep(2000);
				}
			}

		});

		m_open_thread.start();
		m_recv_thread.start();

	}


	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d(Define.TAG, "edog onStartCommand() executed");
	}

	/**
	 * 收到整包数据
	 * 
	 * @param buffer
	 * @param index
	 * @param packlen
	 */
	protected void onDataReceived(final byte[] buffer, final int index,
			final int packlen) {
		// System.out.println("收到信息");
		byte[] buf = new byte[packlen];
		System.arraycopy(buffer, index, buf, 0, packlen);

		AnalyData(buf, packlen);
	}

	private byte[] getBytes(char[] chars) {
		Charset cs = Charset.forName("UTF-8");
		CharBuffer cb = CharBuffer.allocate(chars.length);
		cb.put(chars);
		cb.flip();
		ByteBuffer bb = cs.encode(cb);

		return bb.array();

	}

	/**
	 * 解析数据
	 * 
	 * @param buf
	 *            数据
	 * @param packlen
	 *            数据长度
	 */
	private void AnalyData(final byte[] buf, int packlen) {
		// TODO Auto-generated method stub
		if (buf == null || packlen < 2)
			return;
		println("-完整数据包:" + toHexString(buf, packlen));

		int nCmd = buf[1] & 0xff;
		// 数据格式：消息类型,参数长度,参数
		if (nCmd == EDogCmd.RADAR_CONFIRM_MESSAGE) {// 芯片握手信息
			nLastRevSignal = System.currentTimeMillis();
		} else if (nCmd == EDogCmd.RADAR_LASER_ENCODING) {// Laser
			if(System.currentTimeMillis() - nLastSpeak > 10000){
				nLastSpeak = System.currentTimeMillis();
				sendBdcast("检测到雷射枪");
//				mApplication.mTTSPlayer.stop();
//				mApplication.mTTSPlayer.play("检测到激光测速枪");	
			}	
		} else if (nCmd == EDogCmd.RADAR_K_BAND_LEVEL_1) {// K Band
			if(System.currentTimeMillis() - nLastSpeak > 10000){
				nLastSpeak = System.currentTimeMillis();
				sendBdcast("检测到K波段测速雷达");
//				mApplication.mTTSPlayer.stop();
//				mApplication.mTTSPlayer.play("检测到K波段测速雷达");	
			}
		} else if (nCmd == EDogCmd.RADAR_K_BAND_LEVEL_2) {// K Band
			if(System.currentTimeMillis() - nLastSpeak > 10000){
				nLastSpeak = System.currentTimeMillis();
				sendBdcast("检测到K波段测速雷达");
//				mApplication.mTTSPlayer.stop();
//				mApplication.mTTSPlayer.play("检测到K波段测速雷达");	
			}
		} else if (nCmd == EDogCmd.RADAR_K_BAND_LEVEL_3) {// K Band
			if(System.currentTimeMillis() - nLastSpeak > 10000){
				nLastSpeak = System.currentTimeMillis();
				sendBdcast("检测到K波段测速雷达");
//				mApplication.mTTSPlayer.stop();
//				mApplication.mTTSPlayer.play("检测到K波段测速雷达");	
			}
		} else if (nCmd == EDogCmd.RADAR_X_BAND_LEVEL_1) {// X Band
			if(System.currentTimeMillis() - nLastSpeak > 10000){
				nLastSpeak = System.currentTimeMillis();
				sendBdcast("检测到X波段测速雷达");
//				mApplication.mTTSPlayer.stop();
//				mApplication.mTTSPlayer.play("检测到X波段测速雷达");	
			}
		} else if (nCmd == EDogCmd.RADAR_X_BAND_LEVEL_2) {// X Band
			if(System.currentTimeMillis() - nLastSpeak > 10000){
				nLastSpeak = System.currentTimeMillis();
				sendBdcast("检测到X波段测速雷达");
//				mApplication.mTTSPlayer.stop();
//				mApplication.mTTSPlayer.play("检测到X波段测速雷达");	
			}
		} else if (nCmd == EDogCmd.RADAR_X_BAND_LEVEL_3) {// X Band
			if(System.currentTimeMillis() - nLastSpeak > 10000){
				nLastSpeak = System.currentTimeMillis();
				sendBdcast("检测到X波段测速雷达");
//				mApplication.mTTSPlayer.stop();
//				mApplication.mTTSPlayer.play("检测到X波段测速雷达");	
			}
		} else if (nCmd == EDogCmd.RADAR_KA_BAND_LEVEL_1) {// KA Band
			if(System.currentTimeMillis() - nLastSpeak > 10000){
				nLastSpeak = System.currentTimeMillis();
				sendBdcast("检测到KA波段测速雷达");
//				mApplication.mTTSPlayer.stop();
//				mApplication.mTTSPlayer.play("检测到KA波段测速雷达");	
			}
		} else if (nCmd == EDogCmd.RADAR_KA_BAND_LEVEL_2) {// KA Band
			if(System.currentTimeMillis() - nLastSpeak > 10000){
				nLastSpeak = System.currentTimeMillis();
				sendBdcast("检测到KA波段测速雷达");
//				mApplication.mTTSPlayer.stop();
//				mApplication.mTTSPlayer.play("检测到KA波段测速雷达");	
			}
		} else if (nCmd == EDogCmd.RADAR_KA_BAND_LEVEL_3) {// KA Band
			if(System.currentTimeMillis() - nLastSpeak > 10000){
				nLastSpeak = System.currentTimeMillis();
				sendBdcast("检测到KA波段测速雷达");
//				mApplication.mTTSPlayer.stop();
//				mApplication.mTTSPlayer.play("检测到KA波段测速雷达");	
			}
		} else {
			println("-未识别的数据包:" + toHexString(buf, packlen));
		}

	}
	
	

	private void Sleep(long time) {

		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		println("-onDestroy-");
		super.onDestroy();
	}

	private void tryOpenSerialPort() {
		println("-开始打开电子狗串口-");
		try {
			mSerialPort = mApplication.getEDogSerialPort();
			if (mSerialPort != null) {
				m_bPortOpen = true;
				println("-电子狗串口打开成功-");

				mInputStream = mSerialPort.getInputStream();
				println("-mInputStream-" + mInputStream);
			} else {
				m_bPortOpen = false;
				println("-电子狗串口串口打开失败-");
			}

			return;
		} catch (SecurityException e) {

			e.printStackTrace();
			// DisplayError("R.string.error_security");
		} catch (IOException e) {
			e.printStackTrace();
			// DisplayError("R.string.error_unknown");
		} catch (InvalidParameterException e) {
			e.printStackTrace();
			// DisplayError("R.string.error_configuration");
		}

		m_bPortOpen = false;

		SystemClock.sleep(1000);
	}



	private void closeSerial() {
		try {
			mSerialPort.close();
//			mOutputStream.close();
			mInputStream.close();
			mSerialPort = null;
//			mOutputStream = null;
			mInputStream = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String toHexString(byte[] data) {
		return toHexString(data, data.length);
	}

	private static String toHexString(byte[] data, int bytes) {
		String s = "";
		for (int i = 0; i < bytes; ++i) {
			s += String.format("%02X ", data[i]);
		}
		return s;
	}

	private static String toHexStr(byte[] data, int bytes) {
		String s = "";
		for (int i = 0; i < bytes; ++i) {
			s += String.format("%02X", data[i]);
		}
		return s;
	}

	private static String toString(byte[] data) {
		return toString(data, data.length);
	}

	private static String toString(byte[] data, int bytes) {
		String s = "";
		for (int i = 0; i < bytes; ++i) {
			s += (char) data[i];
		}
		return s;
	}

	private static void println(Object o) {
		// System.out.println( String.valueOf(o) );
		Log.e(Define.TAG, String.valueOf(o));
	}
	
	
	private void sendBdcast(String sData) {
		Intent intent = new Intent();
		intent.setAction(TalkService.TTS_TEXT_ON_VOCIE_PLAY);
		intent.putExtra("text", sData);
		sendBroadcast(intent, null);
	}
	
}
