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

	//SurfaceView����������ʾ��Ƶ
	private SjrsSurfaceView nowSjrsSurfaceView;
	// ��Ƶ���� 
	private Button btUp;
	// ��Ƶ���� 
	private Button btDown;
	// ��Ƶ���� 
	private Button btLeft;
	// ��Ƶ���� 
	private Button btRigth;
	// ��Ƶ���� 
	private Button btUpLeft;
	// ��Ƶ���� 
	private Button btUpRigth;
	// ��Ƶ���� 
	private Button btDownLeft;
	// ��Ƶ���� 
	private Button btDownRigth;
	// ��Ȧ���� 
	private Button btAmplification;
	// ��Ȧ��С 
	private Button btShrink;
	// ץͼ
	private Button btCaptur;
	// �Զ�ת�� 
	private Button btAuto;
	// ¼�� 
	private Button btRecord;
	// �ػ� 
	private Button btShutDown;
	// ���� 
	private Button btReboot;
	// button����¼�
	private ButtonListener btnListener;
	//	ʵ���������SDK
	private SjrsSurfaceView mSurface;
	// ��ص���Ϣ�� 
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

	// �������
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

	// ��������
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

	//ҳ���ʼ��
	private void init() {
		mSurface = new SjrsSurfaceView(HKShowActivity.this);
	}  
  
	// ��ʾ
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		
		// ���û���ڲ��ŵĻ�
		if (!nowSjrsSurfaceView.playFlag) {
			// ��ص���Ϣ��
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
			cameraInfo.describe = "���Ե�";

			nowSjrsSurfaceView.setMonitorInfo(cameraInfo);
			// ��ʼʵʱԤ��
			nowSjrsSurfaceView.startPlay(); 
		}
	}

	// ��ͣ
	protected void onPause() {
		super.onPause();
		if (nowSjrsSurfaceView.playFlag) {
			nowSjrsSurfaceView.stopPlay(); // ֹͣʵʱԤ��
		}
	}

	//ץͼ
	private void Capture(){
		NET_DVR_JPEGPARA jpeg = new NET_DVR_JPEGPARA();
		INT_PTR a = new INT_PTR();
		System.out.println("���س��ȣ�"+a);
		byte[] num = new byte[1024*1024];
		//����ͼƬ�ķֱ���
		jpeg.wPicSize = 2;
		//����ͼƬ������
		jpeg.wPicQuality = 2;
		//��ȡ��ǰʱ��
		Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		//�����ļ�Ŀ¼
		File file = new File(cameraInfo.filepath+"/"+month+"."+day+"/"+(i++)+".jpg");
		System.out.println("fffffffffffff-->"+file);
		System.out.println("�ļ�·����"+cameraInfo.filepath);
//		int n = 0;
		boolean is = mSurface.SjrsSurface().NET_DVR_CaptureJPEGPicture(
				cameraInfo.userId, 1, jpeg, file+"");
//		boolean is = mSurface.SjrsSurface().NET_DVR_CaptureJPEGPicture_NEW(
//				cameraInfo.userId, 1, jpeg, num, 1024*1024, a);
		System.out.println(is+"�쳣-->"+mSurface.SjrsSurface().NET_DVR_GetLastError());
		
		//�洢����
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
				"��ȡ�ɹ���������SD����"+cameraInfo.filepath+"��", Toast.LENGTH_SHORT).show();
	}
	
	//¼��
	private void Record() {
		if(flag == 0){
			boolean is = mSurface.SjrsSurface().NET_DVR_StartDVRRecord(cameraInfo.userId, 1, 0);
			System.out.println(is+"�쳣-->"+mSurface.SjrsSurface().NET_DVR_GetLastError());
			flag = 1;
		}else if(flag == 1){
			boolean iv = mSurface.SjrsSurface().NET_DVR_StopDVRRecord(cameraInfo.userId, 1);
			System.out.println(iv+"�쳣-->"+mSurface.SjrsSurface().NET_DVR_GetLastError());
			flag = 0;
		}
	}
	
	//���򰴼�����      ע�⣺�˴���ͨ���Ų��� ʵ��Ϊ��2 ������ָ��Ϊ��1(��ͨ��)�ſ���������
	public class ButtonListener implements OnTouchListener,OnClickListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.bt_up:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,21,0);
				System.out.println("����");
				break;
			case R.id.bt_down:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,22,0);
				System.out.println("����");
				break;
			case R.id.bt_left:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,23,0);
				System.out.println("����");
				break;
			case R.id.bt_rigth:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,24,0);
				System.out.println("����");
				break;
			case R.id.bt_up_left:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,25,0);
				System.out.println("����");
				break;
			case R.id.bt_up_rigth:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,26,0);
				System.out.println("����");
				break;
			case R.id.bt_down_left:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,27,0);
				System.out.println("����");
				break;
			case R.id.bt_down_rigth:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,28,0);
				System.out.println("����");
				break;
			case R.id.bt_amplification:
				boolean amplification = mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,11,0);
				System.out.println("�쳣��"+mSurface.SjrsSurface().NET_DVR_GetLastError());
				System.out.println("����Ŵ�"+amplification);
				break;
			case R.id.bt_shrink:
				boolean shrink = mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,12,0);
				System.out.println("�쳣��"+mSurface.SjrsSurface().NET_DVR_GetLastError());
				System.out.println("������С"+shrink);
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
				System.out.println("���������ƶ�");
				break;
			case R.id.bt_down:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,22,1);
				System.out.println("���������ƶ�");
				break;
			case R.id.bt_left:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,23,1);
				System.out.println("���������ƶ�");
				break;
			case R.id.bt_rigth:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,24,1);
				System.out.println("���������ƶ�");
				break;
			case R.id.bt_up_left:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,25,1);
				System.out.println("���������ƶ�");
				break;
			case R.id.bt_up_rigth:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,26,1);
				System.out.println("���������ƶ�");
				break;
			case R.id.bt_down_left:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,27,1);
				System.out.println("���������ƶ�");
				break;
			case R.id.bt_down_rigth:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId,1,28,1);
				System.out.println("���������ƶ�");
				break;
			case R.id.bt_amplification:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(0,1,13,1);
				System.out.println("��������Ŵ�");
				break;
			case R.id.bt_shrink:
				mSurface.SjrsSurface().NET_DVR_PTZControl_Other(0,1,14,1);
				System.out.println("����������С");
				break;
			case R.id.bt_capture:
				Capture();
				break;
			case R.id.bt_auto:
				boolean auto = mSurface.SjrsSurface().NET_DVR_PTZControl_Other(cameraInfo.userId, 1,29,0);
				System.out.println("�Զ�ת��"+auto);
				break;
			case R.id.bt_record:
				Record();
				break;
			case R.id.bt_shutDown:
				boolean showDown = mSurface.SjrsSurface().NET_DVR_ShutDownDVR(cameraInfo.userId);
				System.out.println("�쳣��"+mSurface.SjrsSurface().NET_DVR_GetLastError());
				System.out.println("�ر��豸"+showDown);
				break;
			case R.id.bt_reboot:
				boolean reboot = mSurface.SjrsSurface().NET_DVR_RebootDVR(cameraInfo.userId);
				System.out.println("�쳣��"+mSurface.SjrsSurface().NET_DVR_GetLastError());
				System.out.println("�����豸"+reboot);
				break;
			default:
				break;
			}
		}
	}
}