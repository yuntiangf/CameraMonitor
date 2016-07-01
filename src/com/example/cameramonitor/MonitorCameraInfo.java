package com.example.cameramonitor;

import java.io.File;

import android.os.Environment;

//监控点信息类
public class MonitorCameraInfo {
	public String serverip = "";
	public int serverport = 0;
	public String username = "";
	public String userpwd = "";
	public int channel = 0;
	public String describe = "";
	
	//登录帐号id 
	public int userId = 0;
	//播放返回值 
	public int playNum = 0;
	//抓图存放路劲 
	public String filepath = getSDRootPath()+"/HCNetSDK/";
	
	public MonitorCameraInfo() {}
	
	//获得SD卡根路径
	public static String getSDRootPath() {
		File sdDir = null;
		// 判断
		boolean sdCardExist = hasSdcard();
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
			return sdDir.toString();
		} else {
			return null;
		}
	}
	//是否存在Sd card  true:存在；false:不存在
	public static boolean hasSdcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	
}
