/* 
 * @ProjectName VideoGo
 * @Copyright HangZhou Hikvision System Technology Co.,Ltd. All Right Reserved
 * 
 * @FileName RealPlayerActivity.java
 * @Description ������ļ���������
 * 
 * @author Dengshihua
 * @data 2012-8-20
 * 
 * @note ����д���ļ�����ϸ����������ע��
 * @note ��ʷ��¼
 * 
 * @warning ����д���ļ�����ؾ���
 */
package com.example.cameramonitor;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.videogo.realplay.RealPlayMsg;
import com.videogo.realplay.RealPlayerHelper;
import com.videogo.realplay.RealPlayerManager;
import com.videogo.util.LogUtil;

/**
 * ��ʵʱԤ������
 */
public class EzvizRealPlay extends Activity implements  SurfaceHolder.Callback,
         Handler.Callback, OnClickListener{
    // ��ӡ��ǩ
    private static final String TAG = "EzvizRealPlayActivity";
    // ʵʱԤ�����ƶ��� 
    private RealPlayerManager mRealPlayMgr = null;
    //Ԥ��ȡ�����������
    private RealPlayerHelper mRealPlayerHelper = null;
    
    //���Ž��� 
    private SurfaceView mSurfaceView = null;
    private SurfaceHolder mSurfaceHolder = null;
    private Handler mHandler = null;
    //����ͷID 
    private String cameraId;
    //��ͼ��ť
    private ImageButton capture;
    //¼��ť
    private ImageButton record;
    private ImageButton record_start;
    //¼��layout
    private LinearLayout realplay_record_ly;
    private TextView realplay_record_tv;
    //ʵʱԤ������
    private LinearLayout realplay_loading_pb_ly;
    private TextView realplay_loading_tv;
    
    private int mRecordSecond = 0;
    private boolean isRunning = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.realplay);
        
