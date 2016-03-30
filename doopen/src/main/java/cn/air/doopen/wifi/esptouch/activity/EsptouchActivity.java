package cn.air.doopen.wifi.esptouch.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.air.doopen.hy.R;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.utli.MyLog;
import cn.air.doopen.utli.T;
import cn.air.doopen.view.TasksCompletedView;
import cn.air.doopen.wifi.esptouch.EsptouchTask;
import cn.air.doopen.wifi.esptouch.IEsptouchListener;
import cn.air.doopen.wifi.esptouch.IEsptouchResult;
import cn.air.doopen.wifi.esptouch.IEsptouchTask;
import cn.air.doopen.wifi.esptouch.task.__IEsptouchTask;
/**Samconfig配置模式*/
public class EsptouchActivity extends Activity implements OnClickListener {

	private static final String TAG = "EsptouchActivity";
	private EspWifiAdminSimple mWifiAdmin;
	private Button bt1 = null;
	private EditText et1 = null;// pwd
	boolean flag = false;
	private SharedPreferences config;
	int wifiID = 0; // 原来wifi连接的ID
	private Button config_de_result_btn;
	private Button selsect_wifi_btn;
	private TasksCompletedView tasks_view;
	private int mTotalProgress;
	private int mCurrentProgress;
	private TextView config_msg_text;
	private IEsptouchTask mEsptouchTask;
	private Thread startwifithread;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_esptouch);
		setview();
		setlistener();
	}

	private void setview() {
		mWifiAdmin = new EspWifiAdminSimple(this);
		config_msg_text = (TextView) findViewById(R.id.config_msg_text);
		bt1 = (Button) findViewById(R.id.button1);
		config_de_result_btn = (Button) findViewById(R.id.config_de_result_btn);
		selsect_wifi_btn = (Button) findViewById(R.id.selsect_wifi_btn);
		et1 = (EditText) findViewById(R.id.editText1);
		tasks_view = (TasksCompletedView) findViewById(R.id.tasks_view);
	}

	private void setlistener() {
		bt1.setOnClickListener(this);
		selsect_wifi_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
			}
		});
		config_de_result_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		config = getSharedPreferences("config", MODE_PRIVATE);
		et1.setText(config.getString("lastpwd", ""));
	}

	// 圆形进度条显示；
	class ProgressRunable implements Runnable {
		@Override
		public void run() {
			mTotalProgress = 100;
			mCurrentProgress = 0;
			while (mCurrentProgress < mTotalProgress) {
				mCurrentProgress += 1;
				tasks_view.setProgress(mCurrentProgress);
				try {/// 20000*100
					Thread.sleep(600);
				} catch (Exception e) {
					e.printStackTrace();
				}
				runOnUiThread(new  Runnable() {
					public void run() {
						if (mCurrentProgress==100) {
							config_msg_text.setText("没有可配置的设备！");
							bt1.setText("开始配置");
							et1.setFocusable(true);
							bt1.setClickable(true);
						}
					}
				});
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		// display the connected ap's ssid
		String apSsid = mWifiAdmin.getWifiConnectedSsid();
		if (apSsid != null) {
			selsect_wifi_btn.setText(apSsid);
			config_msg_text.setTextColor(R.color.shenhuise);
			config_msg_text.setText("请保证WIFI为打开状态，并且能联网！");
			selsect_wifi_btn.setTextColor(R.color.shenhuise);
		} else {
			config_msg_text.setText("当前没有连接上WIFI,请先连接WIFI");
			config_msg_text.setTextColor(Color.RED);
			selsect_wifi_btn.setText("点击设置WIFI");
			selsect_wifi_btn.setTextColor(Color.RED);
		}
		// check whether the wifi is connected
		boolean isApSsidEmpty = TextUtils.isEmpty(apSsid);
		bt1.setEnabled(!isApSsidEmpty);
	}

	@Override
	public void onClick(View v) {
		//开始配置；
		if (v == bt1) {
			bt1.setText("配置中..."); 
			bt1.setClickable(false);
			et1.setFocusable(false);
			new Thread(new ProgressRunable()).start();
			String apSsid = selsect_wifi_btn.getText().toString();
			String apPassword = et1.getText().toString();
			if (__IEsptouchTask.DEBUG) {
				Log.d(TAG, "mBtnConfirm is clicked, mEdtApSsid = " + apSsid + ", " + " mEdtApPassword = " + apPassword);

				config_msg_text.setText("正在配置设备上网...");
				/**
				 * 如果没有锁,如果用户点击确认,取消速度不够快, / /错误将会出现。原因是: / / 0。开始创建任务,但没有完成 / /
				 * 1。任务的任务取消还没有被创建,它什么都不做 / / 2。创建任务 / / 3。哦,这个任务应该被取消,但它正在运行
				 */
				final Object mLock = new Object();
				startwifithread=new  Thread(new Runnable() {
					@Override
					public void run() {
						int taskResultCount = -1;
						synchronized (mLock) {
							MyLog.i("Thread执行了");
							String apSsid = selsect_wifi_btn.getText().toString();
							String apPassword = et1.getText().toString();
							String apBssid = mWifiAdmin.getWifiConnectedBssid();
							Boolean isSsidHidden = false;
							String isSsidHiddenStr = "NO";
							String taskResultCountStr = Integer.toString(3);
							if (isSsidHiddenStr.equals("YES")) {
								isSsidHidden = true;
							}
							taskResultCount = Integer.parseInt(taskResultCountStr);
							mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, isSsidHidden, 
									EsptouchActivity.this);
							mEsptouchTask.setEsptouchListener(myListener);
						}
						mEsptouchTask.executeForResults(taskResultCount);
					}
				});
				startwifithread.start();
			}
		}
	}
	private int code;
	private void onEsptoucResultAddedPerform(final IEsptouchResult result) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String text = result.getBssid() + " is connected to the wifi";
				MyLog.i("getInetAddress="+result.getInetAddress().getHostAddress()+"getBssid="+result.getBssid());
				config_msg_text.setText("设备已连接到无线网络,正在获取IP地址,可能需要一些时间,请稍等... ");
				bt1.setText("开始配置");
				bt1.setClickable(true);
				et1.setFocusable(true);
				if (code == 0) {
					T.setTost(EsptouchActivity.this, "绑定成功");
					EsptouchActivity.this.finish();
					config_msg_text.setText("绑定成功！");
					//保存密码
					SharedPreferences.Editor editor = config.edit();
					editor.putString("lastpwd", et1.getText().toString());
					editor.commit();
				} else if (code == 202) {
					config_msg_text.setText("设备不存在，检查设备是否对应相应的服务器和APP!");
				} else if (code == 201) {
					config_msg_text.setText("设备已经被其他用户绑定，先解除绑定，请重置设备重试");
				} else if (code == 103) {
					config_msg_text.setText("Token令牌验证失败，请重新登录，此时可能用一账号在两台手机上同时登录了；，请重置设备重试");
				}
			}
		});
	}

	private IEsptouchListener myListener = new IEsptouchListener() {

		@Override
		public void onEsptouchResultAdded(final IEsptouchResult result) {
			//在此做延时原因刚刚获取到bssid就进行绑定此时服务器的状态可能还是离线，所以需要等待两秒，再绑定，
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			IotUser iotUser = new IotUser(getApplicationContext());
			code = iotUser.bindDevsm(result.getBssid());
			onEsptoucResultAddedPerform(result);
		}
	};
	//绑定；
	@Override
	protected void onDestroy() {
		Intent intent=new Intent("STOP_UDPSOCKET");
		sendBroadcast(intent);
		if (startwifithread!=null) {
			startwifithread.interrupt();
		}
		MyLog.i("执行了onDestroy方法");
		super.onDestroy();
	}
}
