package cn.air.doopen.socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;  
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.Handler.Callback;
import cn.air.doopen.config.Deploy;
import cn.air.doopen.config.RadioName;
import cn.air.doopen.freagment.Facility_ft;
import cn.air.doopen.hy.R;
import cn.air.doopen.hy.share.UserMessageActivity;
import cn.air.doopen.hyapp.MyApp;
import cn.air.doopen.logic.BindingListener;
import cn.air.doopen.logic.ControlToOfficialOrTestServer;
import cn.air.doopen.utli.MyLog;
import cn.air.doopen.wifi.WifiAdmin;
import android.os.IBinder;
import android.os.Message;

/**
 * 后台服务,管理SOCKET长连接的登录,挂断和心跳维持,处理其他调用者的收发数据请求
 * 
 * @author xwm
 * 
 */
public class TcpCommSerivce extends Service implements Callback, RadioName,	Deploy {

	private static final String TAG = "TcpCommSerivceSerivce:";
	private final int NTID = 1;// 状态栏消息id

	public static final int STAT_INIT = 0; // 初始状态
	public static final int STAT_ONLINE = 1; // 上线了
	public static final int STAT_OFFLINE = 2; // 离线了
	public static final int STAT_DESS = 2; // 离线了
	// 线程事件
	public static final int EVENT_LOGIN_OK = 0;
	public static final int EVENT_LOGIN_FAIL = 1;

	private int mStatus = STAT_INIT; // 当前状态
	private String token = null; // 登录令牌
	private long lastTimeData = 0;// 最后一次发心跳包消息的时间
	private MyBinder myBinder = new MyBinder();
	private SocketChannel socketChannel = null; // socket通道
	ByteBuffer outBuf = ByteBuffer.allocate(256); // 接收缓存区
	ByteBuffer inBuf = ByteBuffer.allocate(256); // 发送缓存区
	private Handler handler = null;
	private TcpKeepThread keepThread;
	private boolean aliveFlag = true;
	private RecvThread myRecThread;
	private HeartThread myHeartThread;
	private boolean isNeedreLogin;
	private boolean reLoginFlag;
	@Override
	public void onCreate() {
		super.onCreate();
		IntentFilter filter = new IntentFilter();
		filter.addAction("distserivce");
		filter.addAction("startserivce");
		distserivce receiver = new distserivce();
		registerReceiver(receiver, filter);
		MyLog.i(TAG, "onCreate: 创建服务");
		handler = new Handler(this);
		lastTimeData = new Date().getTime();
		// 先打开一个无连接的通道
		try {
			socketChannel = SocketChannel.open();
		} catch (IOException e) {
			MyLog.e(TAG, "打开通道失败!");
			e.printStackTrace();
		}
		myRecThread=new RecvThread();
		myHeartThread=new HeartThread();
		// 一个守护线程,保证不掉线
		keepThread = new TcpKeepThread();
		keepThread.start();
		MyApp myapp = (MyApp) getApplication();
		myapp.isRunning = true;
		MyLog.i("oncreate方法执行了");
	}
	// 判断外网联通性
	public final boolean ping() {

		String result = null;
		try {
			String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
			Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
			// 读取ping的内容，可以不加
			InputStream input = p.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			StringBuffer stringBuffer = new StringBuffer();
			String content = "";
			while ((content = in.readLine()) != null) {
				stringBuffer.append(content);
			}
			MyLog.d("------ping-----", "result content : " + stringBuffer.toString());
			// ping的状态
			int status = p.waitFor();
			if (status == 0) {
				result = "success";
				return true;
			} else {
				result = "failed";
			}
		} catch (IOException e) {
			result = "IOException";
		} catch (InterruptedException e) {
			result = "InterruptedException";
		} finally {
			MyLog.d("----result---", "result = " + result);
		}
		return false;
	}



