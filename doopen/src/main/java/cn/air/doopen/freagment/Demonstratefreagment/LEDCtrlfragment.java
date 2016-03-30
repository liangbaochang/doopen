package cn.air.doopen.freagment.Demonstratefreagment;

import java.nio.ByteBuffer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import cn.air.doopen.config.RadioName;
import cn.air.doopen.hy.R;
import cn.air.doopen.hy.device_control.water_control_tool.Water_control_tool;
import cn.air.doopen.socket.TcpCommSerivce;
import cn.air.doopen.utli.MyLog;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
/**演示板的控制界面，连接号控制*/
public class LEDCtrlfragment extends Fragment implements OnClickListener,RadioName{

	private static final String TAG = "Light";
	private MyReceiver receiver = null;

	private TcpCommSerivce.MyBinder myBinder;

	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			myBinder = (TcpCommSerivce.MyBinder) service;
			Intent intent=new Intent(COM_IMAGINE_INFENONE_SENDQUERY);
			getActivity().sendBroadcast(intent);
		}
	};
	private ToggleButton outlet_togg;private VerticalSeekBar Temp_vert;private ToggleButton keypad_togg;private SeekBar lampbrightness_seekbor;	private VerticalSeekBar TDS_vert;
	private SeekBar motor_seekbor;private SeekBar size_seekbor;private TextView lampbrightness_seekbor_text;private TextView size_seekbor_text;private TextView motor_seekbor_text;
	private TextView Temp_vert_text;private TextView TDS_vert_text;private TextView luminance_vert_text;private ToggleButton led14_togg;
	private ToggleButton led15_togg;private VerticalSeekBar luminance_vert;
	private int clientID;
	private int id_devid;
	private String devkey_id;
	public static  int  lampbrig;
	public static  int  motor;
	public static  int  size;
	public static byte outlet=-1;
	public static byte keypad=-1;
	public static short LED_Ma = 0;
	@Override
	public void onResume() {
		super.onResume();
		// 注册广播接收器,接收来自TcpCom的广播
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(COM_IMAGINE_INFENONE_SOCKET_TCPCOMM_ONLINE);
		filter.addAction(COM_IMAGINE_INFENONE_SOEKET_TCPCOMM_SOCKET_MSG);
		filter.addAction(COM_IMAGINE_INFENONE_SOCKET_TCPCOMM_OFFINE);
		filter.addAction(CN_XIEWEIMING_APP_TCPCOMM);
		filter.addAction(CN_XIEWEIMING_APP_TCPCOMM_ONLINE);
		filter.addAction(COM_IMAGINE_INFENONE_SENDQUERY);
		getActivity().registerReceiver(receiver, filter);
		// 绑定服务
		Intent intent = new Intent(getActivity(), TcpCommSerivce.class);
		getActivity().bindService(intent, connection, 0);
		MyLog.i("onResume", "onResume执行了");
	}

	@Override
	public void onPause() {
		super.onPause();
		// 注销监听器
		getActivity().unregisterReceiver(receiver);
		// 解绑服务
		getActivity().unbindService(connection);
		MyLog.i("onPause", "onPause执行了");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.led_order_layout, null);
		setview(view);
		setlistener();
		MyLog.i("onCreateView","执行了");
		return view;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent1 = getActivity().getIntent();
		clientID = intent1.getIntExtra("clientID", -1);
		id_devid = intent1.getIntExtra("id_devid", -1);
		devkey_id = intent1.getStringExtra("devkey_id");
		MyLog.i(TAG, "clientID=" + clientID);
	}

	/**
	 * 获取广播数据
	 * 
	 * @author xwm
	 *
	 */
	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String actions=intent.getAction();
			if (actions.equals(COM_IMAGINE_INFENONE_SOEKET_TCPCOMM_SOCKET_MSG)) {
				Toast.makeText(getActivity(), "APP和服务器断开了连接，请检查网络设置", Toast.LENGTH_LONG).show();
			}
			if (actions.equals(COM_IMAGINE_INFENONE_SOCKET_TCPCOMM_OFFINE)) {
				Toast.makeText(getActivity(), "设备已离线，暂时无法控制操作！等待设备上线后才可以控制", Toast.LENGTH_LONG).show();
			}
			if (actions.equals(CN_XIEWEIMING_APP_TCPCOMM_ONLINE)) {
				//同样，在读取SharedPreferences数据前要实例化出一个SharedPreferences对象
				SharedPreferences a=getActivity(). getSharedPreferences("dev_msg",Activity.MODE_PRIVATE);
				// 使用getString方法获得value，注意第2个参数是value的默认值
//				clientID =a.getInt("on_clientID_sh", -1);
				Bundle bundle = intent.getExtras();
				clientID=bundle.getInt("on_clientID");
				MyLog.i("", "更新连接号需要此时 =="+clientID);
				sendQuery();
			}
			if (actions.equals(CN_XIEWEIMING_APP_TCPCOMM)) {
				Bundle bundle = intent.getExtras();
				// 只处理发给我的消息
				if (clientID!= bundle.getInt("from")){
					MyLog.d(TAG,"收到不是给我的消息");
					return;
				}else{
					MyLog.d(TAG,"收到给我的消息");
					ByteBuffer buf = ByteBuffer.allocate(256);
					buf.clear();
					byte[] data = bundle.getByteArray("data");
					buf.put(data);
					buf.flip();
					byte zt=buf.get();//帧头
					MyLog.i(TAG, "帧头=="+zt);
					if (zt==104) {
						int a=buf.getInt();//设备编号
						MyLog.i(TAG, "设备编号=="+a);
						short b=buf.getShort();//设备编号
						MyLog.i(TAG, "设备编号=="+b);
						byte cmd=buf.get();//命令字；
						MyLog.i(TAG, "命令字=="+cmd);
						short len=buf.getShort();//长度
						MyLog.i(TAG, "长度=="+len);
						if (cmd==-127||cmd==-125) {
							short LED_R = buf.getShort();//三色灯1
							MyLog.i(TAG, "三色灯a=="+LED_R);
							short LED_G = buf.getShort();//三色灯2
							MyLog.i(TAG, "三色灯b=="+LED_G);
							short LED_B = buf.getShort();//三色灯3
							MyLog.i(TAG, "三色灯c=="+LED_B);
							short LED_M = buf.getShort();//bit:0—灭， 1---亮，高位在前
							MyLog.i(TAG, "LED灯=="+LED_M);
							byte NTC = buf.get();        //温度；
							MyLog.i(TAG, "温度=="+NTC);
							byte CDSa = buf.get();      //环境亮度；
							MyLog.i(TAG, "环境亮度a；=="+CDSa);
							byte CDSb = buf.get();      //环境亮度；
							MyLog.i(TAG, "环境亮度b；=="+CDSb);
							int CDS=Water_control_tool.getTransform(CDSa, CDSb);
							MyLog.i(TAG, "环境亮度CDS；=="+CDS);
							byte seekBar_val = buf.get(); //滑块控制灯；
							MyLog.i(TAG, "滑块控制灯；=="+seekBar_val);
							byte sockets = buf.get();      //插座； -----------》0x00—表示打开，0x01----表示闭合\
							MyLog.i(TAG, "插座=="+sockets);
							byte bulb = buf.get();      //灯泡 -----》0~255表示亮度，0表示断开
							MyLog.i(TAG, "灯泡=="+bulb);
							byte tdsa = buf.get();      //TDS
							MyLog.i(TAG, "TDSa=="+tdsa);
							byte tdsb= buf.get();      //TDS
							MyLog.i(TAG, "tdsb=="+tdsb);
							int TDS=Water_control_tool.getTransform(tdsa, tdsb);
							MyLog.i(TAG, "TDS=="+TDS);
							byte keysa = buf.get();      //按键
							MyLog.i(TAG, "按键=="+keysa);
							byte Motors = buf.get();      //马达转速；
							MyLog.i(TAG, "马达转速；=="+Motors);
							byte buzzer = buf.get();      //蜂鸣器
							MyLog.i(TAG, "蜂鸣器=="+buzzer);
							byte verifys = buf.get();      //校验
							MyLog.i(TAG, "校验=="+verifys);
							byte footers = buf.get();      //帧尾
							MyLog.i(TAG, "帧尾=="+footers);
							if (cmd==-125) {
								lampbrightness_seekbor.setProgress(bulb);
								size_seekbor.setProgress(seekBar_val);
								motor_seekbor.setProgress(Motors);
								lampbrightness_seekbor_text.setText(""+bulb);
								size_seekbor_text.setText(""+seekBar_val);
								motor_seekbor_text.setText(""+Motors);
							}
							Temp_vert.setProgress(NTC);
							Temp_vert_text.setText(""+NTC+" °C");
							luminance_vert.setProgress(CDS);
							luminance_vert_text.setText(""+CDS);
							TDS_vert.setProgress(TDS);
							TDS_vert_text.setText(""+TDS);

							if(sockets==1){
								outlet_togg.setChecked(true);
							}else{
								outlet_togg.setChecked(false);
							}
							if(buzzer==1){
								keypad_togg.setChecked(true);
							}else{
								keypad_togg.setChecked(false);
							}
							if((LED_M & 0x20) > 0){
								led14_togg.setChecked(true);
							}else{
								led14_togg.setChecked(false);
							}
							if((LED_M & 0x40) > 0){
								led15_togg.setChecked(true);
							}else{
								led15_togg.setChecked(false);
							}
						}
					}else{
						MyLog.i(TAG, "帧头错误");
					}
				}
			}
			if (actions.equals(COM_IMAGINE_INFENONE_SENDQUERY)) {
				sendQuery();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		MyLog.i("onDestroy", "onDestroy执行了");
	}
	@Override
	public void onStop() {
		super.onStop();
		MyLog.i("onStop", "onStop执行了");
	}
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.outlet_togg://new byte[]{0x0c, 0x03, 0x00, 0x00, 0x00, 0x0E, 0x00, 0x00, 0x00, 0x0D, 0x01, 0x01}
			upDateValue();
			break;
		case R.id.keypad_togg:
			upDateValue();
			break;
		case R.id.led14_togg:
			upDateValue();
			break;
		case R.id.led15_togg:
			upDateValue();
			break;
		}
	}
	//LED灯控制；
	private void upDateValue(){

		if(outlet_togg.isChecked()==true){
			outlet=0x01;
		}else{
			outlet=0x00;
		}
		if(keypad_togg.isChecked()==true){
			keypad=0x01;
		}else{
			keypad=0x00;
		}
		if(led14_togg.isChecked()){
			LED_Ma= (short) (LED_Ma + 0x20);
		}
		if(led15_togg.isChecked()){
			LED_Ma= (short) (LED_Ma + 0x40);
		}
		ByteBuffer buf = ByteBuffer.allocate(256);
		buf.put((byte) 0x29);   // 消息长度
		buf.put((byte) 0x03);   // 指令
		buf.putInt((int) 0x01); // 发送者
		buf.putInt((int) clientID);// 接收者
		MyLog.i("", "连接号=="+clientID);
		buf.put((byte)0x68);   //帧头
		buf.putInt((int)0x00);//设备编号
		buf.putShort((short)0x00);//设备编号
		buf.put((byte)0x80);      //命令字             80
		buf.putShort((short)19);      //数据长度                  14
		buf.putShort((short)TricolourFragment.redc);//三色灯
		buf.putShort((short)TricolourFragment.yello);//三色灯
		buf.putShort((short)TricolourFragment.Bluec);;//三色灯
		buf.putShort((short)LED_Ma);//LED灯         
		buf.put((byte)0x00);//热敏电阻（NTC）
		buf.putShort((short)0x00);//光敏电阻（CDS）
		buf.put((byte)size);//滑条-----灯显示的个数；
		buf.put((byte)outlet);//插座
		buf.put((byte)lampbrig);//灯泡亮度；
		buf.putShort((short)0x00);//TDS值；
		buf.put((byte)0x00);//按键
		buf.put((byte)motor);//马达转速；
		buf.put((byte)keypad);//蜂鸣器
		byte jy=(byte) (0x80+19+LED_Ma+size+outlet+lampbrig+motor+keypad+TricolourFragment.redc+TricolourFragment.yello+TricolourFragment.Bluec);
		byte jyqf=(byte) ~jy;
		buf.put((byte)jyqf);//校验
		buf.put((byte)0x16);//帧尾
		myBinder.send(buf);
		LED_Ma=0;
	}
	//主动查询；
	public void sendQuery(){
		ByteBuffer buf = ByteBuffer.allocate(256);
		buf.put((byte) 22);   // 消息长度
		buf.put((byte) 0x03);   // 指令
		buf.putInt((int) 0x01); // 发送者
		buf.putInt((int)clientID );// 接收者
		buf.put((byte)0x68);   //帧头
		buf.putInt((int)0x00);//设备编号
		buf.putShort((short)0x00);//设备编号
		buf.put((byte)0x82);      //命令字             80
		buf.putShort((short)0);      //数据长度                  14
		buf.put((byte)~0x82);//校验
		buf.put((byte)0x16);//帧尾
		myBinder.send(buf);
	}
	private void setlistener() {
		lampbrightness_seekbor.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				MyLog.i("onProgressChanged", "A===="+progress);
				lampbrig=progress;
				upDateValue();
				lampbrightness_seekbor_text.setText(""+lampbrig);
			}
		});

		size_seekbor.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				MyLog.i("onProgressChanged", "B===="+progress);
				size=progress;
				upDateValue();
				size_seekbor_text.setText(""+size);
			}
		});

		motor_seekbor.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				MyLog.i("onProgressChanged", "C===="+progress);
				motor=progress;
				upDateValue();
				motor_seekbor_text.setText(""+motor);
			}
		});
	}

	public void setview (View view){
		lampbrightness_seekbor_text = (TextView) view.findViewById(R.id.lampbrightness_seekbor_text);
		size_seekbor_text = (TextView) view.findViewById(R.id.size_seekbor_text);
		motor_seekbor_text = (TextView) view.findViewById(R.id.motor_seekbor_text);
		lampbrightness_seekbor = (SeekBar) view.findViewById(R.id.lampbrightness_seekbor);
		size_seekbor = (SeekBar) view.findViewById(R.id.size_seekbor);
		motor_seekbor = (SeekBar) view.findViewById(R.id.motor_seekbor);
		Temp_vert_text = (TextView) view.findViewById(R.id.Temp_vert_text);
		luminance_vert_text = (TextView) view.findViewById(R.id.luminance_vert_text);
		TDS_vert_text = (TextView) view.findViewById(R.id.TDS_vert_text);
		Temp_vert = (VerticalSeekBar) view.findViewById(R.id.Temp_vert);
		luminance_vert = (VerticalSeekBar) view.findViewById(R.id.luminance_vert);
		TDS_vert = (VerticalSeekBar) view.findViewById(R.id.TDS_vert);
		outlet_togg = (ToggleButton) view.findViewById(R.id.outlet_togg);
		keypad_togg = (ToggleButton) view.findViewById(R.id.keypad_togg);
		led14_togg = (ToggleButton)view. findViewById(R.id.led14_togg);
		led15_togg = (ToggleButton) view.findViewById(R.id.led15_togg);

		outlet_togg.setOnClickListener(this);
		keypad_togg.setOnClickListener(this);
		led14_togg.setOnClickListener(this);
		led15_togg.setOnClickListener(this);

		
	}
}
