/**
 * Copyright (c) 2012-2012 Mango(Shanghai) Co.Ltd. All right reserved.
 * @FileName : ISessionComponent.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2012-11-10
 */
package com.spt.carengine.voice.assistant.view;

/**
 * 
 * <功能描述> 会话视图接口
 * @author  Administrator
 */
public interface ISessionView {

	public boolean isTemporary();

	public void release();
}
