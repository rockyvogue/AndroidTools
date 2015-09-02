package com.spt.carengine.voice;
/**
 * 语音服务相关，请勿修改
 * @author Administrator
 *
 */
public interface ITalkService extends android.os.IInterface {
	
	public static abstract class Stub extends android.os.Binder implements
			com.spt.carengine.voice.ITalkService {
		private static final java.lang.String DESCRIPTOR = "com.spt.carengine.voice.ITalkService";

		/** 构建接口*/
		public Stub() {
			this.attachInterface(this, DESCRIPTOR);
		}

		/*** 生成一个内部对象ITalkService接口 */
		public static com.spt.carengine.voice.ITalkService asInterface(
				android.os.IBinder obj) {
			if ((obj == null)) {
				return null;
			}
			android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
			if (((iin != null) && (iin instanceof com.spt.carengine.voice.ITalkService))) {
				return ((com.spt.carengine.voice.ITalkService) iin);
			}
			return new com.spt.carengine.voice.ITalkService.Stub.Proxy(obj);
		}

		@Override
		public android.os.IBinder asBinder() {
			return this;
		}

		@Override
		public boolean onTransact(int code, android.os.Parcel data,
				android.os.Parcel reply, int flags)
				throws android.os.RemoteException {
			switch (code) {
			case INTERFACE_TRANSACTION: {
				reply.writeString(DESCRIPTOR);
				return true;
			}
			case TRANSACTION_startTalk: {
				data.enforceInterface(DESCRIPTOR);
				this.startTalk();
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_stopTalk: {
				data.enforceInterface(DESCRIPTOR);
				this.stopTalk();
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_cancelTalk: {
				data.enforceInterface(DESCRIPTOR);
				boolean _arg0;
				_arg0 = (0 != data.readInt());
				this.cancelTalk(_arg0);
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_putCustomText: {
				data.enforceInterface(DESCRIPTOR);
				java.lang.String _arg0;
				_arg0 = data.readString();
				this.putCustomText(_arg0);
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_setProtocal: {
				data.enforceInterface(DESCRIPTOR);
				java.lang.String _arg0;
				_arg0 = data.readString();
				this.setProtocal(_arg0);
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_setRecognizerTalkType: {
				data.enforceInterface(DESCRIPTOR);
				java.lang.String _arg0;
				_arg0 = data.readString();
				this.setRecognizerTalkType(_arg0);
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_onStart: {
				data.enforceInterface(DESCRIPTOR);
				this.onStart();
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_onStop: {
				data.enforceInterface(DESCRIPTOR);
				this.onStop();
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_playTTS: {
				data.enforceInterface(DESCRIPTOR);
				java.lang.String _arg0;
				_arg0 = data.readString();
				this.playTTS(_arg0);
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_cancelTTS: {
				data.enforceInterface(DESCRIPTOR);
				this.cancelTTS();
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_getContactName: {
				data.enforceInterface(DESCRIPTOR);
				java.lang.String _arg0;
				_arg0 = data.readString();
				java.lang.String _result = this.getContactName(_arg0);
				reply.writeNoException();
				reply.writeString(_result);
				return true;
			}
			}
			return super.onTransact(code, data, reply, flags);
		}

		private static class Proxy implements
				com.spt.carengine.voice.ITalkService {
			private android.os.IBinder mRemote;

			Proxy(android.os.IBinder remote) {
				mRemote = remote;
			}

			@Override
			public android.os.IBinder asBinder() {
				return mRemote;
			}

			public java.lang.String getInterfaceDescriptor() {
				return DESCRIPTOR;
			}

			@Override
			public void startTalk() throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					mRemote.transact(Stub.TRANSACTION_startTalk, _data, _reply,
							0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void stopTalk() throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					mRemote.transact(Stub.TRANSACTION_stopTalk, _data, _reply,
							0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void cancelTalk(boolean callback)
					throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeInt(((callback) ? (1) : (0)));
					mRemote.transact(Stub.TRANSACTION_cancelTalk, _data,
							_reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void putCustomText(java.lang.String text)
					throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeString(text);
					mRemote.transact(Stub.TRANSACTION_putCustomText, _data,
							_reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void setProtocal(java.lang.String protocal)
					throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeString(protocal);
					mRemote.transact(Stub.TRANSACTION_setProtocal, _data,
							_reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void setRecognizerTalkType(java.lang.String type)
					throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeString(type);
					mRemote.transact(Stub.TRANSACTION_setRecognizerTalkType,
							_data, _reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void onStart() throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					mRemote.transact(Stub.TRANSACTION_onStart, _data, _reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void onStop() throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					mRemote.transact(Stub.TRANSACTION_onStop, _data, _reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void playTTS(java.lang.String text)
					throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeString(text);
					mRemote.transact(Stub.TRANSACTION_playTTS, _data, _reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void cancelTTS() throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					mRemote.transact(Stub.TRANSACTION_cancelTTS, _data, _reply,
							0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public java.lang.String getContactName(java.lang.String number)
					throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				java.lang.String _result;
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeString(number);
					mRemote.transact(Stub.TRANSACTION_getContactName, _data,
							_reply, 0);
					_reply.readException();
					_result = _reply.readString();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
				return _result;
			}
		}

		static final int TRANSACTION_startTalk = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
		static final int TRANSACTION_stopTalk = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
		static final int TRANSACTION_cancelTalk = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
		static final int TRANSACTION_putCustomText = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
		static final int TRANSACTION_setProtocal = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
		static final int TRANSACTION_setRecognizerTalkType = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
		static final int TRANSACTION_onStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
		static final int TRANSACTION_onStop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
		static final int TRANSACTION_playTTS = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
		static final int TRANSACTION_cancelTTS = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
		static final int TRANSACTION_getContactName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
	}

	public void startTalk() throws android.os.RemoteException;

	public void stopTalk() throws android.os.RemoteException;

	public void cancelTalk(boolean callback) throws android.os.RemoteException;

	public void putCustomText(java.lang.String text)
			throws android.os.RemoteException;

	public void setProtocal(java.lang.String protocal)
			throws android.os.RemoteException;

	public void setRecognizerTalkType(java.lang.String type)
			throws android.os.RemoteException;

	public void onStart() throws android.os.RemoteException;

	public void onStop() throws android.os.RemoteException;

	public void playTTS(java.lang.String text)
			throws android.os.RemoteException;

	public void cancelTTS() throws android.os.RemoteException;

	public java.lang.String getContactName(java.lang.String number)
			throws android.os.RemoteException;
}
