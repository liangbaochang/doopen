package cn.air.doopen.wifi;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.air.doopen.hy.R;
import cn.air.doopen.hyapp.MyApp;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.utli.MyLog;
import cn.air.doopen.utli.T;
import cn.air.doopen.utli.tool.BrightnessTools;
import cn.air.doopen.view.TasksCompletedView;
/**AP模式的配置界面发送配置信息连接绑定*/
public class DeployActivity extends Activity implements OnClickListener {

	private final static int WIFI_SELECT_DONE = 1;
	private static final String TAG = "SmartConfig:";

	private WifiAdmin wifiAdmin;
	private Button bt1;
	private TextView tv1;// 状态
	private EditText et1;// pwd
	boolean flag=false;
	private SharedPreferences config;
	int wifiID = 0;                     // 原来wifi连接的ID
	private Button config_de_result_btn;
	private Button selsect_wifi_btn;
	private String apidstr;
	private GPSConfigDevTask configDevTask;
	private int routeid;
	private String ssid;
	private TasksCompletedView tasks_view;
	private int mTotalProgress;
	private int mCurrentProgress;
	private String bssid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_dev_step1);
		setview();
		setlistener();
		tasks_view.setProgress(0);
	}
	private void setview() {
		wifiAdmin = new WifiAdmin(this);
		bt1 = (Button) findViewById(R.id.button1);
		config_de_result_btn = (Button) findViewById(R.id.config_de_result_btn);
		tv1 = (TextView) findViewById(R.id.config_msg_text);
		selsect_wifi_btn = (Button) findViewById(R.id.selsect_wifi_btn);
		et1 = (EditText) findViewById(R.id.editText1);
		tasks_view = (TasksCompletedView) findViewById(R.id.tasks_view);
		Intent apidint=getIntent();
		apidstr=apidint.getStringExtra("apId");
		ssid=apidint.getStringExtra("ssid");
		bssid=apidint.getStringExtra("bssid");
		selsect_wifi_btn.setText(apidint.getStringExtra("wifiID"));
		routeid = apidint.getIntExtra("routeid", -1);
	}

	private void setlistener() {
		bt1.setOnClickListener(this);
		selsect_wifi_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(DeployActivity.this, WifiActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, WIFI_SELECT_DONE);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case WIFI_SELECT_DONE:
			if(resultCode == RESULT_OK){
				Bundle bundle = data.getExtras();
				String wifiapidstr = bundle.getString("ssid");
				routeid=bundle.getInt("targchannel");
				//显示扫描到的内容
				selsect_wifi_btn.setText(wifiapidstr);
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		//点击配置；
		if (v.getId() == R.id.button1) {
			// 记下原来连接的wifi
			wifiID = wifiAdmin.getNetworkId();
			configDevTask=new GPSConfigDevTask();
			configDevTask.execute();
			//设置不可编辑；
			bt1.setEnabled(false);
			selsect_wifi_btn.setEnabled(false);
			et1.setEnabled(false);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (configDevTask!=null) {
			configDevTask.cancel(true);
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	class GPSConfigDevTask extends AsyncTask<Void, Integer, Boolean> {
		String lastErrMsg = null;           // 最后结果提示
		Boolean isWifi = false;             // 是否正在使用wifi
		int sleepindex = 0;
		private int iap;
		private int ib;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// 任务开始前准备工作,显示进度条,改变按钮文字
			bt1.setText("配置中...");
			SharedPreferences.Editor editor = config.edit();
			editor.putString("lastname", wifiAdmin.getSSID());
			editor.putString("lastpwd", et1.getText().toString());
			editor.commit();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result==true) {
				// 修改界面显示
				tv1.setText(lastErrMsg);
				bt1.setText("配置完成");
				// 标记一会回到主界面将要刷新一下列表
				((MyApp)getApplication()).isNeedRefreshDevList = true;
				// 延迟一会儿关闭界面，返回我的设备列表
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						LinkDevActivity.lininfo.finish();
						finish();
					}
				}, 10);
			} else {
				tv1.setText(lastErrMsg);
				// 提示重试
				bt1.setText("配置");
				bt1.setEnabled(true);
				et1.setEnabled(true);
				selsect_wifi_btn.setEnabled(true);
			}
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			if (values[0] == 0) {
				tv1.setText("开始配置...");
				tasks_view.setProgress(5);
			} else if (values[0] == 1) {
				tv1.setText("设备正在连接路由器...");
			} else if (values[0] == 2) {
				T.setTost(DeployActivity.this, "请先连接设备");
			} else if (values[0] == 3) {
				tasks_view.setProgress(60);
				tv1.setText("设备正在连接服务器...");
			} else if (values[0] == 4) {
				tv1.setText("正在连接智能设备...");
				tasks_view.setProgress(10);
			} else if (values[0] == 5) {
				tv1.setText("连接智能设备成功");
				tasks_view.setProgress(20);
			} else if (values[0] == 6) {
				tv1.setText("正在发送配置信息...");
				tasks_view.setProgress(30);
			} else if (values[0] == 7) {
				tv1.setText("发送配置信息到智能设备成功");
				tasks_view.setProgress(40);
			} else if (values[0] == 8) {
				tasks_view.setProgress(50);
				tv1.setText("设备联网中...");
			} else if (values[0] == 9) {
				tv1.setText("智能设备连接服务器成功");
				tasks_view.setProgress(80);
			} else if (values[0] == 10) {
				tasks_view.setProgress(55);
				tv1.setText("智能设备正在连接路由器...");
			} else if (values[0] == 11) {
				tv1.setText("连上了路由器！");
			} else if (values[0] == 12) {
				tv1.setText("正在与智能设备断开连接...");
			} else if (values[0] == 13) {
				tv1.setText("正在恢复WIFI网络...");
				tasks_view.setProgress(85);
			} else if (values[0] == 14) {
				tv1.setText("恢复WIFI网络成功");
				tasks_view.setProgress(90);
			}else if(values[0] == 15){
				tv1.setText("正在绑定智能设备...");
				tasks_view.setProgress(95);
			}else if(values[0] == 16){
				tv1.setText("绑定智能设备成功");
				tasks_view.setProgress(100);
			}else if(values[0] == 17){
				tv1.setText("改变信道成功");
			}else if(values[0] == 18){
				tv1.setText("改变信道中...");
			}
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				UDPClient udpClient = new UDPClient();
				JSONObject msg = null;
				JSONObject result = null;
				int i = 60;
				// 不能在这个里面修改任何UI的东西；
				// 当手机连接着wifi的时候,记下连接信息,配置完要恢复；
				int aprouteid = wifiAdmin.getCurrentChannel();
				wifiAdmin.updateWifiInfo();
				MyLog.i(TAG, "apidstr="+apidstr+"ssids="+apidstr+"sdid="+wifiAdmin.getSSID());
				publishProgress(0);
				if (wifiAdmin.getSSID().equals(apidstr)) {
					if (aprouteid!=routeid) {
						MyLog.i(TAG, "信道不一样，已请求改变信道");
						msg = new JSONObject();
						msg.put("action", "setchannel");
						msg.put("channel", routeid);
						MyLog.i(TAG,"targchannel="+routeid);
						MyLog.i(TAG,"targchannel="+msg.toString());
						result = udpClient.send(msg, 30);
						MyLog.i("result", "result= "+result);
						if (result != null && result.getInt("status") == 0) {
							MyLog.i("cfg", "发送信道改变指令成功！");
						} else {
							MyLog.e("cfg", "发送信道改变指令失败！");
							udpClient.close();
							return false;
						}
						Thread.sleep(800);
						renewAPwifi();
						i = 70;
						while (i > 0) {
							i--;
							Thread.sleep(500);
							sleepindex++;
							// 更新一下状态
							wifiAdmin.updateWifiInfo();
							if (wifiAdmin.getSSID()!=null&&(wifiAdmin.getSSID().contains(apidstr))) {      
								wifiAdmin.updateWifiInfo();
								if (wifiAdmin.getIPAddress() != 0) {
									publishProgress(5);//连接智能设备成功，
									lastErrMsg = "连接设备成功！";
									break;
								} else {
									lastErrMsg = "获取IP中....!";
								}
							} else {	//第一次连接；连接设备ap；
								// 检测当前连上的ssid是否就是传入的ssid,以此判断是否连上,恢复wifi并中断操作
								publishProgress(4);//正在连接智能设备
								lastErrMsg = "连接设备AP中...";
								MyLog.d(TAG,"Now:devssid="+wifiAdmin.getSSID()+",bssid="+wifiAdmin.getBSSID()+";need="+apidstr);}
						}
						sleepindex=0;
						if (i == 0) {
							lastErrMsg = "切换信道后连接AP超时....";			
							udpClient.close();
							return false;
						}
					}
					if (wifiAdmin.getSSID().equals(apidstr)) {
						publishProgress(6);//正在发送配置信息到智能设备
						msg = new JSONObject();
						msg.put("action", "setap");
						msg.put("ssid", selsect_wifi_btn.getText().toString());
						msg.put("pwd", et1.getText().toString());
						MyLog.i("cfg", "正在发送配置信息到AP设备..");
						MyLog.i(TAG,"set ap: ssid=" + selsect_wifi_btn.getText().toString()+",pwd=" + et1.getText().toString());
						result = udpClient.send(msg, 30);
						MyLog.i("result", "resul2= "+result);
						MyLog.i(""+aprouteid, "aprouteid====="+aprouteid+"   routeid====="+routeid);
						if (aprouteid==routeid) {
							MyLog.i(TAG, "信道一样");
						}else{
							MyLog.i(TAG, "信道不一样，断开重连了");
						}
						publishProgress(8);//设备正在连接路由器和服务器
						// 检测设备状态，只限制30秒内完成
						iap = 50;
						while (iap > 0) {
							Thread.sleep(500);
							iap--;
							msg = new JSONObject();
							msg.put("action", "check");
							result = udpClient.send(msg, 3);
							MyLog.i("result", "resul3= "+result);
							if (result != null) {
								if (result.getInt("status") == 0) {
									publishProgress(9);//智能设备连接服务器成功
									break;
								} else if (result.getInt("status") == 1) {
									publishProgress(3);//正在连服务器
								} else if (result.getInt("status") == 2) {
									publishProgress(1);//wifi模块空闲；
								} else if (result.getInt("status") == 3) {
									publishProgress(10);//正在连路由器...
								} else if (result.getInt("status") == 4) {
									lastErrMsg = "WIFI密码错误";
									renewwifi();
									return false;
								} else if (result.getInt("status") == 5) {
									lastErrMsg = "WIFI密码错误或WIFI热点不存在！";
									renewwifi();
									return false;
								} else if (result.getInt("status") == 6) {
									lastErrMsg = "连接路由器失败！";
									renewwifi();
									return false;
								} else if (result.getInt("status") == 7) {
									publishProgress(11);//连上路由器，并获取了IP地址
								} else {
									// 返回1 标识错误的
									lastErrMsg = "设备端未知错误!请重置设备重试";
									renewwifi();
									return false;
								}
							}
							if (iap == 0) {
								lastErrMsg = "设备没有连接上网,重置设备重试一下?";
								renewwifi();
								return false;
							}
						}
					}else{
						renewAPwifi();
					}
					// 重新连原来的wifi；
					MyLog.i("cfg", "// 重新连原来的wifi；此时设备已经连接上路由器");
					String ssidtv=selsect_wifi_btn.getText().toString().trim(); 
					MyLog.i(ssidtv, "name"+ssidtv+"pwd"+et1.getText().toString().trim());
					publishProgress(13);//正在恢复网络
					renewwifi();
					// 更新一下状态
					i = 60;
					while (i>0) {
						Thread.sleep(500);
						i--;
						// 更新一下状态
						wifiAdmin.updateWifiInfo();
						if (wifiAdmin.getSSID()!=null&&wifiAdmin.getSSID().equals(ssidtv)) {    
							if (wifiAdmin.getIPAddress()!=0) {
								publishProgress(14);//恢复网络成功
								MyLog.i("cfg", "恢复wifi成功...");
								MyLog.i(ssid,"getSSID="+ssid+",ssidtv="+ssidtv+";need="+wifiAdmin.getIPAddress());
								MyLog.i("cfg", "正在绑定设备...");
								break;
							} else {
								MyLog.i(ssid,",ssidtv="+ssidtv+";need="+wifiAdmin.getIPAddress()+"  need="+wifiAdmin.getSSID());
								MyLog.i("cfg", "获取IP中....!");
							}
						}else{
							MyLog.i("cfg", "连接设备AP中......");
							MyLog.i("AP",",ssidtv="+ssidtv+"  need="+wifiAdmin.getSSID());
						}
						if (i==20) {
							int codetype=BrightnessTools.getAPNType(getApplicationContext());
							if (codetype==2||codetype==3) {
								MyLog.i("使用移动数据网络绑定");
								break;
							}
						}
					}
					if (i == 0) {
						lastErrMsg = "恢复wifi网络超时，请重置设备重试！";
						renewwifi();
						return false;
					}
					Thread.sleep(500);
					if (ssid!=null) {
						IotUser user = new IotUser(DeployActivity.this);
//					if (bssid!=null) {
//						// 执行绑定操作
//						publishProgress(15);//正在绑定设备;
//						IotUser user = new IotUser(DeployActivity.this);
//						MyLog.i("cfg", "绑定设备中！");
//						//去掉bssid的冒号；
//						final String[] split = bssid.split(":");
//						StringBuffer buffer= new StringBuffer();
//						for (String ss : split) {
//							String replaceAll= ss.replaceAll("^,|,$", "");
//							if (replaceAll.length() > 0) {
//								//叠加字符串；
//								buffer.append(replaceAll);
//							}
//						}
//						///绑定设备
//						MyLog.i(TAG, buffer.toString());
//						int code=user.bindDevsm(buffer.toString());
						
						int code=user.bindDev(ssid);
						//恢复登录
						Intent sendstartsevice = new Intent();  
						sendstartsevice.setAction("startserivce");  
						sendBroadcast(sendstartsevice);  
						ib = 50;
						while(ib>0){
							ib--;
							Thread.sleep(200);  
							if (code==0) {
								publishProgress(16);//正在绑定设备
								lastErrMsg = "恭喜您，一键配置成功!";
								Thread.sleep(1000);  
								return true;
							}else if (code==202) {
								lastErrMsg = "设备不存在，检查设备是否对应相应的服务器和APP!";
								return false;
							}else if (code==201) {
								lastErrMsg = "设备已经被其他用户绑定，先解除绑定，请重置设备重试";
								return false;
							}else if (code==103) {
								lastErrMsg = "Token令牌验证失败，请重新登录，此时可能用一账号在两台手机上同时登录了；，请重置设备重试";
								return false;
							}
						}
						if (ib==0) {
							lastErrMsg = "服务器异常或无法连接网络，请重置设备重试";
							renewwifi();
							return false;
						}
					}else{
						lastErrMsg = "绑定的设备名称为空看，请重置设备重试";
					}
				}else{
					finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return false;
		}
	}
	public void  renewwifi(){
		String ssidtv=selsect_wifi_btn.getText().toString().trim();
		wifiAdmin.CreateWifiInfo(ssidtv, et1.getText().toString().trim());
		MyLog.i("wifiname= "+ssidtv+"  pwd=="+et1.getText().toString());
	}
	public void  renewAPwifi(){
		MyLog.i("wifiname", "  apidstr=="+apidstr);
		wifiAdmin.CreateWifiInfo(apidstr, "12345678");
	}
	public class UDPClient {
		private static final int SERVER_PORT = 8090;
		private DatagramSocket dSocket = null;
		private InetAddress local = null;
		/**
		 * @param msg
		 */
		public UDPClient() {
			try {
				dSocket = new DatagramSocket();
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
		public void close() {
			dSocket.close();
		}

		/**
		 * 发送UDP报文   发送给AP设备；
		 * @param jsonmsg JSON对象类型的数据
		 * @param timeout 设定超时秒
		 * @return
		 * @throws InterruptedException 
		 */
		public JSONObject send(JSONObject jsonmsg, int timeout) throws InterruptedException {
			byte[] message = new byte[1024];
			DatagramPacket rcvPacket = new DatagramPacket(message,
					message.length);
			String msg = jsonmsg.toString();
			try {
				// 发送报文
				local = InetAddress.getByName("192.168.4.1");
				int msg_len = msg == null ? 0 : msg.length();
				DatagramPacket dPacket = new DatagramPacket(
						msg.getBytes("gbk"), msg_len, local, SERVER_PORT);
				MyLog.d("lport", String.valueOf(dSocket.getLocalPort()));
				if (timeout > 0) {
					dSocket.setSoTimeout(timeout * 1000);// 超时
				}
				dSocket.send(dPacket);
				MyLog.i(TAG, "已发送信息到设备");
				// 阻塞接收报文
				while (true) {
					MyLog.i(TAG, "正在接收");
					Thread.sleep(200);
					dSocket.receive(rcvPacket);
					JSONObject udpResult = new JSONObject(new String(
							rcvPacket.getData(), "gbk"));
					MyLog.i("received:" + rcvPacket.getLength(),
							udpResult.toString());
					return udpResult;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	//圆形进度条显示；
	class ProgressRunable implements Runnable {
		@Override
		public void run() {
			mTotalProgress = 100;
			mCurrentProgress = 0;
			while (mCurrentProgress < mTotalProgress) {
				mCurrentProgress += 1;
				tasks_view.setProgress(mCurrentProgress);
				try {///20000*100
					Thread.sleep(200);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
}
