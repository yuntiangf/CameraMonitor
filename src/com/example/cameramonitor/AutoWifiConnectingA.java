package com.example.cameramonitor;

import java.lang.Thread;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hikvision.wifi.configuration.BaseUtil;
import com.hikvision.wifi.configuration.DeviceDiscoveryListener;
import com.hikvision.wifi.configuration.DeviceInfo;
import com.hikvision.wifi.configuration.OneStepWifiConfigurationManager;
import com.videogo.constant.IntentConsts;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.EzvizAPI;
import com.videogo.openapi.bean.resp.CameraInfo;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LogUtil;
import com.videogo.util.Utils;
//import com.videogo.widget.WaitDialog;

/**
 * һ���������ͷ����
 * 
 * @author chengjuntao
 * @data 2014-4-9
 */
public class AutoWifiConnectingA extends Activity implements OnClickListener {
    private static final String TAG = "AutoWifiConnectingActivity";

    /** һ����ӵĵ�ǰ״̬ ��������wifi */
    private static final int STATUS_WIFI_CONNETCTING = 1000;

    /** һ����ӵĵ�ǰ״̬ ���ڽ����豸ע�� */
    private static final int STATUS_REGISTING = 101;

    /** һ����ӵĵ�ǰ״̬ �����������ͷ */
    private static final int STATUS_ADDING_CAMERA = 102;

    /** һ����ӵĵ�ǰ״̬ �������ͷ */
    private static final int STATUS_ADD_CAMERA_SUCCESS = 103;

    /** һ����Ӵ������ �豸����wifiʧ�� */
    private static final int ERROR_WIFI_CONNECT = 1000;

    /** һ����Ӵ������ �豸ע��ʧ�� */
    private static final int ERROR_REGIST = 1001;

    /** һ����Ӵ������ �豸�������ͷʧ�� */
    private static final int ERROR_ADD_CAMERA = 1002;

    private static final long OVERTIME_CONNECT_WIFI_REGIST = 2 * 60 * 1000;

    // ���ذ�ť
    private View btnBack;

    // title
    private TextView tvTitle;

    // �������ͷ������
    private View addCameraContainer;

    // �������ӵ�����
    private View lineConnectContainer;

    // ״̬�仯ͼ
    private ImageView imgStatus;

    // ״̬
    private TextView tvStatus;

    // ���԰�ť
    private View btnRetry;

    // ��������
    private Button btnLineConnect;

    // �����ӳɹ�
    private View btnLineConnetOk;

    // ��ɰ�ť
    private View btnFinish;

    private String serialNo;

    private String wifiPassword = "";

    private String wifiSSID = "";
    
//    private String deviceType;

    /** ��ǰ�Ĵ������ */
    private int errorStep = 0;

    private String mVerifyCode = "";

    private ImageView imgAnimation;

    private AnimationDrawable animWaiting;

    private String maskIpAddress;

    private Timer overTimeTimer;

    private OneStepWifiConfigurationManager oneStepWifi;

    private MulticastLock lock;
    
    private CameraInfo mCameraInfo = null;

    private DeviceDiscoveryListener deviceDiscoveryListener = new DeviceDiscoveryListener() {
        @Override
        public void onDeviceLost(DeviceInfo deviceInfo) {
        }

        @Override
        public void onDeviceFound(DeviceInfo deviceInfo) {
            Message msg = new Message();
            msg.what = 0;
            msg.obj = deviceInfo;
            defiveFindHandler.sendMessage(msg);
        }

		@Override
		public void onError(String arg0, int arg1) {
			
		}
    };

    private boolean isWifiConnected = false;
    private boolean isPlatConnected = false;

