package cn.air.doopen.hy.device_control.xulik;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import cn.air.doopen.config.RadioName;
import cn.air.doopen.hy.R;
import cn.air.doopen.hy.device_control.water_control_tool.Water_control_tool;
import cn.air.doopen.hy.device_control.water_control_tool.Water_setingActivity;
import cn.air.doopen.hy.share.ShareDevActivity;
import cn.air.doopen.hyapp.MyApp;
import cn.air.doopen.socket.TcpCommSerivce;
import cn.air.doopen.utli.MyLog;
import cn.air.doopen.utli.tool.Utils;
import cn.air.doopen.view.MySinkingView;
import cn.air.doopen.view.MySinkingViewSiml;
/**净水器控制界面；这个是使用设备id控制的；*/
@ContentView(value = R.layout.activity_xulik__water)
public class Xulik_WaterActivity_devid extends Activity implements OnClickListener,RadioName{
	@ViewInject(R.id.sinking_desalt)     /////
	private MySinkingView sinking_desalt;                 /////脱盐率
	@ViewInject(R.id.sinking_Doulton_1)     /////
	private MySinkingViewSiml sinking_Doulton_1;                 /////第一级滤芯
	@ViewInject(R.id.sinking_Doulton_2)     /////
	private MySinkingViewSiml sinking_Doulton_2;                 /////第二级滤芯
	@ViewInject(R.id.sinking_Doulton_3)     /////
	private MySinkingViewSiml sinking_Doulton_3;                 /////第三级滤芯
	@ViewInject(R.id.sinking_Doulton_4)     /////
	private MySinkingViewSiml sinking_Doulton_4;                 /////第四级滤芯
	@ViewInject(R.id.sinking_Doulton_5)     /////
	private MySinkingViewSiml sinking_Doulton_5;                 /////第五级滤芯

	@ViewInject(R.id.xulik_share_result)     /////
	private Button xulik_share_result;                 /////返回
	@ViewInject(R.id.xulik_seting_imgbtn)     /////
	private Button xulik_seting_imgbtn;                 /////分享；
	@ViewInject(R.id.xulik_share_imbtn_dev)     /////
	private Button xulik_share_imbtn_dev;                 /////\设置；

	@ViewInject(R.id.sinking_Doulton_1_btn)     /////
	private LinearLayout sinking_Doulton_1_btn;                 /////第一级滤芯
	@ViewInject(R.id.sinking_Doulton_2_btn)     /////
	private LinearLayout sinking_Doulton_2_btn;                 /////第二级滤芯
	@ViewInject(R.id.sinking_Doulton_3_btn)     /////
	private LinearLayout sinking_Doulton_3_btn;                 /////第三级滤芯
	@ViewInject(R.id.sinking_Doulton_4_btn)     /////
	private LinearLayout sinking_Doulton_4_btn;                 /////第四级滤芯
	@ViewInject(R.id.sinking_Doulton_5_btn)     /////
	private LinearLayout sinking_Doulton_5_btn;                 /////第五级滤芯

	@ViewInject(R.id.xulik_state_btn)     /////
	private Button xulik_state_btn;                 /////状态；
	@ViewInject(R.id.xulik_wash_btn)     /////
	private ToggleButton xulik_wash_btn;                 /////冲洗；
	@ViewInject(R.id.xulik_Switch_btn)     /////
	private ToggleButton xulik_Switch_btn;                 /////开关；


	@ViewInject(R.id.xulik_tds_j_txt)     /////
	private TextView xulik_tds_j_txt;                 /////进水TDS值；
	@ViewInject(R.id.xulik_tds_c_txt)     /////
	private TextView xulik_tds_c_txt;                 /////出水TDS值；
	@ViewInject(R.id.xulik_Equipment_Status)     /////
	private TextView xulik_Equipment_Status;                 /////设备在线离线状态；

	@ViewInject(R.id.xulik_content1)     /////
	private cn.air.doopen.view.RippleBackground xulik_content1;                
	@ViewInject(R.id.xulik_content2)     /////
	private cn.air.doopen.view.RippleBackground xulik_content2;        
	@ViewInject(R.id.xulik_state_txt)     /////
	private TextView xulik_state_txt;                 /////出水TDS值；
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
	private int id_devid;
	private String devkey_id;

	private int SetV;

