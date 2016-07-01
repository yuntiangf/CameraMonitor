package com.example.cameramonitor;

import java.util.List;

import com.hikvision.wifi.configuration.BaseUtil;
import com.videogo.constant.Config;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EzvizAPI;
import com.videogo.openapi.bean.req.GetCameraInfoList;
import com.videogo.openapi.bean.resp.CameraInfo;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EzvizShowActivity extends Activity implements OnClickListener {

	private EzvizAPI mEzvizAPI = null;
	Button login, connect, realplay;
	private Button btNext;
	private TextView tvSSID;
	private EditText edtPassword;
	private String maskIpAddress;
	private String serialNo;
	private String veryCode;
	private Dialog myDialog1, myDialog2;
	private String cameraID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ezviz_sel);

		new GetCameraInfoListTask().execute();
		
		initData();
		 findViews();
		// setListener();

	}

	private void findViews() {

		login = (Button) findViewById(R.id.login);
		connect = (Button) findViewById(R.id.connect);
		realplay = (Button) findViewById(R.id.realplay);
		
		login.setOnClickListener(this);
		connect.setOnClickListener(this);
		realplay.setOnClickListener(this);
		
		String str = getInfo();
		System.out.println("ssid-->" + str);

	}

	private void initData() {
		mEzvizAPI = EzvizAPI.getInstance();
		Intent intent = getIntent();
		serialNo = intent.getStringExtra("serialNo");
		veryCode = intent.getStringExtra("veryCode");
		System.out.println("ser-->"+serialNo+",	ver-->"+veryCode);
//		serialNo = "527050475";
//		veryCode = "WWLURO";
		maskIpAddress = BaseUtil.getMaskIpAddress(getApplicationContext());
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.button:
//			if (cameraID != null) {
//				new GetCameraInfoListTask().execute();
//			} else {
//				ShowDialog();
//			}
//			break;
		case R.id.login:
			mEzvizAPI.gotoLoginPage(false);
			break;
		case R.id.connect:
			dialog();
			break;
		case R.id.realplay:
//			new GetCameraInfoListTask().execute();
			Intent intent2 = new Intent(EzvizShowActivity.this,
					EzvizRealPlay.class);
			startActivity(intent2);
//			myDialog1.dismiss();
			break;
		case R.id.btNext:
//			mEzvizAPI.gotoAddDevicePage(serialNo, veryCode);
			myDialog2.dismiss();
			Intent intent = new Intent(EzvizShowActivity.this,AutoWifiConnectingA.class);
			startActivity(intent);
			break;
		}
	}


	// 获取摄像机信息
	private class GetCameraInfoListTask extends
			AsyncTask<Void, Void, List<CameraInfo>> {

		@Override
		public List<CameraInfo> doInBackground(Void... params) {
			try {
				GetCameraInfoList getCameraInfoList = new GetCameraInfoList();
				getCameraInfoList.setPageSize(10);
				getCameraInfoList.setPageStart(0);
				List<CameraInfo> result = mEzvizAPI
						.getCameraInfoList(getCameraInfoList);
				if (result.size() == 0) {
					System.out.println("result size = 0");
					return null;
				} else {
					cameraID = result.get(0).getCameraId();
					Intent intent = new Intent(EzvizShowActivity.this,
							EzvizRealPlay.class);
					intent.putExtra("CameraID", cameraID);
					startActivity(intent);
					System.out.println("ccccccsssssssss-->" + cameraID);
				}

			} catch (BaseException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private void dialog() {
		LayoutInflater layoutInflater = LayoutInflater.from(EzvizShowActivity.this);
		View view = layoutInflater.inflate(R.layout.dialog, null);
		myDialog2 = new AlertDialog.Builder(EzvizShowActivity.this).setTitle("连接网络")
				.setView(view).create();
		myDialog2.show();

		// 设置dialog窗口大小
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay();
		WindowManager.LayoutParams params = myDialog2.getWindow()
				.getAttributes();
		params.width = d.getWidth() * 3 / 4;
		params.height = d.getHeight() / 2;
		myDialog2.getWindow().setAttributes(params);

		tvSSID = (TextView) myDialog2.findViewById(R.id.ssid);
		edtPassword = (EditText) myDialog2.findViewById(R.id.pwd);
		btNext = (Button) myDialog2.findViewById(R.id.btNext);

		tvSSID.setText(getInfo());
		System.out.println("SSID:" + tvSSID.getText().toString());

		btNext.setOnClickListener(this);

	}

	private String getInfo() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		// String ipText= intToIp(info.getIpAddress());
		// System.out.println("ipText-->"+ipText);
		String ssid = info.getSSID();
		String str = ssid.substring(1, ssid.length() - 1);
		return str;
	}
}
