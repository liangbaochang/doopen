package cn.air.doopen.wifi;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.air.doopen.hy.R;
import cn.air.doopen.hyapp.MyApp;
import cn.air.doopen.utli.MyLog;
/**AP模式的配置界面；自动连接和手动连接*/
@ContentView(value = R.layout.activity_link_dev)
public class LinkDevActivity extends Activity implements OnClickListener {

	@ViewInject(R.id.config_de_result_btn_link) /////
	private Button config_de_result_btn_link; ///// 返回
	@ViewInject(R.id.config_msg_text_dev_share) /////
	private TextView config_msg_text_dev_share; /////// 连接状态
	@ViewInject(R.id.config_msg_text_link_dev) /////
	private TextView config_msg_text_link_dev; /// 设备热点名称；
	@ViewInject(R.id.auto_link_dev) ///
	private Button auto_link_dev; /////// 自动连接
	@ViewInject(R.id.jog_link_dev) /////
	private Button jog_link_dev; /////// 手动连接；
	@ViewInject(R.id.tasks_view) /////
	private cn.air.doopen.view.TasksCompletedView tasks_view;

	private static final String TAG = "SmartConfig:";
	private WifiAdmin wifiAdmin = null;
	private String apId;
	String wifiID = null;
	private String devssid;
	private Realssid realssid;
	public static LinkDevActivity lininfo;

