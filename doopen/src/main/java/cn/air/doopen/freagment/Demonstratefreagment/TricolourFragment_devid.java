package cn.air.doopen.freagment.Demonstratefreagment;

import java.nio.ByteBuffer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.air.doopen.hy.R;
import cn.air.doopen.socket.TcpCommSerivce;
import cn.air.doopen.utli.MyLog;
import cn.air.doopen.view.Radial_Menu.WheelMenu;
/**控制灯的颜色；设备id控制*/
public class TricolourFragment_devid extends Fragment{
	public  static int redc=0;
	public  static int yello=0;
	public  static int Bluec=0;
	private TcpCommSerivce.MyBinder myBinder;
	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			myBinder = (TcpCommSerivce.MyBinder) service;
		}
	};
	private WheelMenu wheelMenu;

	@Override
	public void onResume() {
		super.onResume();
		// 绑定服务
		Intent intent = new Intent(getActivity(), TcpCommSerivce.class);
		getActivity().bindService(intent, connection, 0);
	}

	@Override
	public void onPause() {
		super.onPause();
		// 解绑服务
		getActivity().unbindService(connection);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.threecolour, null);
		wheelMenu = (WheelMenu)view.findViewById(R.id.wheelMenu);
		MyLog.i("onCreateView","执行了");
		wheelMenu.setDivCount(7);
		wheelMenu.setWheelImage(R.drawable.qisebj);
		//旋转菜单监听器；旋转到那个颜色就发送那个颜色的值。还可优化；
		wheelMenu.setWheelChangeListener(new WheelMenu.WheelChangeListener() {
			@Override
			public void onSelectionChange(int selectedPosition) {
				switch (selectedPosition+1) {
				case 1:
					redc=255;
					yello=0;
					Bluec=0;
					upDateValuese(redc,yello,Bluec);
					break;
				case 2:
					redc=255;
					yello=255;
					Bluec=0;
					upDateValuese(redc,yello,Bluec);
					break;
				case 3:
					redc=0;
					yello=0;
					Bluec=255;
					upDateValuese(redc,yello,Bluec);
					break;
				case 4:
					redc=153;
					yello=51;
					Bluec=250;
					upDateValuese(redc,yello,Bluec);
					break;
				case 5:
					redc=30;
					yello=44;
					Bluec=255;
					upDateValuese(redc,yello,Bluec);
					break;
				case 6:
					redc=160;
					yello=82;
					Bluec=45;
					upDateValuese(redc,yello,Bluec);
					break;
				case 7:
					redc=255;
					yello=255;
					Bluec=0;
					upDateValuese(redc,yello,Bluec);
					break;
				}
			}
		});
		return view;
	}
	//	LED灯控制；
	private void upDateValuese(int red,int yello,int Blue){
		ByteBuffer buf = ByteBuffer.allocate(256);
		buf.put((byte) 0x29);   // 消息长度
		buf.put((byte) 0x03);   // 指令
		buf.putInt((int) 0x01); // 发送者
		Intent intent =getActivity(). getIntent();
		MyLog.i("id_devid",""+  intent.getIntExtra("id_devid", 0));
		buf.putInt((int) intent.getIntExtra("id_devid", 0));// 接收者
		buf.put((byte)0x68);   //帧头
		buf.putInt((int)0x00);//设备编号
		buf.putShort((short)0x00);//设备编号
		buf.put((byte)0x80);      //命令字             80
		buf.putShort((short)19);      //数据长度                  14
		buf.putShort((short)red);//三色灯
		buf.putShort((short)yello);//三色灯
		buf.putShort((short)Blue);;//三色灯
		buf.putShort((short)LEDCtrlfragment.LED_Ma);//LED灯         
		buf.put((byte)0);//热敏电阻（NTC）
		buf.putShort((short)0x00);//光敏电阻（CDS）
		buf.put((byte)LEDCtrlfragment.size);//滑条-----灯显示的个数；
		buf.put((byte)LEDCtrlfragment.outlet);//插座
		buf.put((byte)LEDCtrlfragment.lampbrig);//灯泡亮度；
		buf.putShort((short)0x00);//TDS值；
		buf.put((byte)0x00);//按键
		buf.put((byte)LEDCtrlfragment.motor);//马达转速；
		buf.put((byte)LEDCtrlfragment.keypad);//蜂鸣器
		byte jy=(byte) (0x80+19+red+yello+Blue+LEDCtrlfragment.LED_Ma+LEDCtrlfragment.size+LEDCtrlfragment.outlet+LEDCtrlfragment.lampbrig+LEDCtrlfragment.motor+LEDCtrlfragment.keypad);
		byte jyqf=(byte) ~jy;
		buf.put((byte)jyqf);//校验
		buf.put((byte)0x16);//帧尾
		myBinder.send(buf);
	}
}
