package com.example.cameramonitor;

import java.io.File;

import android.os.Environment;

//��ص���Ϣ��
public class MonitorCameraInfo {
	public String serverip = "";
	public int serverport = 0;
	public String username = "";
	public String userpwd = "";
	public int channel = 0;
	public String describe = "";
	
	//��¼�ʺ�id 
	public int userId = 0;
	//���ŷ���ֵ 
	public int playNum = 0;
	//ץͼ���·�� 
	public String filepath = getSDRootPath()+"/HCNetSDK/";
	
	public MonitorCameraInfo() {}
	
	//���SD����·��
	public static String getSDRootPath() {
		File sdDir = null;
		// �ж�
		boolean sdCardExist = hasSdcard();
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
			return sdDir.toString();
		} else {
			return null;
		}
	}
	//�Ƿ����Sd card  true:���ڣ�false:������
	public static boolean hasSdcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	
}
