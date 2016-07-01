/* 
 * @ProjectName VideoGo
 * @Copyright HangZhou Hikvision System Technology Co.,Ltd. All Right Reserved
 * 
 * @FileName RealPlayerActivity.java
 * @Description 这里对文件进行描述
 * 
 * @author Dengshihua
 * @data 2012-8-20
 * 
 * @note 这里写本文件的详细功能描述和注释
 * @note 历史记录
 * 
 * @warning 这里写本文件的相关警告
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
 * 简单实时预览界面
 */
public class EzvizRealPlay extends Activity implements  SurfaceHolder.Callback,
         Handler.Callback, OnClickListener{
    // 打印标签
    private static final String TAG = "EzvizRealPlayActivity";
    // 实时预览控制对象 
    private RealPlayerManager mRealPlayMgr = null;
    //预览取流任务处理对象
    private RealPlayerHelper mRealPlayerHelper = null;
    
    //播放界面 
    private SurfaceView mSurfaceView = null;
    private SurfaceHolder mSurfaceHolder = null;
    private Handler mHandler = null;
    //摄像头ID 
    private String cameraId;
    //截图按钮
    private ImageButton capture;
    //录像按钮
    private ImageButton record;
    private ImageButton record_start;
    //录像layout
    private LinearLayout realplay_record_ly;
    private TextView realplay_record_tv;
    //实时预览加载
    private LinearLayout realplay_loading_pb_ly;
    private TextView realplay_loading_tv;
    
    private int mRecordSecond = 0;
    private boolean isRunning = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.realplay);
        
//        mSurfaceView = new SurfaceView(EzvizRealPlay.this);
       
        // 初始化数据
        initData();
        //初始化控件
        initViews();
       
    }
    
    // 初始化数据
    private void initData() {           
    	// 获取播放设备信息
        Intent intent = getIntent();
        cameraId =intent.getStringExtra("CameraID");
        
        mRealPlayerHelper = RealPlayerHelper.getInstance(getApplication());
        mHandler = new Handler(this);
        
    }
    
    // 初始化控件
    private void initViews() {
        // 保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        //获取屏幕长宽
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
			Toast.makeText(EzvizRealPlay.this, "截图成功！", Toast.LENGTH_SHORT).show();
			break;
		case R.id.record:
			startRealPlayRecord();
			break;
		case R.id.record_start:
			stopRealPlayRecord();
			break;
		}
	}
  
    // 开始播放
    private void startRealPlay() {
        LogUtil.debugLog(TAG, "startRealPlay");
        updateLoadingUI();
        mRealPlayMgr = new RealPlayerManager(this);
        //设置Handler，接收处理消息
        mRealPlayMgr.setHandler(mHandler);
        //设置播放Surface
        mRealPlayMgr.setPlaySurface(mSurfaceHolder);
        //开启预览任务 
        mRealPlayerHelper.startRealPlayTask(mRealPlayMgr, cameraId);
        System.out.println("cameraId-->"+cameraId);
    }
    
    // 停止播放
    private void stopRealPlay(boolean onScroll) {
        LogUtil.debugLog(TAG, "stopRealPlay");

        if (mRealPlayMgr != null) {
            //停止预览任务
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
        
     // 显示录像时间
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
            //设置播放Surface
            mRealPlayMgr.setPlaySurface(holder);
        }
        mSurfaceHolder = holder;

        LogUtil.debugLog(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.debugLog(TAG, "surfaceDestroyed");
        if (mRealPlayMgr != null) {
            //设置播放Surface
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
