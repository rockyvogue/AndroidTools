package com.spt.carengine.voice.assistant.view;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.spt.carengine.log.LOG;

public class FloatView extends FrameLayout {
	private static final String TAG = "FloatView";
	protected WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
	protected WindowManager mWindowManager;

	private boolean mShown = false;
	protected Point mWindowSize = new Point();
	public FloatView(Context context) {
		super(context);
		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = mWindowManager.getDefaultDisplay();
		if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
			mWindowSize.y = display.getHeight();
			mWindowSize.x = display.getWidth();
		} else {
			display.getSize(mWindowSize);
		}
	}

	@Override
	public boolean isShown() {
		return mShown;
	}

	public void show() {
		if (mShown) {
			return;
		}
		LOG.writeMsg(this, LOG.MODE_VOICE, "show");
		mShown = true;
		mWindowManager.addView(this, mWindowParams);
	}

	public void hide() {
		if (mShown) {
		    LOG.writeMsg(this, LOG.MODE_VOICE, "hide");
			mShown = false;
			mWindowManager.removeView(this);
		}
	}
}
