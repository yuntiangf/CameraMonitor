package com.example.cameramonitor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import com.hikvision.netsdk.INT_PTR;
import com.hikvision.netsdk.NET_DVR_JPEGPARA;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

public class HKShowActivity extends Activity {

	//SurfaceView对象，用来显示视频
	private SjrsSurfaceView nowSjrsSurfaceView;
	// 视频向上 
	private Button btUp;
	// 视频向下 
	private Button btDown;
	// 视频向左 
	private Button btLeft;
	// 视频向右 
	private Button btRigth;
	// 视频上左 
	private Button btUpLeft;
	// 视频上右 
	private Button btUpRigth;
	// 视频下左 
	private Button btDownLeft;
	// 视频下右 
	private Button btDownRigth;
	// 光圈扩大 
	private Button btAmplification;
	// 光圈缩小 
	private Button btShrink;
	// 抓图
	private Button btCaptur;
	// 自动转动 
	private Button btAuto;
	// 录像 
	private Button btRecord;
	// 关机 
	private Button btShutDown;
	// 重启 
	private Button btReboot;
	// button点击事件
	private ButtonListener btnListener;
	//	实例化网络库SDK
	private SjrsSurfaceView mSurface;
	// 监控点信息类 
	private MonitorCameraInfo cameraInfo;
	int i = 1;
	private int flag = 0;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_show);
		findView();
		setListener();
		init();
	}

	// 组件配置
	private void findView() {
		nowSjrsSurfaceView = (SjrsSurfaceView) findViewById(R.id.video);
		
		btUp = (Button) findViewById(R.id.bt_up);
		btDown = (Button) findViewById(R.id.bt_down);
		btLeft = (Button) findViewById(R.id.bt_left);
		btRigth = (Button) findViewById(R.id.bt_rigth);
		
		btUpLeft = (Button) findViewById(R.id.bt_up_left);
		btUpRigth = (Button) findViewById(R.id.bt_up_rigth);
		btDownLeft = (Button) findViewById(R.id.bt_down_left);
		btDownRigth = (Button) findViewById(R.id.bt_down_rigth);
		
		btAmplification = (Button) findViewById(R.id.bt_amplification);
		btShrink = (Button) findViewById(R.id.bt_shrink);
		btCaptur = (Button) findViewById(R.id.bt_capture);
		btAuto = (Button) findViewById(R.id.bt_auto);
		
		btRecord = (Button) findViewById(R.id.bt_record);
		btShutDown = (Button) findViewById(R.id.bt_shutDown);
		btReboot = (Button) findViewById(R.id.bt_reboot);
		
		btnListener = new ButtonListener(); 
		
	}

	// 监听设置
	private void setListener() {
		btUp.setOnClickListener(btnListener);
		btDown.setOnClickListener(btnListener);
		btLeft.setOnClickListener(btnListener);
		btRigth.setOnClickListener(btnListener);
		
		btUp.setOnTouchListener(btnListener);
		btDown.setOnTouchListener(btnListener);
		btLeft.setOnTouchListener(btnListener);
		btRigth.setOnTouchListener(btnListener);
		
		btUpLeft.setOnClickListener(btnListener);
		btUpRigth.setOnClickListener(btnListener);
		btDownLeft.setOnClickListener(btnListener);
		btDownRigth.setOnClickListener(btnListener);
		
		btUpLeft.setOnTouchListener(btnListener);
		btUpRigth.setOnTouchListener(btnListener);
		btDownLeft.setOnTouchListener(btnListener);
		btDownRigth.setOnTouchListener(btnListener);
		
		btAmplification.setOnClickListener(btnListener);
		btShrink.setOnClickListener(btnListener);
		btCaptur.setOnClickListener(btnListener);
		btAuto.setOnClickListener(btnListener);
		
		btAmplification.setOnTouchListener(btnListener);
		btShrink.setOnTouchListener(btnListener);
		btCaptur.setOnTouchListener(btnListener);
		btAuto.setOnTouchListener(btnListener);
		
		btRecord.setOnClickListener(btnListener);
		btShutDown.setOnClickListener(btnListener);
		btReboot.setOnClickListener(btnListener);
		
		btRecord.setOnTouchListener(btnListener);
		btShutDown.setOnTouchListener(btnListener);
		btReboot.setOnTouchListener(btnListener);
	}

	//页面初始化
	private void init() {
		mSurface = new SjrsSurfaceView(HKShowActivity.this);
	}  
  
	// 显示
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		
		// 如果没有在播放的话
		if (!nowSjrsSurfaceView.playFlag) {
			// 监控点信息类
			cameraInfo = new MonitorCameraInfo();
			//224.186.114.116
//			cameraInfo.serverip = "192.168.2.88";
//			cameraInfo.serverport = 8080;
//			cameraInfo.username = "admin";
//			cameraInfo.userpwd = "12345";
			
			
			cameraInfo.serverip = intent.getStringExtra("ip");
			cameraInfo.serverport = intent.getIntExtra("port", 8080);
			cameraInfo.username = intent.getStringExtra("username");
			cameraInfo.userpwd = intent.getStringExtra("userpwd");
			
			System.out.println("-->"+cameraInfo.serverip+","+cameraInfo.serverport+","+
			cameraInfo.username+","+cameraInfo.userpwd);
			
			
			cameraInfo.channel = 2;
			cameraInfo.describe = "测试点";

			nowSjrsSurfaceView.setMonitorInfo(cameraInfo);
			// 开始实时预览
			nowSjrsSurfaceView.startPlay(); 
		}
	}

	// 暂停
	protected void onPause() {
		super.onPause();
		if (nowSjrsSurfaceView.playFlag) {
			nowSjrsSurfaceView.stopPlay(); // 停止实时预览
		}
	}

	//抓图
	private void Capture(){
		NET_DVR_JPEGPARA jpeg = new NET_DVR_JPEGPARA();
		INT_PTR a = new INT_PTR();
		System.out.println("返回长度："+a);
		byte[] num = new byte[1024*1024];
		//设置图片的分辨率
		jpeg.wPicSize = 2;
		//设置图片的质量
		jpeg.wPicQuality = 2;
		//获取当前时间
		Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		//创建文件目录
		File file = new File(cameraInfo.filepath+"/"+month+"."+day+"/"+(i++)+".jpg");
		System.out.println("fffffffffffff-->"+file);
		System.out.println("文件路径："+cameraInfo.filepath);
//		int n = 0;
		boolean is = mSurface.SjrsSurface().NET_DVR_CaptureJPEGPicture(
				cameraInfo.userId, 1, jpeg, file+"");
//		boolean is = mSurface.SjrsSurface().NET_DVR_CaptureJPEGPicture_NEW(
//				cameraInfo.userId, 1, jpeg, num, 1024*1024, a);
		System.out.println(is+"异常-->"+mSurface.SjrsSurface().NET_DVR_GetLastError());
		
		//存储本地
		BufferedOutputStream outputStream = null;
		try {
			outputStream = new BufferedOutputStream(new FileOutputStream(file));
			outputStream.write(num);
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Toast.makeText(HKShowActivity.this, 
				"截取成功，保存在SD卡的"+cameraInfo.filepath+"下", Toast.LENGTH_SHORT).show();
	}
	
	//录像
	private void Record() {
		if(flag == 0){
			boolean is = mSurface.SjrsSurface().NET_DVR_StartDVRRecord(cameraInfo.userId, 1, 0);
			System.out.println(is+"异常-->"+mSurface.SjrsSurface().NET_DVR_GetLastError());
			flag = 1;
		}else if(flag == 1){
			boolean iv = mSurface.SjrsSurface().NET_DVR_StopDVRRecord(cameraInfo.userId, 1);
			System.out.println(iv+"异常-->"+mSurface.SjrsSurface().NET_DVR_GetLastError());
			flag = 0;
		}
	}
	
	//方向按键监听      注意：此处的通道号参数 实质为：2 但必须指定为：1(主通道)才可以做控制
	public class ButtonListener implements OnTouchListener,OnClickListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.bt_up:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,21,0);
				System.out.println("向上");
				break;
			case R.id.bt_down:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,22,0);
				System.out.println("向下");
				break;
			case R.id.bt_left:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,23,0);
				System.out.println("向左");
				break;
			case R.id.bt_rigth:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,24,0);
				System.out.println("向右");
				break;
			case R.id.bt_up_left:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,25,0);
				System.out.println("上左");
				break;
			case R.id.bt_up_rigth:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,26,0);
				System.out.println("上右");
				break;
			case R.id.bt_down_left:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,27,0);
				System.out.println("下左");
				break;
			case R.id.bt_down_rigth:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,28,0);
				System.out.println("下右");
				break;
			case R.id.bt_amplification:
				boolean amplification = mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,11,0);
				System.out.println("异常："+mSurface.SjrsSurface().NET_DVR_GetLastError());
				System.out.println("焦距放大"+amplification);
				break;
			case R.id.bt_shrink:
				boolean shrink = mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,12,0);
				System.out.println("异常："+mSurface.SjrsSurface().NET_DVR_GetLastError());
				System.out.println("焦距缩小"+shrink);
				break;
			default:
				break;
			}
			return false;
		}
		

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_up:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,21,1);
				System.out.println("结束向上移动");
				break;
			case R.id.bt_down:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,22,1);
				System.out.println("结束向下移动");
				break;
			case R.id.bt_left:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,23,1);
				System.out.println("结束向左移动");
				break;
			case R.id.bt_rigth:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,24,1);
				System.out.println("结束向右移动");
				break;
			case R.id.bt_up_left:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,25,1);
				System.out.println("结束上左移动");
				break;
			case R.id.bt_up_rigth:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,26,1);
				System.out.println("结束上右移动");
				break;
			case R.id.bt_down_left:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,27,1);
				System.out.println("结束下左移动");
				break;
			case R.id.bt_down_rigth:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,28,1);
				System.out.println("结束下右移动");
				break;
			case R.id.bt_amplification:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(0,1,13,1);
				System.out.println("结束焦距放大");
				break;
			case R.id.bt_shrink:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(0,1,14,1);
				System.out.println("结束焦距缩小");
				break;
			case R.id.bt_capture:
				Capture();
				break;
			case R.id.bt_auto:
				boolean auto = mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId, 1,29,0);
				System.out.println("自动转动"+auto);
				break;
			case R.id.bt_record:
				Record();
				break;
			case R.id.bt_shutDown:
				boolean showDown = mSurface.SjrsSurface().NET_DVR_ShutDownDVR(cameraInfo.userId);
				System.out.println("异常："+mSurface.SjrsSurface().NET_DVR_GetLastError());
				System.out.println("关闭设备"+showDown);
				break;
			case R.id.bt_reboot:
				boolean reboot = mSurface.SjrsSurface().NET_DVR_RebootDVR(cameraInfo.userId);
				System.out.println("异常："+mSurface.SjrsSurface().NET_DVR_GetLastError());
				System.out.println("重启设备"+reboot);
				break;
			default:
				break;
			}
		}
	}
}