	private int CntV;
	private MyApp myApp;
	public List<Map<String, Integer>> Setvlist = new ArrayList<Map<String, Integer>>();
	public List<Map<String, Integer>> Cntvlist = new ArrayList<Map<String, Integer>>();
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		initdata();
		init();
	}

	private void initdata() {
		Intent intent1 = Xulik_WaterActivity_devid.this.getIntent();
		clientID = intent1.getIntExtra("clientID", -1);
		id_devid = intent1.getIntExtra("id_devid", -1);
		devkey_id = intent1.getStringExtra("devkey_id");
		MyLog.i(TAG, "id_devid=" + id_devid);
		myApp=(MyApp) getApplicationContext();
		xulik_share_result.setOnClickListener(this);
		xulik_seting_imgbtn.setOnClickListener(this);
		xulik_share_imbtn_dev.setOnClickListener(this);
		xulik_wash_btn.setOnClickListener(this);
		xulik_Switch_btn.setOnClickListener(this);
		xulik_wash_btn.setClickable(false);
		sinking_Doulton_1_btn.setOnClickListener(this);
		sinking_Doulton_2_btn.setOnClickListener(this);
		sinking_Doulton_3_btn.setOnClickListener(this);
		sinking_Doulton_4_btn.setOnClickListener(this);
		sinking_Doulton_5_btn.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 绑定服务；
		Intent ab = new Intent(this, TcpCommSerivce.class);
		bindService(ab, connection, 0);
		MyLog.i("onResume", "onResume方法执行了");
		Setvlist.clear();
		Cntvlist.clear();
		// 注册广播接收器,接收来自TcpCom的广播；
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(CN_XIEWEIMING_APP_TCPCOMM_ONLINE);
		filter.addAction(COM_IMAGINE_INFENONE_SOCKET_TCPCOMM_ONLINE);
		filter.addAction(COM_IMAGINE_INFENONE_SOEKET_TCPCOMM_SOCKET_MSG);
		filter.addAction(COM_IMAGINE_INFENONE_SOCKET_TCPCOMM_ANEW);
		filter.addAction(CN_XIEWEIMING_APP_TCPCOMM);

		filter.addAction(COM_IMAGINE_INFENONE_UI);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 注销监听器
		unregisterReceiver(receiver);
		// 解绑服务
		unbindService(connection);
		Water_control_tool.index=0;
	}


	/**
	 * 获取广播数据；接收设备信息；
	 */
	public class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent actionreceiver) {
			String action = actionreceiver.getAction();
			if (action.equals(COM_IMAGINE_INFENONE_SOCKET_TCPCOMM_ANEW)) {
				xulik_Equipment_Status.setText("用户在其他地方登陆,无法控制");
				xulik_Equipment_Status.setTextColor(Color.RED);
			}
			if (action.equals(COM_IMAGINE_INFENONE_SOEKET_TCPCOMM_SOCKET_MSG)) {
				Toast.makeText(getApplicationContext(), "APP和服务器断开了连接，请检查网络设置", Toast.LENGTH_SHORT).show();
			}
			if (action.equals(COM_IMAGINE_INFENONE_SOCKET_TCPCOMM_OFFINE)) {
				Toast.makeText(getApplicationContext(), "设备已离线，暂时无法控制操作！等待设备上线后才可以控制", Toast.LENGTH_SHORT).show();
				xulik_Equipment_Status.setText("离线");
				xulik_Equipment_Status.setTextColor(Color.RED);
			}
			if (action.equals(CN_XIEWEIMING_APP_TCPCOMM_ONLINE)) {
				xulik_Equipment_Status.setText("在线");
				xulik_Equipment_Status.setTextColor(Color.WHITE);

				new sendshare().execute(3,3);
			}
			if (action.equals(CN_XIEWEIMING_APP_TCPCOMM)) {
				Bundle bundle = actionreceiver.getExtras();
				// 只处理发给我的消息
				MyLog.i(TAG, "id_devid=" + id_devid + " from=" + bundle.getInt("from"));
				if (id_devid!=bundle.getInt("from")) {
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
						Water_control_tool.returndata(id_devid, acmdL, cmdH, myBinder);
						Byte isopen = buf.get(); // 显示设备是否开机
						Byte code = buf.get();// 1:制水2:缺水3:待机4:检修5:冲洗6:欠费7:关机8:漏水
						Byte isuv = buf.get();// UV灯开关状态
						MyLog.i(TAG + "接收数据",
								"指令==" + cmd + "开关机==" + isopen + "   冲洗-5，制水-1，设备状态==" + code);
						if (isopen == 1) {
							xulik_content2.stopRippleAnimation();
							xulik_Switch_btn.setChecked(true);
							xulik_wash_btn.setClickable(true);
							xulik_Equipment_Status.setText("在线");
							xulik_Equipment_Status.setTextColor(Color.WHITE);
						} else {
							xulik_content2.stopRippleAnimation();
							xulik_Switch_btn.setChecked(false);
							xulik_wash_btn.setChecked(false);
							xulik_wash_btn.setClickable(false);
							xulik_Equipment_Status.setText("待机");
							xulik_Equipment_Status.setTextColor(Color.YELLOW);
							xulik_state_txt.setText("待机");
						}
						switch (code) {
						case 0:
							break;
						case 1:// 制水；-------
							xulik_content1.stopRippleAnimation();
							xulik_state_btn.setBackgroundResource(R.drawable.xulik_state_5);	xulik_Switch_btn.setChecked(true);xulik_wash_btn.setChecked(false);
							// 列表淡入动画
							xulik_state_btn.startAnimation(AnimationUtils.loadAnimation(
									context, R.anim.fade_in));
							xulik_state_txt.setText("制水");
							break;
						case 2:// 缺水------------
							xulik_content1.stopRippleAnimation();
							xulik_state_btn.setBackgroundResource(R.drawable.xulik_state_2);					xulik_wash_btn.setChecked(false);xulik_Switch_btn.setChecked(true);		
							// 列表淡入动画
							xulik_state_btn.startAnimation(AnimationUtils.loadAnimation(
									context, R.anim.fade_in));
							xulik_state_txt.setText("缺水");
							break;
						case 3:// 水满；------------
							xulik_content1.stopRippleAnimation();
							xulik_state_btn.setBackgroundResource(R.drawable.xulik_state_1);					xulik_wash_btn.setChecked(false);xulik_Switch_btn.setChecked(true);		
							// 列表淡入动画
							xulik_state_btn.startAnimation(AnimationUtils.loadAnimation(
									context, R.anim.fade_in));
							xulik_state_txt.setText("水满");
							break;
						case 4:// 检修---------------
							xulik_content1.stopRippleAnimation();
							xulik_state_btn.setBackgroundResource(R.drawable.xulik_state_3);					xulik_wash_btn.setChecked(false);xulik_Switch_btn.setChecked(true);		
							// 列表淡入动画
							xulik_state_btn.startAnimation(AnimationUtils.loadAnimation(
									context, R.anim.fade_in));
							xulik_state_txt.setText("检修");
							break;
						case 5:// 冲洗；--------------
							xulik_content1.stopRippleAnimation();
							xulik_state_btn.setBackgroundResource(R.drawable.xulik_state_4);					xulik_wash_btn.setChecked(true);xulik_Switch_btn.setChecked(true);		
							// 列表淡入动画
							xulik_state_btn.startAnimation(AnimationUtils.loadAnimation(
									context, R.anim.fade_in));
							xulik_state_txt.setText("冲洗");
							break;
						case 6:// 欠费；
							break;
						case 7:// 关机；-----------
							xulik_wash_btn.setClickable(false);
							xulik_Switch_btn.setChecked(false);
							break;
						case 8:// 漏水；
							xulik_state_txt.setText("漏水");
							break;
						}
						break;
					case 2:
						Water_control_tool.returndata(id_devid, acmdL, cmdH, myBinder);
						byte TDSa = buf.get();
						byte TDSb = buf.get();
						int TDS1 = Water_control_tool.getTransform(TDSb, TDSa);// /// 原水值；
						byte TDSd = buf.get();
						byte TDSc = buf.get();
						int TDS2 = Water_control_tool.getTransform(TDSc, TDSd);// 纯水值；
						Byte waterq = buf.get();// 水的质量；
						//						MyLog.i(TAG + "接收数据", " 指" + " 令字" + cmd + "原水值；=" + TDS1 + " 纯水值 = TDS2=" + TDS2
						//								+ " 水的质量；=waterq=" + waterq);
						float pent=((float)TDS1-(float)TDS2)/(float)TDS1;
						sinking_desalt.setPercent(pent);
						xulik_tds_j_txt.setText(""+TDS1);
						xulik_tds_c_txt.setText(""+TDS2);
						pent=0;
						TDS1=0;
						TDS2=0;
						break;
					case 3:
						if (datalen < 18)
							break;
						int count = (datalen - 18) / 4;// 求出有多少组滤芯；总长度-命令字-设备码-制水累计量-剩余可制水量/4；
						// 生成数据源
						for (int i = 1; i <= count; i++) {
							byte SetVa = buf.get();
							byte SetVb = buf.get();
							SetV = Water_control_tool.getTransform(SetVb, SetVa); // 滤芯设定值；
							Map<String, Integer> map = new HashMap<String, Integer>();
							map.put("SetV",  SetV);
							Setvlist.add(map);
						}
						for (int i = 1; i <= count; i++) {
							byte CntVa = buf.get();
							byte CntVb = buf.get();
							CntV = Water_control_tool.getTransform(CntVb, CntVa);// 剩余值；
							Map<String, Integer> map = new HashMap<String, Integer>();
							map.put("CntV",  CntV);
							Cntvlist.add(map);
						}
						float pen=(float)Cntvlist.get(0).get("CntV")/(float)Setvlist.get(0).get("SetV");
						//						MyLog.i(TAG, "百分比"+pen);
						sinking_Doulton_1.setPercent(pen);
						sinking_Doulton_2.setPercent((float)Cntvlist.get(1).get("CntV")/(float)Setvlist.get(1).get("SetV"));
						sinking_Doulton_3.setPercent((float)Cntvlist.get(2).get("CntV")/(float)Setvlist.get(2).get("SetV"));
						sinking_Doulton_4.setPercent((float)Cntvlist.get(3).get("CntV")/(float)Setvlist.get(3).get("SetV"));
						sinking_Doulton_5.setPercent((float)Cntvlist.get(4).get("CntV")/(float)Setvlist.get(4).get("SetV"));
						int WaterCntb = buf.getInt();// 制水累计量
						int Paper = buf.getInt();// 剩余可制水量
						Water_control_tool.returndata(id_devid, acmdL, cmdH, myBinder);
						break;
					case 4:
						byte retcontrolinstruction = buf.get();
						ByteBuffer retbuf = ByteBuffer.allocate(256);
						retbuf.put((byte) 0x11); // 消息长度 -6
						retbuf.put((byte) 0x003); // 指令
						retbuf.putInt((int) 0x01); // 发送者
						retbuf.putInt((int) id_devid);// 接收者
						retbuf.putShort((short) 0x00);// 返回状态嘛；
						retbuf.putShort((short) 0x0300);/// 长度；
						retbuf.putShort((short) ((short) (acmdL << 8) | cmdH));// 指令字；
						retbuf.put((byte) retcontrolinstruction); // 指令
						myBinder.send(retbuf);
						break;
					}
				}
			} else if (action.equals(COM_IMAGINE_INFENONE_UI)) {
				Toast.makeText(getApplicationContext(), "获取状态中...", 2).show();
				new sendshare().execute(3,3); 
				MyLog.i("", "此时主动查询了数据");
			}
		}
	}
	//控制；
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.xulik_wash_btn:// 冲洗；
			//为了防止用户或者测试MM疯狂的点击某个button，
			if (Utils.isFastClick()) {
				shake();
				MyLog.i(TAG, "id_devid="+id_devid);
				Water_control_tool.opendevwash((byte) 2, xulik_wash_btn, id_devid, myBinder);
				//				Water_control_tool.opendevwash(23,(byte) 2, xulik_wash_btn, id_devid, myBinder);//测试代码；
				xulik_content1.startRippleAnimation();
				if (xulik_wash_btn.isChecked()==false) {
					xulik_wash_btn.setChecked(true);
				}else{
					xulik_wash_btn.setChecked(false);
				}
				myApp.setControl_id(2);
				myApp.setBtn_state(xulik_wash_btn.isChecked());
			}
			break;
		case R.id.sinking_Doulton_1_btn:// 
			if (Utils.isFastClick()) {
				if ( !Setvlist.isEmpty()) {
					filter("第一级滤芯", Cntvlist.get(0).get("CntV"), Setvlist.get(0).get("SetV"), 1);
				}else{
					Toast.makeText(getApplicationContext(), "稍等，设备还未返回滤芯值,", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case R.id.sinking_Doulton_2_btn://
			if ( !Setvlist.isEmpty()) {
				filter("第二级滤芯",Cntvlist.get(1).get("CntV"), Setvlist.get(1).get("SetV"), 2);
			}else{
				Toast.makeText(getApplicationContext(), "稍等，设备还未返回滤芯值", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.sinking_Doulton_3_btn:// 
			if ( !Setvlist.isEmpty()) {
				filter("第三级滤芯",Cntvlist.get(2).get("CntV"), Setvlist.get(2).get("SetV"), 3);
			}
			break;
		case R.id.sinking_Doulton_4_btn:// 
			if (! Setvlist.isEmpty()) {
				filter("第四级滤芯",Cntvlist.get(3).get("CntV"), Setvlist.get(3).get("SetV"), 4);
			}else{
				Toast.makeText(getApplicationContext(), "稍等，设备还未返回滤芯值", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.sinking_Doulton_5_btn://
			if ( !Setvlist.isEmpty()) {
				filter("第五级滤芯",Cntvlist.get(4).get("CntV"), Setvlist.get(4).get("SetV"), 5);
			}else{
				Toast.makeText(getApplicationContext(), "稍等，设备还未返回滤芯值", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.xulik_Switch_btn:// 开关；
			if (Utils.isFastClick()) {
				MyLog.i(TAG, "id_devid="+id_devid);
				shake();
				Water_control_tool.opendevwash((byte) 1, xulik_Switch_btn, id_devid, myBinder);
				//				Water_control_tool.opendevwash(23,(byte) 1, xulik_Switch_btn, id_devid, myBinder);//测试代码；
				xulik_content2.startRippleAnimation();
				if (xulik_Switch_btn.isChecked()==false) {
					xulik_wash_btn.setChecked(false);
					xulik_wash_btn.setClickable(false);
					xulik_Switch_btn.setChecked(true);
				}else{
					xulik_Switch_btn.setChecked(false);
					xulik_wash_btn.setClickable(true);
					new sendshare().execute(3,2);
				}
				myApp.setControl_id(1);
				myApp.setBtn_state(xulik_Switch_btn.isChecked());
			}
			break;
		case R.id.xulik_share_result:// 返回；
			finish();
			break;
		case R.id.xulik_share_imbtn_dev:// 分享设备；
			Intent xulik_share_imbtn_devintent = new Intent(this, ShareDevActivity.class);
			xulik_share_imbtn_devintent.putExtra("devkey",devkey_id);
			xulik_share_imbtn_devintent.putExtra("devid", id_devid);
			startActivity(xulik_share_imbtn_devintent);
			break;
		case R.id.xulik_seting_imgbtn:// 设备设置；
			Intent upgrade=new Intent(getApplicationContext(), Water_setingActivity.class);
			startActivity(upgrade);
			break;
		}
	}

	class sendshare extends AsyncTask<Integer, Void, Void>{

		@Override
		protected Void doInBackground(Integer... arg0) {
			if (arg0[0]==3) {
				try {
					if (arg0[1]==3) {
						Water_control_tool.sendstatus((short) 1, id_devid, myBinder);
						Thread.sleep(150);
					}
					Water_control_tool.sendstatus((short) 2, id_devid, myBinder);
					Thread.sleep(150);
					Water_control_tool.sendstatus((short) 3, id_devid, myBinder);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}
	private void filter(String type,int CntVs,int SetVs,int indexs){
		Intent RO=new Intent(getApplicationContext(), Filter_details_Activity_devid.class);
		RO.putExtra("filter_type", type);
		RO.putExtra("filter_from", CntVs);
		RO.putExtra("filter_too", SetVs);
		RO.putExtra("index", indexs);
		RO.putExtra("id_devid",id_devid);
		RO.putExtra("filter_clientID", clientID);
		startActivity(RO);
	}
	private void init() {
		xulik_tds_j_txt.setText(""+0);
		xulik_tds_c_txt.setText(""+0);
		xulik_state_txt.setText("状态");
		sinking_desalt.setPercent(0.00f);
		sinking_Doulton_1.setPercent(0.00f);
		sinking_Doulton_2.setPercent(0.00f);
		sinking_Doulton_3.setPercent(0.00f);
		sinking_Doulton_4.setPercent(0.00f);
		sinking_Doulton_5.setPercent(0.00f);
	}
	public void shake(){
		/* 
		 * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到 
		 * */  
		Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);  
		long [] pattern = {100,100};   // 停止 开启 停止 开启   
		vibrator.vibrate(pattern,-1);           //重复两次上面的pattern 如果只想震动一次，index设为-1   
	}
}