	// 保活线程
	class TcpKeepThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				while (aliveFlag) {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (mStatus == STAT_ONLINE) {
						if(reLoginFlag){
							MyLog.i("断开了连接");
							try{
								socketChannel.close();
							} catch (IOException e1) {
								MyLog.i(TAG, "关闭Socket 出错!!!");
								e1.printStackTrace();
							}
							isNeedreLogin=reLoginFlag;
							reLoginFlag=false;
						}
						if (socketChannel != null&& socketChannel.isConnected()) {
							// 在线的
							MyLog.i(TAG, "Socket 已经正常连接并工作!!! do nothing!");
						} else {
							MyLog.i("--------222------");
							boolean state=WifiAdmin.isWiFiActive(MyApp.getContext());
							if (state==true) {
								MyLog.i("。。=网络可可可用的=");
							}else{
								MyLog.i("。=网络不不不用的=");
							}
							MyLog.i(TAG, "Socket 失效或连接已经中断!!!");
							if (socketChannel.isConnected()) {
								MyLog.i(TAG, "Socket 连接已经中断!!!");
							} else {
								MyLog.i(TAG, "Socket 失效!!!");
							}
							if (isNetworkAvailable()) {
								MyLog.i("--------333------");
								// 有可用网络,发起登录
								MyLog.i(TAG, "对异常掉线发起重登录");
								// 一个守护线程,保证不掉线
								new LoginThread().execute();
								if (!ping()) {
									MyLog.i(TAG, "网络Ping不通");
								}
							} else {
								MyLog.i(TAG, "无可用网络");
							}
						}

					}else{
						MyLog.i(TAG, "网络: STAT_OFFL INE");
					}
				}
			}
		}
	}
	//判断是否连接；
	public boolean isNetworkAvailable() {
		Context context = this.getApplicationContext();
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		} else {
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					MyLog.i(TAG, i + ", 类型:" + networkInfo[i].getTypeName()
							+ ", 状态:" + networkInfo[i].getState());
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// 心跳包线程
	class HeartThread extends Thread {
		@Override
		public void run() {
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);
			while (mStatus == STAT_ONLINE) {
				try {
					Thread.sleep(10000);
					boolean state=WifiAdmin.isWiFiActive(MyApp.getContext());
					if (state==true) {
						MyLog.i("=网络可可可用的=");
					}else{
						MyLog.i("=网络不不不用的=");
					}
					if ((new Date().getTime() - lastTimeData) > 25000) {
						MyLog.i(TAG, "发心跳包...");
						if (socketChannel.isConnected()) {
							// 虽然这里判断连接否,但可能通道关闭, isOpen ?
							inBuf.clear();
							inBuf.put(new byte[] { 2, 0 });
							inBuf.flip();
							socketChannel.write(inBuf);
							MyLog.i(TAG, "发送心跳包成功");
						} else {
							MyLog.e(TAG, "发送心跳包失败，已断开连接");
						}

						lastTimeData = new Date().getTime();
					}
				} catch (InterruptedException e) {
					MyLog.e(TAG, "发送心跳出错1");
					e.printStackTrace();
				} catch (IOException e) {
					MyLog.e(TAG, "发送心跳出错2,关闭通道");
					try {
						socketChannel.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}
		}
	}

	// 发送数据的线程
	class SenderThread extends Thread {

		private ByteBuffer buf;

		public SenderThread(ByteBuffer buf) {
			this.buf = buf;
		}

		@Override
		public void run() {
			try {
				if (socketChannel.isConnected()) {
					buf.flip();
					int writesize=socketChannel.write(buf);
					MyLog.i("write_size=="+writesize);
					byte[] datas = buf.array();
					if (datas != null) {
						String str = "";
						for (int i = 0; i < writesize; i++) {
							str += (Integer.toHexString(datas[i]) + " ");
						}
						if (str != null) {
							MyLog.i(TAG, "发送的的==="+str);
						}
					}
					buf.clear();
					MyLog.i(TAG, "发送数据到服务器");
				}
			} catch (Exception e) {
				MyLog.e(TAG, "发送数据异常,关闭通道");
				try {
					socketChannel.close();
				} catch (IOException e1) {
					MyLog.e(TAG, "关闭SOCKET异常！！！");
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}

	// 执行登录的线程
	class LoginThread extends AsyncTask<Void, Void, Boolean> {
		boolean status = false;
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			MyLog.i("result=="+result);
			if (result) {
				MyLog.i("被踢掉了");
				isNeedreLogin = false;
				//发起异常控制；
				BindingListener.abnormity_control(myBinder);
			}else{
				MyLog.i("没有被踢掉");
			}
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				MyLog.i(TAG, "创建Socket对象并连接");
				InetSocketAddress isa = new InetSocketAddress(new ControlToOfficialOrTestServer().socket_url(), 8000);
				if (socketChannel == null || !socketChannel.isOpen()) {
					// 如果通道未开启,先开启通道
					socketChannel = SocketChannel.open(); // 建立一个socket通道
					socketChannel.configureBlocking(true); //true为阻塞模式；
				}
				if (!socketChannel.isConnected()) {
					// 如果未连接
					if (isa != null) {
						status = socketChannel.connect(isa); // 建立一个socket连接
						socketChannel.socket().setTcpNoDelay(true);
						socketChannel.socket().setKeepAlive(true);
						//读取数据超时；
						//						socketChannel.socket().setSoTimeout(3000);  
					}
				} else {
					status = true;
				}
				// 连接状态正常的情况下,开始登录操作
				if (status) {
					MyLog.i(TAG, "SOCKET 网络连接成功!!!");
					if (token.length() > 0) {
						// 获取packagemanager的实例
						PackageManager packageManager = getPackageManager();
						// getPackageName()是你当前类的包名，0代表是获取版本信息
						PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
						String version = packInfo.versionName;
						MyLog.i(TAG, version);
						inBuf.clear();
						inBuf.put(new byte[] { 48, 1, 1, 0, 0, 0, 0, 40 });
						MyLog.i(TAG, "tokengbk=  " + token.getBytes("gbk"));
						inBuf.put(token.getBytes("gbk"));
						inBuf.flip();
						socketChannel.write(inBuf);
						outBuf.clear();
						socketChannel.read(outBuf);
						outBuf.flip();
						byte len = outBuf.get();
						byte cmd = outBuf.get();
						byte ret = outBuf.get();
						MyLog.i(TAG, "Socket登录返回：len=" + len + ",cmd=" + cmd + ",ret=" + ret);
						outBuf.clear();
						if (cmd == 1 && ret == 0) {
							MyLog.i(TAG, "登录成功");
							// 发送消息
							handler.sendEmptyMessage(EVENT_LOGIN_OK);
							mStatus = STAT_ONLINE;
							MyLog.i("isNeedreLogin=="+isNeedreLogin);
							if (isNeedreLogin==true) {
								return true;
							} 
						} else {
							isNeedreLogin = false;
							MyLog.e(TAG, "登录失败(不是出错)");
							// 也是发消息
							handler.sendEmptyMessage(EVENT_LOGIN_FAIL);
						}
					}
				}
			} catch (Exception e) {
				MyLog.e(TAG, "登录出错,关闭通道");
				try {
					socketChannel.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
			return false;
		}
	}

	// 接收和处理数据线程
	class RecvThread extends Thread {
		@Override
		public void run() {
			//设置优先级，
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
			try {
				while (mStatus == STAT_ONLINE&&reLoginFlag==false) {
					MyLog.i("--------444------");
					if (socketChannel.isConnected()) {
						outBuf.clear();
						int size=socketChannel.read(outBuf);
						MyLog.i("===read===size=="+size);
						if (size==0||size==-1) {  
							outBuf.clear();
							MyLog.i("返回了0");      
							reLoginFlag = true;
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						if (size> 0) {
							byte[] datas = outBuf.array();
							if (datas != null) {
								//循环打印，耗时操作；
								String str = "";
								for (int i = 0; i <size; i++) {
									str += (Integer.toHexString(datas[i]) + " ");
								}
								if (str != null) {
									MyLog.i(TAG, "接收的==="+str);
								}
								outBuf.flip();
								if(!outBuf.equals("")){
									// 处理数据包
									Byte packLen = outBuf.get();
									MyLog.i(TAG,  "第一个字节=="+packLen+",长度===" + outBuf.limit());
									if (packLen == outBuf.limit()) {
										// 验证字节数是否对；
										Byte cmdByte = outBuf.get();
										// 处理各自命令
										switch (cmdByte) {
										case (byte) 0:
											MyLog.d(TAG, "发送了心跳包");
										break;
										case (byte) 1:
											MyLog.d(TAG, "登录回应");
										break;
										case (byte) 2:
											MyLog.d(TAG, "登出回应");
										break;
										case (byte) 3:
											// 一条控制消息
											byte[] data = new byte[256];
										int from = outBuf.getInt();
										int to = outBuf.getInt();
										// 开始数据
										outBuf.get(data, 0, outBuf.remaining());
										Intent intent = new Intent(CN_XIEWEIMING_APP_TCPCOMM);
										intent.putExtra("from", from);
										intent.putExtra("to", to);
										intent.putExtra("data", data);
										sendBroadcast(intent);
										outBuf.clear();
										break;
										case 4:
											// 升级消息,系统要弹出一个通知
											showNotification("有新版本发布", "可以升级了", Facility_ft.class);
											break;
										case 5:
											// 消息
											byte message_type = outBuf.get(); // 消息类型；
											switch (message_type) {
											case 0:
												showNotification("有新消息", "点击查看",	Facility_ft.class);
												break;
											case 1:
												showNotification("设备邀请",
														"有用户邀请您分享他的设备，点击同意吧！",
														UserMessageActivity.class);
												break;
											case 2:
												Intent offline = new Intent(
														COM_IMAGINE_INFENONE_AGREE); // 发送离线广播；
												sendBroadcast(offline);
												break;
											}
											break;
										case 6:
											// 查询消息的回应
											break;
										case 7: // 某个设备上线或下线了,需要刷新一下设备列表的状态
											((MyApp) getApplication()).isNeedRefreshDevList = true;
											byte state_default = outBuf.get();// 设备是否在线默认值；如果为1表示在线；如果为2表示离线；分别判断；并且做出不同的处理；如果离线需要重新获取设备信息更新才可以重新控制设备；主要通过发送广播的形式来完成此操作；
											MyLog.i(TAG, "设备是否在线" + state_default);
											if (state_default == 1) {
												byte type = outBuf.get(); // 设备类型；
												int sql_number = outBuf.getInt(); // 此设备在数据库中的编号；
												int connective = outBuf.getInt(); // 连接号；
												MyLog.i(TAG, "type=" + type
														+ "on_clientID="
														+ connective + "on_devid"
														+ sql_number);
												Intent onlinecmd = new Intent(
														CN_XIEWEIMING_APP_TCPCOMM_ONLINE); // 发送在线广播；
												onlinecmd.putExtra("on_clientID",
														connective);
												onlinecmd.putExtra("on_devid",
														sql_number);
												sendBroadcast(onlinecmd);
												outBuf.clear();
											}
											if (state_default == 2) {
												Intent offline = new Intent(); // 发送离线广播；
												offline.setAction(COM_IMAGINE_INFENONE_SOCKET_TCPCOMM_OFFINE);
												sendBroadcast(offline);
												outBuf.clear();
											}
											if (state_default == 3) {
												MyLog.i("这是应该发送广播", "设备已在其他地方登陆官博");
												Intent offline = new Intent(); 
												offline.setAction(COM_IMAGINE_INFENONE_SOCKET_TCPCOMM_ANEW);
												sendBroadcast(offline);
												outBuf.clear();
											}
											break;
										case 9:
											// 被服务器踢掉时；
											MyLog.i("cfg", "消息出错，服务器中断了通信！！！！");
											reLoginFlag = true;
											MyLog.i("--------111------");
											break;
										default: 
											MyLog.i(TAG, "未识别的通信指令");
											break;
										}   
									}
									outBuf.clear();
								}
							}
						}
					}
				}

			} catch (IOException e) {
				MyLog.i(TAG, "接收数据IO异常,关闭通道");
				e.printStackTrace();
				try {
					socketChannel.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		MyLog.i("TrafficService", "startCommand");
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onStart(Intent intent, int startId) {
		aliveFlag = true;
		mStatus = STAT_ONLINE;
		MyLog.i(TAG, "onStart");
	}
	//控制启动；
	class distserivce extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("distserivce")) {
				aliveFlag = false;
				MyLog.i(TAG, "已停止serivce");
			}
			if (action.equals("startserivce")) {
				aliveFlag = true;
				MyLog.i(TAG, "已开启serivce");
			}
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		MyLog.i(TAG, "onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		Intent localIntent = new Intent();
		localIntent.setClass(this, TcpCommSerivce.class); // 销毁时重新启动Service
		this.startService(localIntent);
		super.onDestroy();
		MyApp myapp = (MyApp) getApplication();
		myapp.isRunning = true;
		MyLog.i(TAG, "服务已销毁");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		MyLog.i(TAG, "服务被绑定");
		return myBinder;
	}

	// 绑定类(外部接口)
	public class MyBinder extends Binder {

		// 发送数据
		public boolean send(ByteBuffer buf) {
			new SenderThread(buf).start();
			MyLog.i(TAG, "启动发送线程");
			return true;
		}

		// 登录
		public void login(String token) {
			if (!socketChannel.isConnected()) {
				TcpCommSerivce.this.token = token;
				new LoginThread().execute();
				MyLog.i(TAG, "启动登录线程");
			} else {
				MyLog.w(TAG, "SOCKET 未连接，启动登录线程失败！！！");
			}
		}

		// 重新登录
		public void relogin() {

		}

		// 获取使用的token
		public String getToken() {
			return token;
		}

		// 退出连接
		public void logout() {
			try {
				if (socketChannel.isConnected()) {
					if (mStatus == STAT_ONLINE) {
						// 如果当前连接的,发送退出命令,然后断开连接
						mStatus = STAT_OFFLINE;
						socketChannel.close();
						MyLog.i(TAG, "关闭SOCKET 连接！！！");
					} else {
						MyLog.w(TAG, " 当前为 STAT_OFFLINE 状态，无需操作！");
					}
				} else {
					MyLog.w(TAG, "SOCKET已经关闭，无需再次关闭！");
				}

			} catch (IOException e) {
				MyLog.e(TAG, "关闭SOCKET 连接发生异常！！！");
				e.printStackTrace();
			}
		}

		// 检查状态(被设定状态)
		public int checkStatus() {
			return mStatus;
		}

		// 判断通道实际是否连接
		public Boolean isConnected() {
			if (socketChannel != null && socketChannel.isConnected()) {
				return true;
			} else {
				return false;
			}
		}

		// 退出服务
		public void exit() {
			stopSelf();
		}

		public TcpCommSerivce getService() {
			return TcpCommSerivce.this;
		}
	}
	//接收消息；
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case EVENT_LOGIN_OK:
			mStatus = STAT_ONLINE;
			if (myRecThread.getState() == Thread.State.TERMINATED) {
				myRecThread = new RecvThread();
				myRecThread.setPriority(Thread.MAX_PRIORITY); // 10  
				myRecThread.start();
			} else {
				if (!myRecThread.isAlive()) {
					myRecThread.start();
				}
			}
			//TERMINATED=终止；如果当前线程被终止了；
			if (myHeartThread.getState() == Thread.State.TERMINATED) {
				myHeartThread = new HeartThread();
				myHeartThread.setPriority(Thread.MIN_PRIORITY); // 1  
				myHeartThread.start();
			} else {
				// 判断mThread 是不是正在运行
				if (!myHeartThread.isAlive()) {
					myHeartThread.start();
				}
			}
			MyLog.i(TAG, "开启接收线程和心跳线程！");
			break;
		case EVENT_LOGIN_FAIL:
			MyLog.e(TAG, "线程登录失败");
			break;
		}
		return false;
	}
	// 发出通知
	public void showNotification(String ticker, String content, Class<?> taget) {
		Notification.Builder builder = new Notification.Builder(
				getApplicationContext());
		Intent notificationIntent = new Intent(TcpCommSerivce.this, taget);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				TcpCommSerivce.this, 0, notificationIntent, 0);
		builder.setContentTitle("家电在联");
		builder.setContentText(content);
		builder.setContentIntent(pendingIntent);
		builder.setSmallIcon(R.drawable.logo5);
		builder.setTicker(ticker);
		Notification notification = builder.getNotification();
		notification.flags |= Notification.FLAG_AUTO_CANCEL
				| Notification.FLAG_ONLY_ALERT_ONCE;
		notification.defaults |= Notification.DEFAULT_SOUND;
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.notify(NTID, notification);
	}

}