	private int mTotalProgress;
	private int mCurrentProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		lininfo = this;
		MyLog.i(TAG, "onCreate");
		setview();
		// 取消登录；
		Intent intent = new Intent();
		intent.setAction("distserivce");
		sendBroadcast(intent);
	}

	private void setview() {
		// 创建wifi对象；
		wifiAdmin = new WifiAdmin(this);
		// 保存ssid；
		Intent getdata = getIntent();
		devssid = getdata.getStringExtra("ssid");
		SharedPreferences mySharedPreferences = getSharedPreferences("wifinamemsg", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("ssid", devssid);
		editor.commit();
		// 显示扫描到的内容
		config_msg_text_link_dev.setText(devssid);
	}

	@SuppressLint("ResourceAsColor")
	@OnClick({ R.id.config_de_result_btn_link, R.id.auto_link_dev, R.id.jog_link_dev })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.config_de_result_btn_link:// 返回；
			finish();
			break;
		case R.id.auto_link_dev:// 自动连接；
			auto_link_dev.setEnabled(false);
			// new Thread(new ProgressRunable()).start();
			new GPSConfigDevTask().execute(null, null, null);
			if (realssid != null) {
				realssid.cancel(true);
			}
			break;
		case R.id.jog_link_dev:// 手动连接；
			final AlertDialog.Builder builder = new AlertDialog.Builder(LinkDevActivity.this);
			TextView textView = new TextView(getApplicationContext());
			textView.setText("\n" + "  名称:" + devssid + "\n" + "\n" + "  密码:12345678" + "\n");
			textView.setTextColor(R.color.black);
			textView.setTextSize(18);
			builder.setView(textView);
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					wifiAdmin.updateWifiInfo();
					wifiID = wifiAdmin.getSSID();
					startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
					dialog.dismiss();
				}
			});
			builder.show();
			break;
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		MyLog.i(TAG, "onPause");
		if (realssid != null) {
			realssid.cancel(true);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		realssid = new Realssid();
		realssid.execute();
		MyLog.i(TAG, "onRestart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		tasks_view.setProgress(0);
		config_msg_text_dev_share.setText("设备名称");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (realssid != null) {
			realssid.cancel(true);
		}
		MyLog.i(TAG, "onDestroy");
	}

	private int routeid = -1;

	class GPSConfigDevTask extends AsyncTask<Void, Integer, Boolean> {
		String lastErrMsg = null; // 最后结果提示
		Boolean isWifi = false; // 是否正在使用wifi
		// 原来wifi连接的ID
		private int network;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// 任务开始前准备工作,显示进度条,改变按钮文字
			auto_link_dev.setText("配置中...");
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result == true) {
				// 修改界面显示
				// losg_wifi_link_dev.stopRippleAnimation();
				config_msg_text_dev_share.setText(lastErrMsg);
				auto_link_dev.setEnabled(true);
				// 标记一会回到主界面将要刷新一下列表
				((MyApp) getApplication()).isNeedRefreshDevList = true;
				auto_link_dev.setText("自动连接");
				// 延迟一会儿关闭界面，返回我的设备列表
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						startactivity();
					}
				}, 10);
			} else {
				// losg_wifi_link_dev.stopRippleAnimation();
				config_msg_text_dev_share.setText(lastErrMsg);
				// 提示重试
				auto_link_dev.setText("再试一试");
				auto_link_dev.setEnabled(true);
				// 重新连原来的wifi
				wifiAdmin.disconnectWifi(wifiAdmin.getNetworkId());
				wifiAdmin.connectWifi(network);
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			if (values[0] == 0) {
				config_msg_text_dev_share.setText("开始连接设备...");
				tasks_view.setProgress(5);
			} else if (values[0] == 1) {
				config_msg_text_dev_share.setText("断开家中路由器");
				tasks_view.setProgress(20);
			} else if (values[0] == 2) {
				config_msg_text_dev_share.setText("连接智能设备");
			} else if (values[0] == 3) {
				config_msg_text_dev_share.setText("连接智能设备中...");
				tasks_view.setProgress(25);
			} else if (values[0] == 4) {
				config_msg_text_dev_share.setText("正在连接智能设备...");
				tasks_view.setProgress(70);
			} else if (values[0] == 5) {
				tasks_view.setProgress(100);
				config_msg_text_dev_share.setText("连接智能设备成功!");
			} else if (values[0] == 6) {
				config_msg_text_dev_share.setText("获取IP中...");
				tasks_view.setProgress(90);
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				int i;
				// 不能在这个里面修改任何UI的东西；
				// 当手机连接着wifi的时候,记下连接信息,配置完要恢复；
				// 当前wifi状态
				wifiAdmin.updateWifiInfo();
				wifiID = wifiAdmin.getSSID();
				// 记下原来连接的wifi
				routeid = wifiAdmin.getCurrentChannel();
				publishProgress(0);// 准备连接
				MyLog.i("wifi id", String.valueOf(wifiID));
				// 连接设备AP---------
				MyLog.i("cfg", "正在连接AP...");
				publishProgress(0);// 连接智能设备
				// 同样，在读取SharedPreferences数据前要实例化出一个SharedPreferences对象
				SharedPreferences sharedPreferences = getSharedPreferences("wifinamemsg", Activity.MODE_PRIVATE);
				// 使用getString方法获得value，注意第2个参数是value的默认值
				String apwifissid = sharedPreferences.getString("ssid", null);
				MyLog.i(TAG, "apwifissid=" + apwifissid);
				if (!apwifissid.isEmpty()) {
					wifiAdmin.CreateWifiInfo(apwifissid, "12345678");
					publishProgress(3);// 连接智能设备中
					MyLog.i("cfg", "连接中网络！.");
					i = 40;
					while (i > 0) {
						i--;
						Thread.sleep(500);
						// 更新一下状态
						wifiAdmin.updateWifiInfo();
						String kong = wifiAdmin.getSSID();
						MyLog.i(TAG, "devssid==" + devssid + "sdid==" + kong);
						if (wifiAdmin.getSSID().equals(devssid)) {
							// 第一次连接；连接设备ap；
							wifiAdmin.updateWifiInfo();
							if (wifiAdmin.getIPAddress() != 0) {
								MyLog.i(TAG, "devssid==" + devssid + "sdid" + kong + "wifiAdmin.getIPAddress()"
										+ wifiAdmin.getIPAddress());
								publishProgress(5);// 连接智能设备成功，
								lastErrMsg = "连接设备成功！";
								MyLog.i("cfg", "连接设备AP成功");
								return true;
							} else {
								publishProgress(6);//
								MyLog.i(TAG, "devssid==" + devssid + "sdid" + kong + "wifiAdmin.getIPAddress()"
										+ wifiAdmin.getIPAddress());
								lastErrMsg = "获取IP中....";
								MyLog.i("cfg", "获取IP中....");
							}
						} else {
							MyLog.i("cfg", "连接设备AP中...");
							// 检测当前连上的ssid是否就是传入的ssid,以此判断是否连上,恢复wifi并中断操作
							publishProgress(4);// 正在连接智能设备
							lastErrMsg = "连接设备AP中...";
							wifiAdmin.updateWifiInfo();
							MyLog.i(TAG, "sdid=" + wifiAdmin.getSSID() + ";devssid=" + devssid);
						}
					}
					if (i == 0) {
						MyLog.e("cfg", "连接设备AP超时...");
						lastErrMsg = "连接设备AP超时..可以尝试手动连接";
						return false;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	public void startactivity() {
		// 切断当前wifi，连接设备
		Intent getdata = getIntent();
		String devssid = getdata.getStringExtra("ssid");
		String bssid = getdata.getStringExtra("bssid");
		int devChannel = getdata.getIntExtra("channel", -1);
		// 第一次连接；连接设备ap；
		wifiAdmin.updateWifiInfo();
		apId = wifiAdmin.getSSID();
		Intent intent = new Intent(getApplicationContext(), DeployActivity.class);
		intent.putExtra("ssid", devssid);
		intent.putExtra("bssid", bssid);
		intent.putExtra("channel", devChannel);
		intent.putExtra("routeid", routeid);
		intent.putExtra("apId", apId);
		intent.putExtra("wifiID", wifiID);
		startActivity(intent);
	}

	class Realssid extends AsyncTask<Void, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				while (true) {
					Thread.sleep(200);
					wifiAdmin.updateWifiInfo();
					String kong = wifiAdmin.getSSID();
					if (devssid.equals(kong)) {
						MyLog.i(TAG, "equals");
						publishProgress(5);
						break;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			if (values[0] == 5) {
				startactivity();
				MyLog.i(TAG, "startactivity");
			}
		}
	};

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
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
}