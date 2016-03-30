package cn.air.doopen.freagment.airA;
import java.nio.ByteBuffer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import cn.air.doopen.config.RadioName;
import cn.air.doopen.freagment.airA.CustomClipprogress.OnVolumeChangedListener;
import cn.air.doopen.hy.R;
import cn.air.doopen.hy.device_control.water_control_tool.Water_setingActivity;
import cn.air.doopen.hy.share.Social_share;
import cn.air.doopen.socket.TcpCommSerivce;
import cn.air.doopen.utli.MyLog;

/**海一的空气净化器的控制界面；使用设备id控制；*/
public class ContorlAir_devid extends Fragment implements RadioName,OnClickListener{

	private static final String TAG = "ContorlAir";
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

	private RadioGroup air_radiogroup;
	private RadioButton air_self_radio;
	private RadioButton air_hand;
	private RadioButton air_radio0;
	private RadioButton air_radio2;
	private RadioButton air_radio4;
	private RadioButton air_radio3;
	private LinearLayout air_linear;
	private ImageView air_pm_img;
	private ImageView air_voc_img;
	private ToggleButton air_dupopen_onof;
	private TextView air_pm_txt;
	private TextView air_voc_txt;
	private int clientID;
	private int id_devid=-1;
	private Button air_share_imbtn_dev;
	private Button air_share_imbtn_seting;
	private CustomClipprogress Reduced_regulation;

	@Override
	public void onResume() {
		super.onResume();
		// 绑定服务
		Intent intent = new Intent(getActivity(), TcpCommSerivce.class);
		getActivity().bindService(intent, connection, 0);
		MyLog.i("onResume", "onResume执行了");
		// 注册广播接收器,接收来自TcpCom的广播
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(CN_XIEWEIMING_APP_TCPCOMM_ONLINE);
		filter.addAction(COM_IMAGINE_INFENONE_SOCKET_TCPCOMM_OFFINE);
		filter.addAction(COM_IMAGINE_INFENONE_SOEKET_TCPCOMM_SOCKET_MSG);
		filter.addAction(CN_XIEWEIMING_APP_TCPCOMM);
		filter.addAction(COM_IMAGINE_INFENONE_SENDQUERY);
		getActivity().registerReceiver(receiver, filter);
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
		View v = inflater.inflate(R.layout.air_control__layout, null);
		Intent clie =getActivity(). getIntent();
		clientID=clie.getIntExtra("clientID", 0);
		id_devid = clie.getIntExtra("id_devid", -1);
		setview(v);
		setlistener();
		return v;
	}