//        mSurfaceView = new SurfaceView(EzvizRealPlay.this);
       
        // ��ʼ������
        initData();
        //��ʼ���ؼ�
        initViews();
       
    }
    
    // ��ʼ������
    private void initData() {           
    	// ��ȡ�����豸��Ϣ
        Intent intent = getIntent();
        cameraId =intent.getStringExtra("CameraID");
        
        mRealPlayerHelper = RealPlayerHelper.getInstance(getApplication());
        mHandler = new Handler(this);
        
    }
    
    // ��ʼ���ؼ�
    private void initViews() {
        // ������Ļ����
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        //��ȡ��Ļ����
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mSurfaceView = (SurfaceView) findViewById(R.id.video);
        mSurfaceView.getHolder().addCallback(this);
        
        capture = (ImageButton) findViewById(R.id.capture);
        record = (ImageButton) findViewById(R.id.record);
        record_start = (ImageButton) findViewById(R.id.record_start);
        
        realplay_record_ly = (LinearLayout) findViewById(R.id.realplay_record_ly);
        realplay_record_tv = (TextView) findViewById(R.id.realplay_record_tv);
        
        realplay_loading_pb_ly = (LinearLayout) findViewById(R.id.realplay_loading_pb_ly);
        realplay_loading_tv = (TextView) findViewById(R.id.realplay_loading_tv);
        capture.setOnClickListener(this);
        record.setOnClickListener(this);
    }

    @Override
	public void onClick(View v) {
    	switch (v.getId()) {
		case R.id.capture:
			mRealPlayerHelper.capturePictureTask(mRealPlayMgr);
			Toast.makeText(EzvizRealPlay.this, "��ͼ�ɹ���", Toast.LENGTH_SHORT).show();
			break;
		case R.id.record:
			startRealPlayRecord();
			break;
		case R.id.record_start:
			stopRealPlayRecord();
			break;
		}
	}
  
    // ��ʼ����
    private void startRealPlay() {
        LogUtil.debugLog(TAG, "startRealPlay");
        updateLoadingUI();
        mRealPlayMgr = new RealPlayerManager(this);
        //����Handler�����մ�����Ϣ
        mRealPlayMgr.setHandler(mHandler);
        //���ò���Surface
        mRealPlayMgr.setPlaySurface(mSurfaceHolder);
        //����Ԥ������ 
        mRealPlayerHelper.startRealPlayTask(mRealPlayMgr, cameraId);
        System.out.println("cameraId-->"+cameraId);
    }
    
    // ֹͣ����
    private void stopRealPlay(boolean onScroll) {
        LogUtil.debugLog(TAG, "stopRealPlay");

        if (mRealPlayMgr != null) {
            //ֹͣԤ������
            mRealPlayerHelper.stopRealPlayTask(mRealPlayMgr);
        }
    }
    
    private void startRealPlayRecord(){
    	isRunning = true;
    	record_start.setVisibility(View.VISIBLE);
    	record.setVisibility(View.GONE);
    	realplay_record_ly.setVisibility(View.VISIBLE);
    	
    	mRealPlayerHelper.startRecordTask(mRealPlayMgr, EzvizRealPlay.this.getResources(),
    				R.drawable.video_file_watermark);
    	updateTime();
    }
    
    private void stopRealPlayRecord(){
    	record_start.setVisibility(View.GONE);
    	record.setVisibility(View.VISIBLE);
    	realplay_record_ly.setVisibility(View.GONE);
    	mRealPlayerHelper.stopRecordTask(mRealPlayMgr);
    	
    	stopUpdateTimer();
    }
    
    private void updateLoadingProgress(final int progress){
    	if(realplay_loading_tv == null){
    		return;
    	}
    	realplay_loading_tv.setText(progress+"%");
    	mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(realplay_loading_tv != null){
					Random random = new Random();
					int t = random.nextInt(20);
					realplay_loading_tv.setText((progress+t)+"%");
				}
			}
		}, 500);
    }
    
    private void updateLoadingUI(){
    	realplay_loading_pb_ly.setVisibility(View.VISIBLE);
    	capture.setEnabled(false);
    	record.setEnabled(false);
    }
    
    private void handlePlaySuccess(Message msg){
    	realplay_loading_pb_ly.setVisibility(View.GONE);
    	capture.setEnabled(true);
    	record.setEnabled(true);
    }
     
    private String updateRecordTime(){
    	int leftSecond = mRecordSecond % 3600;
        int minitue = leftSecond / 60;
        int second = leftSecond % 60;
        
     // ��ʾ¼��ʱ��
        String recordTime = String.format("%02d:%02d", minitue, second);
        return recordTime;
    }
  
   private void updateTime(){
	   
	   new Thread(){
       	@Override
       	public void run() {
       		String timer= "";
       		while(isRunning){
       			try {
       				Thread.currentThread().sleep(1000);
       				mRecordSecond++;
       				timer = updateRecordTime();
       				Message msg = new Message();
       				msg.obj = timer;
       				msg.what = 0;
       				mHandler.sendMessage(msg);
       			} catch (Exception e) {
       			}
       		}
       		super.run();
       	}
      }.start();
   }

   private void stopUpdateTimer(){
	   isRunning = false;
	   mRecordSecond = 0;
	   mHandler.removeMessages(0);
   }
   
   @Override
   protected void onResume() {
       super.onResume();
       startRealPlay();
   }
   
   @Override
   protected void onPause() {
   	super.onPause();
   }
   
   @Override
   protected void onStop() {
       super.onStop();
       stopRealPlay(false);
   }
   
   @Override
   protected void onDestroy() {     
       super.onDestroy();
   }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mRealPlayMgr != null) {
            //���ò���Surface
            mRealPlayMgr.setPlaySurface(holder);
        }
        mSurfaceHolder = holder;

        LogUtil.debugLog(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.debugLog(TAG, "surfaceDestroyed");
        if (mRealPlayMgr != null) {
            //���ò���Surface
            mRealPlayMgr.setPlaySurface(holder);
        }
    }

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case 0:
			realplay_record_tv.setText(""+msg.obj);
			System.out.println("-->"+realplay_record_tv.getText());
			break;
		case RealPlayMsg.MSG_REALPLAY_PLAY_START:
            updateLoadingProgress(40);
            break;
        case RealPlayMsg.MSG_REALPLAY_CONNECTION_START:
            updateLoadingProgress(60);
            break;
        case RealPlayMsg.MSG_REALPLAY_CONNECTION_SUCCESS:
            updateLoadingProgress(80);
            break;
        case RealPlayMsg.MSG_GET_CAMERA_INFO_SUCCESS:
        	updateLoadingProgress(20);
            break;	
		case RealPlayMsg.MSG_REALPLAY_PLAY_SUCCESS:
			handlePlaySuccess(msg);
			break;
			
		}
		return false;
	}

}
