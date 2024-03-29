package com.spt.carengine.fragment;


import android.app.Fragment;
import android.os.Bundle;

import com.spt.carengine.interfaces.BackHandledInterface;

public abstract class BackHandledFragment extends Fragment {
	protected BackHandledInterface mBackHandledInterface;

	/**
	 * 所有继承BackHandledFragment的子类都将在这个方法中实现物理Back键按下后的逻辑
	 */
	public abstract boolean onBackPressed();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!(getActivity() instanceof BackHandledInterface)) {
			throw new ClassCastException(
					"Hosting Activity must implement BackHandledInterface");
		} else {
			this.mBackHandledInterface = (BackHandledInterface) getActivity();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		// 告诉FragmentActivity，当前Fragment在栈顶
		mBackHandledInterface.setSelectedFragment(this);
	}

}