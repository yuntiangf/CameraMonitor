package com.example.cameramonitor;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	private Button indoor;
	private Button outdoor;

	private Dialog dialog;
	private Button addCamera;
	private ListView cameraList;
	private List<CameraInfo> listCameraInfo = new ArrayList<CameraInfo>();
	private EditText cameraName;
	private String name;
	private int type;
	private EditText serialNo;
	private String serialno;
	private EditText veryCode;
	private String verycode;
	private Button confirm; 
	private Dialog myDialog;
	private Dialog myDialog2;
	private EditText monitor_ip,monitor_port,monitor_username,monitor_password;
	private String ip,username,password;
	private int port;
	private Button monitor_confirm;
	private RadioGroup radioGroup;
	private Button btnNext;
	private ArrayAdapter<CameraInfo> cameraListAdapter;
	private CameraListAdapter adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		indoor = (Button) findViewById(R.id.indoor);
		outdoor = (Button) findViewById(R.id.outdoor);

		indoor.setOnClickListener(this);
		outdoor.setOnClickListener(this);

		addCamera = (Button) findViewById(R.id.addCamera);
		cameraList = (ListView) findViewById(R.id.cameraList);

		adapter = new CameraListAdapter(this);
		cameraList.setAdapter(adapter);
		
		addCamera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addDevice();
			}
		});
		
		cameraList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				int type = listCameraInfo.get(position).getCamera_type();
				String name = listCameraInfo.get(position).getCamera_name();
				Toast.makeText(MainActivity.this,"name-->"+name+",	type-->"+type,Toast.LENGTH_SHORT).show();
				if(type == 1){
					
					Intent intent1 = new Intent(MainActivity.this,HKShowActivity.class);
					intent1.putExtra("name", name);
					intent1.putExtra("ip", listCameraInfo.get(position).getServerip());
					intent1.putExtra("port", listCameraInfo.get(position).getServerport());
					intent1.putExtra("username", listCameraInfo.get(position).getUsername());
					intent1.putExtra("userpwd", listCameraInfo.get(position).getUserpwd());
					startActivity(intent1);
				}else if(type ==2){
					Toast.makeText(MainActivity.this,"ser-->"+listCameraInfo.get(position).getSerialNo().toString()+
							",	very-->"+listCameraInfo.get(position).getVeryCode().toString(),Toast.LENGTH_SHORT).show();
					Intent intent2 = new Intent(MainActivity.this,EzvizShowActivity.class);
					intent2.putExtra("serialNo", listCameraInfo.get(position).getSerialNo().toString());
					intent2.putExtra("veryCode", listCameraInfo.get(position).getVeryCode().toString());
					startActivity(intent2);
				}
			} 
		});
		
		cameraList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				new AlertDialog.Builder(MainActivity.this)
						.setMessage("确定删除设备？")
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								})
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										listCameraInfo.remove(position);
										Toast.makeText(MainActivity.this,"删除成功！",Toast.LENGTH_SHORT).show();
										
										adapter = new CameraListAdapter(MainActivity.this);
										cameraList.setAdapter(adapter);
									}
								}).show();
				return false;
			}
		});
	}

	private void addDevice() {
		LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
		View view = layoutInflater.inflate(R.layout.addcamera, null);
		dialog = new AlertDialog.Builder(MainActivity.this).setTitle("添加设备")
				.setView(view).create();
		dialog.show();

		type = 0;
		cameraName = (EditText) dialog.findViewById(R.id.cameraName);
		radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
		btnNext = (Button) dialog.findViewById(R.id.btNext);

		radioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						RadioButton rb = (RadioButton) radioGroup
								.findViewById(checkedId);
						switch (checkedId) {
						case R.id.hik:
							type = 1;
							break;
						case R.id.ezviz:
							type = 2;
							break;
						}
					}
				});

		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				name = cameraName.getText().toString();
				if(type == 0){
					Toast.makeText(MainActivity.this,"请选择摄像机类型！",Toast.LENGTH_SHORT).show();
				}else{
					dialog.dismiss();
					if(type == 1){
						HKDialog(name,type);
//						CameraInfo cameraInfo = new CameraInfo(name, type);
//						listCameraInfo.add(cameraInfo);
					}else if(type == 2){
						EzvizDialog(name,type);
						
					}
//					CameraInfo cameraInfo = new CameraInfo(name, type);
//					listCameraInfo.add(cameraInfo);
					adapter = new CameraListAdapter(MainActivity.this);
					cameraList.setAdapter(adapter);
				}
			}
		});
	}

	private void HKDialog(String name, int type){
		LayoutInflater layoutinflater= LayoutInflater.from(MainActivity.this);
    	View view = layoutinflater.inflate(R.layout.hk_dialog, null);
    	myDialog2 = new AlertDialog.Builder(MainActivity.this).setTitle("请输入设备信息")
    			.setView(view).create();
    	myDialog2.show();
    	
    	//设置dialog窗口大小
//    	WindowManager m = getWindowManager();
//    	Display d = m.getDefaultDisplay();
//    	WindowManager.LayoutParams params = myDialog2.getWindow().getAttributes();
//    	params.width = d.getWidth()*5/6;
//    	params.height = d.getHeight()*2/3;
//    	myDialog2.getWindow().setAttributes(params);
    			
    	monitor_ip = (EditText) myDialog2.findViewById(R.id.monitor_ip);
    	monitor_port = (EditText) myDialog2.findViewById(R.id.monitor_port);
    	monitor_username = (EditText) myDialog2.findViewById(R.id.monitor_username);
    	monitor_password = (EditText) myDialog2.findViewById(R.id.monitor_password);
    	monitor_confirm = (Button) myDialog2.findViewById(R.id.monitor_confirm);
    	monitor_confirm.setOnClickListener(this);
	}
	
	private void EzvizDialog(String name, int type){
		LayoutInflater layoutinflater= LayoutInflater.from(MainActivity.this);
		View view = layoutinflater.inflate(R.layout.ezviz_dialog, null);
		myDialog = new AlertDialog.Builder(MainActivity.this).setTitle("请输入设备序列号和验证码")
				.setView(view).create();
		myDialog.show();
		
		serialNo = (EditText) myDialog.findViewById(R.id.serialNum);
		veryCode = (EditText) myDialog.findViewById(R.id.veryCode);
		confirm = (Button) myDialog.findViewById(R.id.confirm);
		
		confirm.setOnClickListener(this);
		
	}
	
	public class CameraListAdapter extends BaseAdapter {
		private Context mContext;
		public CameraListAdapter(Context context) {
			this.mContext = context;
		}

		@Override
		public int getCount() {
			return listCameraInfo.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CameraInfo cameraInfo = listCameraInfo.get(position);
			View view;
			ViewHolder viewHolder;
			try {
				if(convertView == null){
					
					LayoutInflater inflater=LayoutInflater.from(mContext);				
					view=inflater.inflate(R.layout.list_item, null);
					viewHolder=new ViewHolder();
					viewHolder.camera_Name= (TextView) view.findViewById(R.id.camera_name);
					viewHolder.camera_Type= (TextView) view.findViewById(R.id.camera_type);
					viewHolder.camere_image = (ImageView) view.findViewById(R.id.camera_image);
					view.setTag(viewHolder);
				}else{
					view=convertView;
					viewHolder=(ViewHolder) view.getTag();
				}
				viewHolder.camere_image.setImageResource(R.drawable.camera);
				viewHolder.camera_Name.setText(cameraInfo.getCamera_name());
				viewHolder.camera_Type.setText(""+cameraInfo.getCamera_type());
				return view;
			}catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		class ViewHolder{
			ImageView camere_image;
			TextView camera_Name;
			TextView camera_Type;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.indoor:
			Intent intent = new Intent(MainActivity.this,
					EzvizShowActivity.class);
			startActivity(intent);
			break;

		case R.id.outdoor:
			Intent intent2 = new Intent(MainActivity.this, HKShowActivity.class);
			startActivity(intent2);
			break;
		case R.id.confirm:
			myDialog.dismiss();
			serialno = serialNo.getText().toString();
			verycode = veryCode.getText().toString();
			CameraInfo cameraInfo = new CameraInfo(name, type,serialno,verycode);
			listCameraInfo.add(cameraInfo);
			adapter = new CameraListAdapter(MainActivity.this);
			cameraList.setAdapter(adapter);
			break;
		case R.id.monitor_confirm:
			myDialog2.dismiss();
			ip = monitor_ip.getText().toString();
			port = Integer.parseInt(monitor_port.getText().toString());
			username = monitor_username.getText().toString();
			password = monitor_password.getText().toString();
			CameraInfo cameraInfo2 = new CameraInfo(name, type,ip,port,username,password);
			listCameraInfo.add(cameraInfo2);
			adapter = new CameraListAdapter(MainActivity.this);
			cameraList.setAdapter(adapter);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.action_settings:
			Toast.makeText(MainActivity.this, "设置", Toast.LENGTH_SHORT).show();
			break;
		case R.id.action_addCamera:
			addDevice();
			break;
		}
		return false;
	}

}
