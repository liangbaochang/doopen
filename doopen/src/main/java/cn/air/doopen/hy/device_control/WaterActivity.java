package cn.air.doopen.hy.device_control;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import cn.air.doopen.adapter.FlierSimpleAdapter;
import cn.air.doopen.config.RadioName;
import cn.air.doopen.hy.R;
import cn.air.doopen.hy.device_control.water_control_tool.Water_control_tool;
import cn.air.doopen.hy.device_control.water_control_tool.Water_setingActivity;
import cn.air.doopen.hy.share.ShareDevActivity;
import cn.air.doopen.socket.TcpCommSerivce;
import cn.air.doopen.utli.ActivityManager;
import cn.air.doopen.utli.MyLog;

//净水器控制界面；
@ContentView(value = R.layout.activity_ledctrl)
public class WaterActivity extends Activity implements OnClickListener ,RadioName{
	@ViewInject(R.id.water_wash_btn)     /////
	private Button water_wash_btn;                 /////
	@ViewInject(R.id.water_producing_btn)            /////
	private Button water_producing_btn;                         /////
	@ViewInject(R.id.water_full_btn)                                         /////状态显示；
	private Button water_full_btn;                                       /////    
	@ViewInject(R.id.water_lack_btn)                         /////
	private Button water_lack_btn;                         /////
	@ViewInject(R.id.water_recondition_btn)/////
	private Button water_recondition_btn;  /////               

	@ViewInject(R.id.share_result)/////
	private Button share_result;  /////               

	@ViewInject(R.id.seting_imgbtn)/////
	private ImageButton seting_imgbtn;  /////               
	@ViewInject(R.id.share_imbtn_dev)/////
	private ImageButton share_imbtn_dev;  /////               


	@ViewInject(R.id.war_water_TDS)
	private TextView war_water_TDS;
	@ViewInject(R.id.aqwa_water_TDS)
	private TextView aqwa_water_TDS;       //原水，纯水值显示；

	@ViewInject(R.id.water_quality)
	private TextView water_quality;// 水质；

	@ViewInject(R.id.water_cto_spinnar)
	private Spinner water_cto_spinnar; ///滤芯值显示；

	@ViewInject(R.id.water_wash_true_false_togg)
	private ToggleButton water_wash_true_false_togg;  //控制冲水；
	@ViewInject(R.id.openclose)
	private ToggleButton openclose;      //控制开关机；

