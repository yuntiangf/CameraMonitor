package com.example.cameramonitor;

/**
 * @author:hj
 * @date:2016-1-18 下午8:22:02
 */
public class CameraInfo {

	// 摄像机名称
	private String camera_name;
	// 摄像机类型
	private int camera_type;
	
	//摄像机序列号
	private String serialNo;
	//摄像机验证码
	private String veryCode;
	//摄像机ip
	private String serverip;
	//摄像机端口号
	private int serverport;
	//摄像机用户名
	private String username;
	//摄像机密码
	private String userpwd;
	
	public CameraInfo(String camera_name, int camera_type) {
		super();
		this.camera_name = camera_name;
		this.camera_type = camera_type;
	}

	//萤石网络摄像机属性
	public CameraInfo(String camera_name, int camera_type, String serialNo,
			String veryCode) {
		super();
		this.camera_name = camera_name;
		this.camera_type = camera_type;
		this.serialNo = serialNo;
		this.veryCode = veryCode;
	}

	//半球形网络摄像机属性
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
