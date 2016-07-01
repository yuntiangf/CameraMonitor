package com.example.cameramonitor;

/**
 * @author:hj
 * @date:2016-1-18 ����8:22:02
 */
public class CameraInfo {

	// ���������
	private String camera_name;
	// ���������
	private int camera_type;
	
	//��������к�
	private String serialNo;
	//�������֤��
	private String veryCode;
	//�����ip
	private String serverip;
	//������˿ں�
	private int serverport;
	//������û���
	private String username;
	//���������
	private String userpwd;
	
	public CameraInfo(String camera_name, int camera_type) {
		super();
		this.camera_name = camera_name;
		this.camera_type = camera_type;
	}

	//өʯ�������������
	public CameraInfo(String camera_name, int camera_type, String serialNo,
			String veryCode) {
		super();
		this.camera_name = camera_name;
		this.camera_type = camera_type;
		this.serialNo = serialNo;
		this.veryCode = veryCode;
	}

	//�������������������
	public CameraInfo(String camera_name, int camera_type, String serverip,
			int serverport, String username, String userpwd) {
		super();
		this.camera_name = camera_name;
		this.camera_type = camera_type;
		this.serverip = serverip;
		this.serverport = serverport;
		this.username = username;
		this.userpwd = userpwd;
	}

	public String getCamera_name() {
		return camera_name;
	}

	public void setCamera_name(String camera_name) {
		this.camera_name = camera_name;
	}

	public int getCamera_type() {
		return camera_type;
	}

	public void setCamera_type(int camera_type) {
		this.camera_type = camera_type;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getVeryCode() {
		return veryCode;
	}

	public void setVeryCode(String veryCode) {
		this.veryCode = veryCode;
	}

	public String getServerip() {
		return serverip;
	}

	public void setServerip(String serverip) {
		this.serverip = serverip;
	}

	public int getServerport() {
		return serverport;
	}

	public void setServerport(int serverport) {
		this.serverport = serverport;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserpwd() {
		return userpwd;
	}

	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}
	
}