	private void setlistener() {
		Reduced_regulation.setProgress(5);
		Reduced_regulation.setOnVolumeChangeListener(new OnVolumeChangedListener() {

			@Override
			public void setYourVolume(int index) {
				MyLog.i("监听器里面的index="+index);	
			}
		});
		air_hand.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (air_hand.isClickable()==true) {
					air_linear.setVisibility(View.VISIBLE);
					air_radio3.setChecked(false);air_radio4.setChecked(false);air_radio2.setChecked(false);air_radio0.setChecked(true);
					MyLog.i(TAG, "消毒；");
					upDateValue(02);
				}
			}
		});
		air_share_imbtn_seting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent upgrade=new Intent(getActivity(), Water_setingActivity.class);
				startActivity(upgrade);
			}
		});
		air_self_radio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (air_self_radio.isClickable()==true) {
					air_linear.setVisibility(View.GONE);
					upDateValue(01);
					MyLog.i(TAG, "自动模式；");
				}
			}
		});

		air_dupopen_onof.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (air_dupopen_onof.isChecked()==true) {
					MyLog.i(TAG, "测试服务器的界面");
					MyLog.i(TAG, "isChecked====true");
					air_hand.setChecked(true);
					air_hand.setChecked(false);
					air_self_radio.setChecked(true);
					air_hand.setClickable(true);
					air_self_radio.setClickable(true);
					upDateValue(01);
					air_linear.setVisibility(View.GONE);
				}else{
					MyLog.i(TAG, "测试服务器的界面");
					air_self_radio.setChecked(false);
					air_self_radio.setClickable(false);
					air_hand.setChecked(false);
					air_hand.setClickable(false);
					MyLog.i(TAG, "isChecked====false");
					air_linear.setVisibility(View.GONE);
					upDateValue(00);
				}
			}
		});
		//		00—表示关机01—表示自动模式02—表示消毒模式   03—表示杀菌模式04—表示除尘模式05—表示消除静电
		air_radio4.setOnClickListener(this);
		air_radio3.setOnClickListener(this);
		air_radio2.setOnClickListener(this);
		air_radio0.setOnClickListener(this);
		air_share_imbtn_dev.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.air_radio4://杀菌
			air_radio3.setChecked(false);air_radio2.setChecked(false);air_radio0.setChecked(false);air_radio4.setChecked(true);
			MyLog.i(TAG, "/杀菌");
			upDateValue(03);
			break;
		case R.id.air_radio3://放尘
			air_radio2.setChecked(false);air_radio4.setChecked(false);air_radio0.setChecked(false);air_radio3.setChecked(true);
			MyLog.i(TAG, "放尘");
			upDateValue(04);
			break;
		case R.id.air_radio2://除静电
			air_radio3.setChecked(false);air_radio4.setChecked(false);air_radio0.setChecked(false);air_radio2.setChecked(true);
			MyLog.i(TAG, "除静电");
			upDateValue(05);
			break;
		case R.id.air_radio0://消毒
			air_radio3.setChecked(false);air_radio4.setChecked(false);air_radio2.setChecked(false);air_radio0.setChecked(true);
			MyLog.i(TAG, "消毒；");
			upDateValue(02);
			break;
		case R.id.air_share_imbtn_dev://分享
			Social_share.showShare();
			break;
		}

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
				Toast.makeText(getActivity(), "APP和服务器断开了连接，请检查网络设置", Toast.LENGTH_SHORT).show();
			}
			if (actions.equals(COM_IMAGINE_INFENONE_SOCKET_TCPCOMM_OFFINE)) {
				Toast.makeText(getActivity(), "设备已离线，暂时无法控制操作！等待设备上线后才可以控制", Toast.LENGTH_SHORT).show();
			}
			if (actions.equals(CN_XIEWEIMING_APP_TCPCOMM_ONLINE)) {
				sendQuery();
			}
			if (actions.equals(CN_XIEWEIMING_APP_TCPCOMM)) {
				Bundle bundle = intent.getExtras();
				// 只处理发给我的消息
				if (id_devid != bundle.getInt("from")){
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
					int a=buf.getInt();//设备编号
					MyLog.i(TAG, "设备编号=="+a);
					short b=buf.getShort();//设备编号
					MyLog.i(TAG, "设备编号=="+b);
					byte cmd=buf.get();//命令字；
					MyLog.i(TAG, "命令字=="+cmd);
					short len=buf.getShort();//长度
					MyLog.i(TAG, "长度=="+len);
					if (cmd==-127||cmd==-125) {
						/**模式选择	1	00—表示关机01—表示自动模式02—表示消毒模式03—表示杀菌模式04—表示除尘模式05—表示消除静电
						VOC值	1	环境有机物等级0-10
						PM2.5值	1	颗粒物等级0-10
						报警代码	1	E1:热敏电阻超温，E2：跌倒报警
						室温显示	1	-20~100
						工作温度	1	-20~100
						采样时间	1	0-60分钟*/
						byte selection = buf.get();        //模式选择
						switch (selection) {
						case 0:
							//更新按钮的状态；
							air_hand.setChecked(false);
							air_self_radio.setChecked(false);air_linear.setVisibility(View.GONE);
							air_hand.setClickable(false);
							air_dupopen_onof.setChecked(false);
							air_self_radio.setClickable(false);
							break;
						case 1:
							air_hand.setChecked(true);
							air_hand.setChecked(false);
							air_self_radio.setChecked(true);air_linear.setVisibility(View.GONE);
							air_dupopen_onof.setChecked(true);
							air_hand.setClickable(true);
							air_self_radio.setClickable(true);
							break;
						case 2:
							air_hand.setChecked(true);air_self_radio.setChecked(false);
							air_radio0.setChecked(true);air_radio2.setChecked(false);air_radio4.setChecked(false);air_radio3.setChecked(false);air_linear.setVisibility(View.VISIBLE);
							air_hand.setClickable(true);
							air_self_radio.setClickable(true);
							air_dupopen_onof.setChecked(true);
							break;
						case 3:
							air_hand.setChecked(true);air_self_radio.setChecked(false);

							air_radio2.setChecked(false);air_radio4.setChecked(true);air_radio0.setChecked(false);	air_radio3.setChecked(false);air_linear.setVisibility(View.VISIBLE);

							air_hand.setClickable(true);
							air_self_radio.setClickable(true);
							air_dupopen_onof.setChecked(true);
							break;
						case 4:
							air_hand.setChecked(true);air_self_radio.setChecked(false);
							air_radio2.setChecked(false);air_radio4.setChecked(false);air_radio0.setChecked(false);air_radio3.setChecked(true);air_linear.setVisibility(View.VISIBLE);
							air_hand.setClickable(true);
							air_self_radio.setClickable(true);
							air_dupopen_onof.setChecked(true);
							break;
						case 5:
							air_hand.setChecked(true);air_self_radio.setChecked(false);

							air_radio2.setChecked(true);air_radio4.setChecked(false);air_radio0.setChecked(false);air_radio3.setChecked(false);air_linear.setVisibility(View.VISIBLE);

							air_hand.setClickable(true);
							air_self_radio.setClickable(true);
							air_dupopen_onof.setChecked(true);
							break;
						}
						MyLog.i(TAG, "模式选择=="+selection);
						byte VOC = buf.get();        //VOC值
						MyLog.i(TAG, "VOC=="+VOC);
						air_voc_txt.setText(""+VOC);
						byte PM= buf.get();        //PM2.5
						air_pm_txt.setText(""+PM);
						MyLog.i(TAG, "PM2.5=="+PM);
						byte Danger = buf.get();        //报警代码
						MyLog.i(TAG, "报警代码=="+Danger);
						byte NTC = buf.get();        //室温显示
						MyLog.i(TAG, "室温显示=="+NTC);
						byte jobNTC = buf.get();        //工作温度
						MyLog.i(TAG, "工作温度=="+jobNTC);
						byte time = buf.get();        //采样时间
						MyLog.i(TAG, "采样时间=="+time);
						byte verifys = buf.get();      //校验
						MyLog.i(TAG, "校验=="+verifys);
						byte footers = buf.get();      //帧尾
						MyLog.i(TAG, "帧尾=="+footers);

					}
				}
			}
			if (actions.equals(COM_IMAGINE_INFENONE_SENDQUERY)) {
				sendQuery();
				MyLog.i(TAG, "sendQuery222");
			}
		}
	}
	//LED灯控制；
	private void upDateValue(int contents){
		ByteBuffer buf = ByteBuffer.allocate(256);
		buf.put((byte) 0x17);   // 消息长度
		buf.put((byte) 0x03);   // 指令
		buf.putInt((int) 0x01); // 发送者
		buf.putInt((int) id_devid);// 接收者
		buf.put((byte)0x68);   //帧头
		buf.putInt((int)0x00);//设备编号
		buf.putShort((short)0x00);//设备编号
		buf.put((byte)0x80);      //命令字             80
		buf.putShort((short)0x01);      //数据长度                  14
		buf.put((byte)contents);//数据内容；
		buf.put((byte)~(0x80+0x01+contents));//校验
		buf.put((byte)0x16);//帧尾
		myBinder.send(buf);
	}
	//主动查询；
	public void sendQuery(){
		ByteBuffer buf = ByteBuffer.allocate(256);
		buf.put((byte) 22);   // 消息长度
		buf.put((byte) 0x03);   // 指令
		buf.putInt((int) 0x01); // 发送者
		buf.putInt((int) id_devid);// 接收者
		buf.put((byte)0x68);   //帧头
		buf.putInt((int)0x00);//设备编号
		buf.putShort((short)0x00);//设备编号
		buf.put((byte)0x82);      //命令字             80
		buf.putShort((short)0);      //数据长度                  14
		buf.put((byte)~0x82);//校验
		buf.put((byte)0x16);//帧尾
		myBinder.send(buf);
	}
	public void setview(View v){
		Reduced_regulation=(CustomClipprogress)v.findViewById(R.id.Reduced_regulation); 
		air_share_imbtn_seting=(Button)v.findViewById(R.id.air_share_imbtn_seting); 
		air_share_imbtn_dev=(Button)v.findViewById(R.id.air_share_imbtn_dev);
		air_pm_img=(ImageView)v.findViewById(R.id.air_pm_img);
		air_voc_img=(ImageView)v.findViewById(R.id.air_voc_img);
		air_pm_txt=(TextView)v.findViewById(R.id.air_pm_txt);
		air_voc_txt=(TextView)v.findViewById(R.id.air_voc_txt);
		air_dupopen_onof=(ToggleButton)v.findViewById(R.id.air_dupopen_onof);
		air_radiogroup=(RadioGroup)v.findViewById(R.id.air_radiogroup);
		air_self_radio=(RadioButton)v.findViewById(R.id.air_self_radio);
		air_hand=(RadioButton)v.findViewById(R.id.air_hand);
		air_radio0=(RadioButton)v.findViewById(R.id.air_radio0);
		air_radio2=(RadioButton)v.findViewById(R.id.air_radio2);
		air_radio3=(RadioButton)v.findViewById(R.id.air_radio3);
		air_radio4=(RadioButton)v.findViewById(R.id.air_radio4);
		air_linear=(LinearLayout)v.findViewById(R.id.air_linear);
		air_linear.setVisibility(View.GONE);
	}
}
