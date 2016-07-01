package com.example.cameramonitor;

import org.MediaPlayer.PlayM4.Player;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_CLIENTINFO;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.RealPlayCallBack;

/**
 * SurfaceView用来播放视频并显示
 * 在要显示视频的SurfaceView对象创建完成后（即surfaceCreated()方法被触发）再连接服务器
 * 进行实时预览，否则在实时预览时可能会出现SurfaceView尚未完全加载成功，导致调调数据显示异常
 */
public class SjrsSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	
	private HCNetSDK videoCtr;    					//网络库sdk
	private Player myPlayer = null;  				//播放库sdk
	public int playPort = -1;   					//播放端口
	public boolean playFlag = false;  				//播放标志
	public MonitorCameraInfo cameraInfo = null;  	//监控点信息
	
	private SurfaceHolder holder = null;
	
	public SjrsSurfaceView(Context paramContext) {
		super(paramContext);
		initSurfaceView();
	}

	public SjrsSurfaceView(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		initSurfaceView();
	}

	public SjrsSurfaceView(Context paramContext,
			AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet);
		initSurfaceView();
	}

	private void initSurfaceView() {
		getHolder().addCallback(this);
	}

	public HCNetSDK SjrsSurface(){
		//实例化海康威视android sdk
		if(videoCtr == null){
			videoCtr = HCNetSDK.getInstance();
		}
		return videoCtr;
	}
	
	public boolean onDown(MotionEvent paramMotionEvent) {
		return false;
	}

	public boolean onFling(MotionEvent paramMotionEvent1,
			MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
		return false;
	}

	public void onLongPress(MotionEvent paramMotionEvent) {
	}

	public boolean onScroll(MotionEvent paramMotionEvent1,
			MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
		return false;
	}

	public void onShowPress(MotionEvent paramMotionEvent) {
	}

	public boolean onSingleTapUp(MotionEvent paramMotionEvent) {
		return false;
	}

	public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1,
			int paramInt2, int paramInt3) {
	}

	public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {
		holder = this.getHolder();
	}

	public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {}

	public void setMonitorInfo(MonitorCameraInfo setMonitorInfo) {
		this.cameraInfo = setMonitorInfo;
	}

	//flag 1/暂停 0/恢复
	public void pausePaly(int flag) {
		myPlayer.pause(playPort, flag);
	}

	// 停止播放&释放资源
	public void stopPlay() {
		try {
			playFlag = false;
			videoCtr.NET_DVR_StopRealPlay(playPort);
			videoCtr.NET_DVR_Logout_V30(cameraInfo.userId);
			cameraInfo.userId = -1;
			videoCtr.NET_DVR_Cleanup();

			//ֹͣ停止播放后释放资源
			if (myPlayer != null) {
				myPlayer.stop(playPort);
				myPlayer.closeStream(playPort);
				myPlayer.freePort(playPort);
				playPort = -1;
				//使用绘图缓存后释放资源
				destroyDrawingCache();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//暂不做任何操作
		}
	}

	//开始实时预览 
	public void startPlay() {
		try {
			//实例化播放API
			myPlayer = Player.getInstance();
			SjrsSurface();
			//初始化海康威视android sdk
			videoCtr.NET_DVR_Init();  
			//设置错误回掉函数
			videoCtr.NET_DVR_SetExceptionCallBack(mExceptionCallBack);  
			//设置连接超时时长
			videoCtr.NET_DVR_SetConnectTime(60000);
			//获取空闲播放端口
			playPort = myPlayer.getPort();  
			NET_DVR_DEVICEINFO_V30 deviceInfo = new NET_DVR_DEVICEINFO_V30();
			//登录服务器
			cameraInfo.userId = videoCtr.NET_DVR_Login_V30(cameraInfo.serverip,
					cameraInfo.serverport, cameraInfo.username,
					cameraInfo.userpwd, deviceInfo);

			System.out.println("下面是设备信息************************");
			System.out.println("userId=" + cameraInfo.userId);
			System.out.println("通道开始=" + deviceInfo.byStartChan);
			System.out.println("通道个数=" + deviceInfo.byChanNum);
			System.out.println("设备类型=" + deviceInfo.byDVRType);
			System.out.println("ip通道个数=" + deviceInfo.byIPChanNum);

			byte[] sbbyte = deviceInfo.sSerialNumber;
			String sNo = "";  
			for (int i = 0; i < sbbyte.length; i++) {
				sNo += String.valueOf(sbbyte[i]);
			}

			System.out.println("设备序列号=" + sNo);
			System.out.println("************************");
   
			// 参数结构体
			NET_DVR_CLIENTINFO clientInfo = new NET_DVR_CLIENTINFO();
			// 浏览通道号
			clientInfo.lChannel = cameraInfo.channel;
			// 子码流（保证图像连续性）tcp连接方式，如果要保证图像清晰度，可选用主码流 
			clientInfo.lLinkMode = 0x80000000; 
			// 多播地址 需要多播时配置 
			clientInfo.sMultiCastIP = null;
			// 启动实时预览	mRealDataCallback即为数据回传回掉函数 
			cameraInfo.playNum = videoCtr.NET_DVR_RealPlay_V30(cameraInfo.userId, 
					clientInfo,mRealPlayCallBack, false);
			
			System.out.println("playFlags=" + cameraInfo.playNum);
			System.out.println("GetLastError="+ videoCtr.NET_DVR_GetLastError());

		} catch (Exception e) {
			e.printStackTrace();
			//  释放资源
			stopPlay();
		}
	}
	
	// 异常回掉函数
	private ExceptionCallBack mExceptionCallBack = new ExceptionCallBack() {

		public void fExceptionCallBack(int arg0, int arg1, int arg2) {
			System.out.println("异常回掉函数运行！");
		}
	};

	private RealPlayCallBack mRealPlayCallBack = new RealPlayCallBack() {
		@Override
		public void fRealDataCallBack(int lRealHandle, int dataType,
				byte[] paramArrayOfByte, int byteLen) {
			//端口连接状态返回码
			if (playPort == -1)
				return;
			switch (dataType) {
			case 1: 
				// 打开流连接
				if (myPlayer.openStream(playPort, paramArrayOfByte, byteLen,1024 * 1024)) {
					// 放入要播放的控件中
					if (myPlayer.play(playPort, holder)) {
						playFlag = true;
					} else {
						playError(3);
					}
				} else {
					playError(1);
				}
				break;
			case 4:
				if (playFlag && myPlayer.inputData(playPort, paramArrayOfByte,byteLen)) {
					playFlag = true;
				} else {
					playError(4);
					playFlag = false;
				}
			}
		}
	};

	//打印出相对应的异常
	private void playError(int step) {

		switch (step) {
		case 1:
			System.out.println("openStream error,step=" + step);
			break;
		case 2:
			System.out.println("setStreamOpenMode error,step=" + step);
			break;
		case 3:
			System.out.println("play error,step=" + step);
			break;
		case 4:
			System.out.println("inputData error,step=" + step);
			break;
		}
		stopPlay();
	}
}