    private boolean isPlatBonjourget = false;
    private boolean isWifiOkBonjourget = false;
    private Handler defiveFindHandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                DeviceInfo deviceInfo = (DeviceInfo) msg.obj;
                if (deviceInfo == null || deviceInfo.getState() == null) {
                    LogUtil.debugLog(TAG, "���յ���Ч��bonjour��Ϣ Ϊ��");
                    return;
                }
                // �豸���к� ��� ˵��������Ҫ��ӵ��豸 ������
                if (serialNo != null && serialNo.equals(deviceInfo.getSerialNo())) {
                    if ("WIFI".equals(deviceInfo.getState().name())) {
                        if (isWifiConnected) {
                            return;
                        }
                        isWifiOkBonjourget = true;
                        isWifiConnected = true;
                        LogUtil.debugLog(TAG, "���յ��豸������wifi��Ϣ " + deviceInfo.toString());
                        stopConfigOnThread();
                        changeStatuss(STATUS_REGISTING);
                    } else if ("PLAT".equals(deviceInfo.getState().name())) {
                        if (isPlatConnected) {
                            return;
                        }
                        isPlatBonjourget = true;
                        isPlatConnected = true;
                        LogUtil.debugLog(TAG, "���յ��豸������PLAT��Ϣ " + deviceInfo.toString());
                        cancelOvertimeTimer();
                        changeStatuss(STATUS_ADDING_CAMERA);
                    }
                }
            }
        };
    };

    private View btnCancel;

    private int searchErrorCode = -1;

    private boolean isSupportNetWork = true;

    private boolean isSupportWifi = true;

    private View tvDeviceWifiConfigTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_wifi_connecting);
        init();
        findViews();
        initUI();
        setListener();
        new GetCamersInfoTask().execute();
    }

    private void init() {
        // ���ѣ�����
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent intent = getIntent();
        serialNo = intent.getStringExtra(IntentConsts.EXTRA_DEVICE_ID);
        mVerifyCode = intent.getStringExtra(IntentConsts.EXTRA_DEVICE_CODE);
        wifiPassword = intent.getStringExtra(IntentConsts.EXTRA_WIFI_PASSWORD);
        wifiSSID = intent.getStringExtra(IntentConsts.EXTRA_WIFI_SSID);
        System.out.println("serialNo = " + serialNo + ",mVerifyCode = " + mVerifyCode + ",wifiPassword = "
                + wifiPassword + ",wifiSSID = " + wifiSSID + ",isSupportNetWork " + isSupportNetWork
                + ",isSupportWifi " + isSupportWifi);
        
        // һ����װ������
        maskIpAddress = BaseUtil.getMaskIpAddress(getApplicationContext());
        oneStepWifi = new com.hikvision.wifi.configuration.OneStepWifiConfigurationManager(this, maskIpAddress);
        oneStepWifi.setDeviceDiscoveryListener(deviceDiscoveryListener);
        LogUtil.debugLog(TAG, wifiSSID + " " + wifiPassword + " " + maskIpAddress);
    }

    /**
     * ����Է���������
     * 
     * @see
     * @since V1.0
     */
    private void findViews() {
    	
        btnBack = findViewById(R.id.btnBack);
        btnCancel = findViewById(R.id.cancel_btn);
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        addCameraContainer = findViewById(R.id.addCameraContainer);
        lineConnectContainer = findViewById(R.id.lineConnectContainer);
        imgStatus = (ImageView) findViewById(R.id.imgStatus);
        tvStatus = (TextView) findViewById(R.id.tvStatus);

        btnRetry = (TextView) findViewById(R.id.btnRetry);
        btnLineConnect = (Button) findViewById(R.id.btnLineConnet);
        btnLineConnetOk = findViewById(R.id.btnLineConnetOk);
        imgAnimation = (ImageView) findViewById(R.id.imgAnimation);
        btnFinish = findViewById(R.id.btnFinish);

        tvDeviceWifiConfigTip = findViewById(R.id.tvDeviceWifiConfigTip);
    }

    /**
     * ����Է���������
     * 
     * @see
     * @since V1.0
     */
    private void initUI() {
        tvTitle.setText("�豸���");
    }

    /**
     * ����Է���������
     * 
     * @see
     * @since V1.0
     */
    private void setListener() {
        btnBack.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnLineConnect.setOnClickListener(this);
        btnLineConnetOk.setOnClickListener(this);
        btnRetry.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
    }

    /**
     * ��ʼ����wifi״̬,����bonjour��Ϣ,���ó�ʱ��Ϣ
     * 
     * @see
     * @since V1.8.2
     */
    private void start() {
        android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) getSystemService(android.content.Context.WIFI_SERVICE);
        lock = wifi.createMulticastLock("videogo_multicate_lock");
        lock.setReferenceCounted(true);
        lock.acquire();
        isWifiConnected = false;
        isPlatConnected = false;
        isWifiOkBonjourget = false;
        isPlatBonjourget = false;
        // ���
        startOvertimeTimer(OVERTIME_CONNECT_WIFI_REGIST, new Runnable() {
            public void run() {
                stopBonjour();
                if (isWifiOkBonjourget && isPlatBonjourget) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            changeStatuss(STATUS_ADDING_CAMERA);
                        }
                    }); 
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            addCameraFailed(ERROR_WIFI_CONNECT, searchErrorCode);
                        }
                    }); 
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                stopConfigAndBonjour(false);
                if (oneStepWifi == null) {
                    maskIpAddress = BaseUtil.getMaskIpAddress(getApplicationContext());
                    oneStepWifi = new OneStepWifiConfigurationManager(
                            AutoWifiConnectingA.this, maskIpAddress);
                    oneStepWifi.setDeviceDiscoveryListener(deviceDiscoveryListener);
                    LogUtil.debugLog(TAG, wifiSSID + " " + wifiPassword + " " + maskIpAddress);
                }
                int startSendConfigData = oneStepWifi.startConfig(wifiSSID, wifiPassword);
                if (startSendConfigData == OneStepWifiConfigurationManager.START_SUCESS) {
                    LogUtil.debugLog(TAG, "��ʼ�����ص�ַ: " + maskIpAddress + " ��������: ssid: " + wifiSSID + " key:"
                            + wifiPassword);
                } else if (startSendConfigData == OneStepWifiConfigurationManager.PARAM_ERROR) {
                    LogUtil.debugLog(TAG, "���÷��ͽӿ�: �����쳣");
                } else if (startSendConfigData == OneStepWifiConfigurationManager.HAS_SENDING) {
                    LogUtil.debugLog(TAG, "���ڷ��ͣ����Ժ�...");
                }
                if (!isFinishing()
                        && ConnectionDetector.getConnectionType(AutoWifiConnectingA.this) == ConnectionDetector.WIFI
                        && oneStepWifi != null) {
                	oneStepWifi.startBonjour();
                }
            }
        }).start();
    }

    /**
     * ֹͣ����wifi��ע���豸
     * 
     * @see
     * @since V1.8.2
     */
    private synchronized void stopBonjourOnThread() {
        if (lock != null) {
            lock.release();
            lock = null;
        }
        // ֹͣ���ã�ֹͣbonjour����
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                stopConfigAndBonjour(false);
                LogUtil.debugLog(TAG, "stopBonjourOnThread .cost time = " + (System.currentTimeMillis() - startTime)
                        + "ms");
            }
        }).start();
        LogUtil.debugLog(TAG, "stopBonjourOnThread ..................");
    }

    /**
     * ֹͣ����wifi��ע���豸
     * 
     * @see
     * @since V1.8.2
     */
    private synchronized void stopConfigOnThread() {
        // ֹͣ���ã�ֹͣbonjour����
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                stopConfigAndBonjour(true);
                LogUtil.debugLog(TAG, "stopConfigOnThread .cost time = " + (System.currentTimeMillis() - startTime)
                        + "ms");
            }
        }).start();
    }

    /**
     * ����Է���������
     * 
     * @param config
     *            ture just stop Config
     * @see
     * @since V1.8.2
     */
    private synchronized void stopConfigAndBonjour(boolean config) {

        if (oneStepWifi != null) {
            if (config) {
            	oneStepWifi.stopConfig();
            } else {
            	oneStepWifi.stopConfig();
            	oneStepWifi.stopBonjour();
            	oneStepWifi = null;
            }
            LogUtil.debugLog(TAG, "stopConfigAndBonjour is invoked...");
        }
    }

    /**
     * ֹͣ����wifi��ע���豸
     * 
     * @see
     * @since V1.8.2
     */
    private void stopBonjour() {
        long startTime = System.currentTimeMillis();
        if (lock != null) {
            lock.release();
            lock = null;
        }
        // ֹͣ���ã�ֹͣbonjour����
        stopConfigAndBonjour(false);
        LogUtil.debugLog(TAG, "stopBonjour cost time = " + (System.currentTimeMillis() - startTime) + "ms");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                // �豸�л�wifi�������ģ��������ӳɹ�
                if (tvDeviceWifiConfigTip.getVisibility() == View.VISIBLE) {

                    finish();
                } else {
                    if (addCameraContainer.getVisibility() == View.VISIBLE && btnRetry.getVisibility() != View.VISIBLE) {
                        showConfirmDialog();
                    } else {
                        finish();
                    }
                }

                break;
            case R.id.cancel_btn:
                cancelOnClick();
                break;
            case R.id.btnRetry:
                retryOnclick();
                break;
            case R.id.btnLineConnet:
                lineConnectClick();
                break;
            case R.id.btnLineConnetOk:
                lineConnectOkClick();
                break;
            case R.id.btnFinish:
                closeActivity();
                break;
            default:
                break;
        }
    }

    private void cancelOnClick() {
        btnCancel.setVisibility(View.GONE);
        lineConnectContainer.setVisibility(View.GONE);
        addCameraContainer.setVisibility(View.VISIBLE);
        tvTitle.setText("�豸���");
    }

    /**
     * ���԰�ť�������
     * 
     * @see
     * @since V1.8.2
     */
    private void retryOnclick() {
        switch (errorStep) {
            case ERROR_WIFI_CONNECT:
            case ERROR_REGIST:
                changeStatuss(STATUS_WIFI_CONNETCTING);
                break;
            case ERROR_ADD_CAMERA:
                changeStatuss(STATUS_ADDING_CAMERA);
                break;
            default:
                break;
        }
    }

    /**
     * �������Ӱ�ť�������
     * 
     * @see
     * @since V1.8.2
     */
    private void lineConnectClick() {
        btnCancel.setVisibility(View.VISIBLE);
        lineConnectContainer.setVisibility(View.VISIBLE);

        tvTitle.setText("�����������");
        addCameraContainer.setVisibility(View.GONE);
    }

    /**
     * �Ѿ����Ӻð�ť�������
     * 
     * @see
     * @since V1.8.2
     */
    private void lineConnectOkClick() {
        //cancelOnClick();
        btnRetry.setVisibility(View.GONE);
        btnLineConnect.setVisibility(View.GONE);
        changeStatuss(STATUS_ADDING_CAMERA);
    }

    /**
     * �����Ի�ȷ���Ƿ��˳�
     * 
     * @see
     * @since V1.8.2
     */
    private void showConfirmDialog() {
        new AlertDialog.Builder(this).setMessage("��ӹ��������е����������ĵ�һ���Ŷ~")
                .setPositiveButton("�˳�", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).setNegativeButton("�ȴ�", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    /**
     * �ı�״̬���ı�����
     * 
     * @param Status
     * @see
     * @since V1.8.2
     */
    private void changeStatuss(int Status) {
        switch (Status) {
            case STATUS_WIFI_CONNETCTING:
                tvStatus.setText("�뽫�ֻ������豸����ӹ��̴�Լ��Ҫ�ȴ�1���ӣ����Ժ�");
                imgStatus.setImageResource(R.drawable.auto_wifi_cicle_bg);
                imgAnimation.setVisibility(View.VISIBLE);
                imgStatus.setVisibility(View.VISIBLE);
                imgAnimation.setImageResource(R.drawable.auto_wifi_wait);
                animWaiting = (AnimationDrawable) imgAnimation.getDrawable();
                animWaiting.start();
                btnRetry.setVisibility(View.GONE);
                btnLineConnect.setVisibility(View.GONE);
                start();
                break;
            case STATUS_REGISTING:
                tvStatus.setText("����ע�ᵽ�����������Ժ�");
                imgStatus.setImageResource(R.drawable.auto_wifi_cicle_120);
                imgStatus.setVisibility(View.VISIBLE);
                imgAnimation.setImageResource(R.drawable.auto_wifi_wait1);
                animWaiting = (AnimationDrawable) imgAnimation.getDrawable();
                animWaiting.start();
                btnRetry.setVisibility(View.GONE);
                btnLineConnect.setVisibility(View.GONE);
                break;
            case STATUS_ADDING_CAMERA:
                tvStatus.setText("��������豸�����Ժ�");
                imgAnimation.setImageResource(R.drawable.auto_wifi_wait1);
                imgStatus.setVisibility(View.VISIBLE);
                imgStatus.setImageResource(R.drawable.auto_wifi_cicle_240);
                animWaiting = (AnimationDrawable) imgAnimation.getDrawable();
                animWaiting.start();
                btnRetry.setVisibility(View.GONE);
                btnLineConnect.setVisibility(View.GONE);
                if(mCameraInfo == null) {
                    //Goto ����豸�м�ҳ
                    EzvizAPI.getInstance().gotoAddDevicePage(serialNo, mVerifyCode);
                    finish();
                } else {
                    mCameraInfo.setStatus(1);//�豸������
                    changeStatuss(STATUS_ADD_CAMERA_SUCCESS);
                }
                break;
            case STATUS_ADD_CAMERA_SUCCESS:
                tvStatus.setText("��ϲ������ӳɹ� ��");
                imgStatus.setImageResource(R.drawable.auto_wifi_add_success_2);
                imgAnimation.setVisibility(View.GONE);

                btnBack.setVisibility(View.GONE);
                btnFinish.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * ������ʱ
     * 
     * @param time
     * @see
     * @since V1.8.2
     */
    private void startOvertimeTimer(long time, final Runnable run) {
        if (overTimeTimer != null) {
            overTimeTimer.cancel();
            overTimeTimer = null;
        }
        overTimeTimer = new Timer();
        overTimeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                LogUtil.debugLog(TAG, "startOvertimeTimer");
                runOnUiThread(run);
            }
        }, time);

    }

    /**
     * �رճ�ʱ
     * 
     * @see
     * @since V1.8.2
     */
    private void cancelOvertimeTimer() {
        if (overTimeTimer != null) {
            overTimeTimer.cancel();
        }
    }

    /**
     * ����ʧ�ܵĴ���
     * 
     * @param errorStep
     * @see
     * @since V1.8.2
     */
    private void addCameraFailed(int errorStep, int errorCode) {
        this.errorStep = errorStep;
        switch (errorStep) {
            case ERROR_WIFI_CONNECT:
                btnRetry.setVisibility(View.VISIBLE);
                // ֧���������Ӳ���ʾ
                if (isSupportNetWork) {
                    btnLineConnect.setVisibility(View.VISIBLE);
                }
                btnLineConnect.setText("��������");
                imgAnimation.setImageResource(R.drawable.auto_wifi_failed);
                tvStatus.setText("Wi-Fi����ʧ�ܣ������Ի򷵻ؼ��Wi-Fi�����Ƿ�������ȷ");
                // stopBonjourOnThread();
                break;
            case ERROR_REGIST:
                // stopBonjourOnThread();
                btnRetry.setVisibility(View.VISIBLE);
                btnLineConnect.setVisibility(View.GONE);
                imgAnimation.setImageResource(R.drawable.auto_wifi_failed);
                tvStatus.setText("�豸δ���ӵ�өʯ�ƣ������豸���������Ƿ�����������������");
                break;
            case ERROR_ADD_CAMERA:
                btnRetry.setVisibility(View.VISIBLE);
                btnLineConnect.setVisibility(View.GONE);
                imgAnimation.setImageResource(R.drawable.auto_wifi_failed);
                
                if(errorCode == ErrorCode.ERROR_WEB_CAMERA_NO_PERMISSION
                || errorCode == ErrorCode.ERROR_WEB_DIVICE_ADDED
                || errorCode == ErrorCode.ERROR_WEB_DIVICE_ONLINE_ADDED
                || errorCode == ErrorCode.ERROR_WEB_DIVICE_OFFLINE_ADDED) {
                    tvStatus.setText("���豸�ѱ��������");
                } else if (errorCode == ErrorCode.ERROR_WEB_DEVICE_NOTEXIT
                        || errorCode == ErrorCode.ERROR_WEB_DEVICE_NOT_EXIT) {
                    tvStatus.setText("");
                } else if (errorCode > 0) {
                    tvStatus.setText(Utils.getErrorTip(this, R.string.auto_wifi_add_device_failed, errorCode));
                } else {
                    tvStatus.setText("���ʧ�ܣ�������");
                }
                break;
            default:
                break;
        }
    }

    /**
     * �رյ�ǰ���棬������ҳ��
     * 
     * @see
     * @since V1.8.2
     */
    private void closeActivity() {
        Intent intent = new Intent(this, EzvizShowActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (btnFinish.getVisibility() == View.VISIBLE) {
            closeActivity();
        } else if (btnCancel.getVisibility() == View.VISIBLE) {
            cancelOnClick();
        } else {
            btnBack.performClick();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelOvertimeTimer();
        stopBonjourOnThread();
    }
    
    /**
     * ��ȡ�豸��Ϣ����
     */
    private class GetCamersInfoTask extends AsyncTask<Void, Void, CameraInfo> {  
        private int mErrorCode = 0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected CameraInfo doInBackground(Void... params) {
            if(AutoWifiConnectingA.this.isFinishing()) {
                return null;
            }
            if (!ConnectionDetector.isNetworkAvailable(AutoWifiConnectingA.this)) {
                return null;
            }

            try {
                return (CameraInfo)EzvizAPI.getInstance().getCameraInfo(1, serialNo);
            } catch (BaseException e) {
                e.printStackTrace();
                mErrorCode = e.getErrorCode();
                return null;
            }
        }

        @Override
        protected void onPostExecute(CameraInfo result) {
            super.onPostExecute(result);
//            mWaitDlg.dismiss();
            if(AutoWifiConnectingA.this.isFinishing()) {
                return;
            }
            
            if (result != null) { 
                mCameraInfo = result;
                if(mCameraInfo.getStatus() == 1) {//�豸���ߣ��ѱ��Լ����
                    changeStatuss(STATUS_ADD_CAMERA_SUCCESS);
                } else {//�豸�����ߣ��ѱ��Լ���ӣ�ֻ������wifi
                    changeStatuss(STATUS_WIFI_CONNETCTING);
                }
            } else {
                onError(mErrorCode);
            }
        }
        
        protected void onError(int errorCode) {
            LogUtil.debugLog(TAG, "GetCamersInfoTask onError:" + errorCode);
            switch (errorCode) {
                case ErrorCode.ERROR_WEB_SESSION_ERROR:
                case ErrorCode.ERROR_WEB_SESSION_EXPIRE:
                case ErrorCode.ERROR_WEB_HARDWARE_SIGNATURE_ERROR:
                    EzvizAPI.getInstance().gotoLoginPage();
                    finish();
                    break;
                case ErrorCode.ERROR_WEB_DEVICE_NOTEXIT:
                case ErrorCode.ERROR_WEB_DEVICE_NOT_EXIT:
                case ErrorCode.ERROR_WEB_DIVICE_ONLINE_NOT_ADD:
                    //Goto ����豸�м�ҳ
                    EzvizAPI.getInstance().gotoAddDevicePage(serialNo, mVerifyCode);
                    finish();
                    break;
                case ErrorCode.ERROR_WEB_DIVICE_ADDED:
                case ErrorCode.ERROR_WEB_DIVICE_ONLINE_ADDED:
                case ErrorCode.ERROR_WEB_DIVICE_OFFLINE_ADDED:                    
                    addCameraFailed(ERROR_ADD_CAMERA, mErrorCode);
                    break;
                case ErrorCode.ERROR_WEB_DIVICE_NOT_ONLINE:
                case ErrorCode.ERROR_WEB_DIVICE_OFFLINE:
                default:
                    if (!isSupportWifi) {
                        lineConnectClick();
                        btnBack.setVisibility(View.VISIBLE);
                        btnCancel.setVisibility(View.GONE);
                    } else {
                        changeStatuss(STATUS_WIFI_CONNETCTING);
                    }
                    break;                    
            }
        }
    }
}