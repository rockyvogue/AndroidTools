
package com.spt.carengine.define;


/**
 * 常量定义
 * 
 * @author Administrator
 */
public interface EDogCmd {

    public final String TAG = "wujie";

	public final int BT_CMD_LEN 	= 	250;
	
	/******命令头************/
	public final int RADAR_COMMAND_HEAD      		=	0x55;
	
	/******主控芯片跟语音芯片握手信息************/
	public final int RADAR_CONFIRM_MESSAGE      	=	0xAA;
	/******Laser************/
	public final int RADAR_LASER_ENCODING      		=	0x01;
	
	/******K Band************/
	public final int RADAR_K_BAND_LEVEL_1      		=	0x0A;
	public final int RADAR_K_BAND_LEVEL_2      		=	0x4A;
	public final int RADAR_K_BAND_LEVEL_3      		=	0xCA;
	
	/******X Band************/
	public final int RADAR_X_BAND_LEVEL_1      		=	0x02;
	public final int RADAR_X_BAND_LEVEL_2      		=	0x42;
	public final int RADAR_X_BAND_LEVEL_3      		=	0xC2;	
	
	
	/******Ka Band************/
	public final int RADAR_KA_BAND_LEVEL_1      	=	0x1A;
	public final int RADAR_KA_BAND_LEVEL_2      	=	0x5A;
	public final int RADAR_KA_BAND_LEVEL_3      	=	0xDA;		

}	
	