	private static final String TAG = "WaterActivity";
	private MyReceiver receiver = null;
	private TcpCommSerivce.MyBinder myBinder;
	/**指令字低位*/
	private byte acmdL;
	/**指令字高位*/
	private byte cmdH;
	/**指令字*/
	private short cmd =-1; //指令字；
	/**连接号*/
	private int clientID = -1; //连接号；
	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			myBinder = (TcpCommSerivce.MyBinder) service;
			Intent intent = new Intent(COM_IMAGINE_INFENONE_UI);
			sendBroadcast(intent);
		}
	};
	private int id_devid;
	private String devkey_id;
	private AnimationDrawable animationDrawable;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		ActivityManager.getInstance().addActivity(this);
		ViewUtils.inject(this);
		initdata();
		openclose.setBackgroundResource(R.anim.pb_loading);
		animationDrawable=(AnimationDrawable) openclose.getBackground();
		animationDrawable.start();
		water_wash_true_false_togg.setBackgroundResource(R.anim.pb_loading2);
		animationDrawable=(AnimationDrawable) water_wash_true_false_togg.getBackground();
		animationDrawable.start();
		water_wash_true_false_togg.setClickable(false);
	}
	private void initdata() {
		openclose.setOnClickListener(this);
		water_wash_true_false_togg.setOnClickListener(this);
		share_result.setOnClickListener(this);
		share_imbtn_dev.setOnClickListener(this);
		seting_imgbtn.setOnClickListener(this);
		Intent intent1 = WaterActivity.this.getIntent();
		clientID = intent1.getIntExtra("clientID", -1);
		id_devid = intent1.getIntExtra("id_devid", -1);
		devkey_id = intent1.getStringExtra("devkey_id");
		MyLog.i(TAG, "clientID=" + clientID);
	}
	@Override
	protected void onResume() {
		super.onResume();
		// 注册广播接收器,接收来自TcpCom的广播；
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(COM_IMAGINE_INFENONE_SOCKET_TCPCOMM_ONLINE);
		filter.addAction(CN_XIEWEIMING_APP_TCPCOMM);
		filter.addAction(COM_IMAGINE_INFENONE_UI);
		registerReceiver(receiver, filter);
		// 绑定服务；
		Intent ab = new Intent(this, TcpCommSerivce.class);
		bindService(ab, connection, 0);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 注销监听器
		unregisterReceiver(receiver);
		// 解绑服务
		unbindService(connection);
	}

	/**
	 * 获取广播数据
	 */
	public class MyReceiver extends BroadcastReceiver {
		private ArrayList<Map<String, Object>> list;
		private FlierSimpleAdapter simpleAdapter;
		@Override
		public void onReceive(Context context, Intent actionreceiver) {
			String action = actionreceiver.getAction();
			if (action.equals(COM_IMAGINE_INFENONE_SOCKET_TCPCOMM_ONLINE)) {
				Bundle bundle = actionreceiver.getExtras();
				clientID = bundle.getInt("on_clientID");
				new sendshare().execute(null,null,null);
			}
			if (action.equals(CN_XIEWEIMING_APP_TCPCOMM)) {
				//同样，在读取SharedPreferences数据前要实例化出一个SharedPreferences对象
				Bundle bundle = actionreceiver.getExtras();
				// 只处理发给我的消息
				MyLog.i(TAG, "clientID=" + clientID + " from=" + bundle.getInt("from"));
				if (clientID != bundle.getInt("from")) {
					MyLog.i(TAG, "收到不是给我的消息");
					return;
				} else {
					MyLog.i(TAG, "收到给我的消息");
					ByteBuffer buf = ByteBuffer.allocate(256);
					buf.clear();
					byte[] data = bundle.getByteArray("data");// 数据
					buf.put(data);
					buf.flip();
					byte statecode1 = buf.get();
					byte statecode2 = buf.get();
					Short statecode = (short) ((statecode2 << 8) | statecode1);// 指令移位转换；
					// 状态返回码；
					byte datalen1 = buf.get();
					byte datalen2 = buf.get();
					Short datalen = (short) ((datalen2 << 8) | datalen1);// 指令移位转换；
					// 长度
					buf.getLong(); // 设备码；
					acmdL = buf.get();// 指令低位；
					cmdH = buf.get();// 指令高位；
					cmd = (short) ((cmdH << 8) | acmdL);// 指令移位转换；
					switch (cmd) {
					case 1:
						Water_control_tool.returndata(clientID, acmdL, cmdH, myBinder);
						Byte isopen = buf.get(); // 显示设备是否开机
						Byte code = buf.get();// 1:制水2:缺水3:待机4:检修5:冲洗6:欠费7:关机8:漏水
						Byte isuv = buf.get();// UV灯开关状态
						MyLog.i(TAG + "接收数据",
								"指令=" + cmd + " 显示设备是否开机 = isopen=" + isopen + " =code=" + code + "开关状态=isuv" + isuv);
						if (isopen == 1) {
							openclose.setChecked(true);
							animationDrawable.stop();
							openclose.setBackgroundResource(R.drawable.on0);
							openclose.setClickable(true);
							animationDrawable.stop();
							water_wash_true_false_togg.setBackgroundResource(R.drawable.on0);
							water_wash_true_false_togg.setClickable(true);
						} else {
							openclose.setChecked(false);
							animationDrawable.stop();
							openclose.setBackgroundResource(R.drawable.off0);
							openclose.setClickable(true);
							animationDrawable.stop();
							water_wash_true_false_togg.setBackgroundResource(R.drawable.off0);
							water_wash_true_false_togg.setClickable(false);
						}
						switch (code) {
						case 0:
							getviewback(R.drawable.full_water_false, R.drawable.water_filling_false, R.drawable.water_producing_false, R.drawable.lite_water_false, R.drawable.fix_water_false, false);
							water_wash_true_false_togg.setBackgroundResource(R.drawable.off0);
							openclose.setChecked(false);
							break;
						case 1:// 制水；
							getviewback(R.drawable.full_water_false, R.drawable.water_filling_false, R.drawable.water_producing_true, R.drawable.lite_water_false, R.drawable.fix_water_false, false);
							animationDrawable.stop();
							water_wash_true_false_togg.setBackgroundResource(R.drawable.off0);
							water_wash_true_false_togg.setClickable(true);
							break;
						case 2:// 缺水
							getviewback(R.drawable.full_water_false, R.drawable.water_filling_false, R.drawable.water_producing_false, R.drawable.lite_water_true, R.drawable.fix_water_false, false);
							water_wash_true_false_togg.setClickable(false);
							water_wash_true_false_togg.setBackgroundResource(R.drawable.off0);
							break;
						case 3:// 水满；
							getviewback(R.drawable.full_water_true, R.drawable.water_filling_false, R.drawable.water_producing_false, R.drawable.lite_water_false, R.drawable.fix_water_false, false);
							break;
						case 4:// 检修
							getviewback(R.drawable.full_water_false, R.drawable.water_filling_false, R.drawable.water_producing_false, R.drawable.lite_water_false, R.drawable.fix_water_true, false);
							water_wash_true_false_togg.setClickable(false);
							water_wash_true_false_togg.setBackgroundResource(R.drawable.off0);
							break;
						case 5:// 冲洗；
							getviewback(R.drawable.full_water_false, R.drawable.water_filling_true, R.drawable.water_producing_false, R.drawable.lite_water_false, R.drawable.fix_water_false, true);
							animationDrawable.stop();
							water_wash_true_false_togg.setBackgroundResource(R.drawable.on0);
							water_wash_true_false_togg.setClickable(true);
							break;
						case 6:// 欠费；
							break;
						case 7:// 关机；
							getviewback(R.drawable.full_water_false, R.drawable.water_filling_false, R.drawable.water_producing_false, R.drawable.lite_water_false, R.drawable.fix_water_false, false);
							openclose.setChecked(false);
							war_water_TDS.setText("0 ppm");
							aqwa_water_TDS.setText("0 ppm");
							water_wash_true_false_togg.setClickable(false);
							list=null;
							break;
						case 8:// 漏水；
							//							water_wash_true_false_togg.setChecked(false);
							break;
						}
						break;
					case 2:
						Water_control_tool.returndata(clientID, acmdL, cmdH, myBinder);
						byte TDSa = buf.get();
						byte TDSb = buf.get();
						int TDS1 = Water_control_tool.getTransform(TDSb, TDSa);// /
						// 原水值；
						byte TDSd = buf.get();
						byte TDSc = buf.get();
						int TDS2 = Water_control_tool.getTransform(TDSc, TDSd);// 纯水值；
						Byte waterq = buf.get();// 水的质量；
						MyLog.i(TAG + "接收数据", " 指" + " 令字" + cmd + "原水值；=" + TDS1 + " 纯水值 = TDS2=" + TDS2
								+ " 水的质量；=waterq=" + waterq);
						war_water_TDS.setText("" + TDS1 + "  ppm");
						aqwa_water_TDS.setText("" + TDS2 + "  ppm");
						if (waterq == 1) {
							water_quality.setText("水质：优");
						} else if (waterq == 2) {
							water_quality.setText("水质：良");
						} else if (waterq == 3) {
							water_quality.setText("水质：差");
						}
						break;
					case 3:
						if (datalen < 18)
							break;
						int count = (datalen - 18) / 4;// 求出有多少组滤芯；总长度-命令字-设备码-制水累计量-剩余可制水量/4；
						// 生成数据源
						list = new ArrayList<Map<String, Object>>();
						for (int i = 1; i <= count; i++) {
							byte SetVa = buf.get();
							byte SetVb = buf.get();
							int SetV = Water_control_tool.getTransform(SetVb, SetVa); // 滤芯设定值；
							byte CntVa = buf.get();
							byte CntVb = buf.get();
							int CntV = Water_control_tool.getTransform(CntVb, CntVa);// 剩余值；
							MyLog.i(TAG + "接收数据", " 指令字" + count + "n组滤芯设定值；=" + SetV + " n组滤芯累积值= CntV=" + CntV);
							//							每个Map结构为一条数据，key与Adapter中定义的String数组中定义的一一对应。
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("SetV", (int) SetV);
							map.put("CntV", (int) CntV);
							map.put("name", "滤芯" + i);
							list.add(map);
						}
						int WaterCntb = buf.getInt();// 制水累计量
						int Paper = buf.getInt();// 剩余可制水量
						if (list != null) {
							// 声明一个SimpleAdapter独享，设置数据与对应关系
							simpleAdapter = new FlierSimpleAdapter(getApplicationContext(), list);
							// 绑定Adapter到Spinner中
							water_cto_spinnar.setAdapter(simpleAdapter);
						}
						Water_control_tool.returndata(clientID, acmdL, cmdH, myBinder);
						break;
					case 4:
						byte retcontrolinstruction = buf.get();
						ByteBuffer retbuf = ByteBuffer.allocate(256);
						retbuf.put((byte) 0x11); // 消息长度 -6
						retbuf.put((byte) 0x003); // 指令
						retbuf.putInt((int) 0x01); // 发送者
						retbuf.putInt((int) clientID);// 接收者
						retbuf.putShort((short) 0x00);// 返回状态嘛；
						retbuf.putShort((short) 0x0300);/// 长度；
						retbuf.putShort((short) ((short) (acmdL << 8) | cmdH));// 指令字；
						retbuf.put((byte) retcontrolinstruction); // 指令
						myBinder.send(retbuf);
						break;
					}
				}
			} else if (action.equals(COM_IMAGINE_INFENONE_UI)) {
				new sendshare().execute(null,null,null);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.water_wash_true_false_togg:// 冲洗；
			shake();
			water_wash_true_false_togg.setClickable(false);
			Water_control_tool.opendevwash((byte) 2, water_wash_true_false_togg, clientID, myBinder);
			if (water_wash_true_false_togg.isChecked()==true) {
				water_wash_true_false_togg.setBackgroundResource(R.anim.pb_loading);
				animationDrawable=(AnimationDrawable) water_wash_true_false_togg.getBackground();
				animationDrawable.start();
			}else{
				water_wash_true_false_togg.setBackgroundResource(R.anim.pb_loading2);
				animationDrawable=(AnimationDrawable) water_wash_true_false_togg.getBackground();
				animationDrawable.start();
			}
			break;
		case R.id.openclose:// 开关；
			shake();
			openclose.setClickable(false);
			Water_control_tool.opendevwash((byte) 1, openclose, clientID, myBinder);

			if (openclose.isChecked()==true) {
				openclose.setBackgroundResource(R.anim.pb_loading);
				animationDrawable=(AnimationDrawable) openclose.getBackground();
				animationDrawable.start();
			}else{
				openclose.setBackgroundResource(R.anim.pb_loading2);
				animationDrawable=(AnimationDrawable) openclose.getBackground();
				animationDrawable.start();
			}
			break;
		case R.id.share_result:// 返回；
			finish();
			break;
		case R.id.share_imbtn_dev:// 分享设备；
			Intent intent = new Intent(this, ShareDevActivity.class);
			intent.putExtra("devkey",devkey_id);
			intent.putExtra("devid", id_devid);
			startActivity(intent);
			break;
		case R.id.seting_imgbtn:// 设备设置；
			Intent upgrade=new Intent(getApplicationContext(), Water_setingActivity.class);
			startActivity(upgrade);
			break;
		}
	}

	public void getviewback(int a,int b,int c,int d,int e,boolean btn_flag) {
		water_full_btn.setBackgroundResource(a);
		water_wash_btn.setBackgroundResource(b);
		water_producing_btn.setBackgroundResource(c);
		water_lack_btn.setBackgroundResource(d);
		water_recondition_btn.setBackgroundResource(e);
		water_wash_true_false_togg.setChecked(btn_flag);
	}
	public void shake(){
		/* 
		 * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到 
		 * */  
		Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);  
		long [] pattern = {100,100};   // 停止 开启 停止 开启   
		vibrator.vibrate(pattern,-1);           //重复两次上面的pattern 如果只想震动一次，index设为-1   
	}
	class sendshare extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Water_control_tool.sendstatus((short) 1, clientID, myBinder);
				Thread.sleep(100);
				Water_control_tool.sendstatus((short) 2, clientID, myBinder);
				Thread.sleep(100);
				Water_control_tool.sendstatus((short) 3, clientID, myBinder);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

	}
}
