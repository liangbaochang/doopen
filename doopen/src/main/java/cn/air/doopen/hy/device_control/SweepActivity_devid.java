package cn.air.doopen.hy.device_control;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.air.doopen.hy.R;
import cn.air.doopen.socket.TcpCommSerivce;
import cn.air.doopen.utli.ActivityManager;
import cn.air.doopen.utli.MyLog;
/**扫地机控制界面；*/
public class SweepActivity_devid extends Activity implements OnClickListener {

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
		}
	};
	private Button purling_add;
	private Button purling_cut;
	@Override
	protected void onResume() {
		super.onResume();
		// 注册广播接收器,接收来自TcpCom的广播
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("cn.xieweiming.app.TcpComm");
		registerReceiver(receiver, filter);
		// 绑定服务
		Intent intent = new Intent(this, TcpCommSerivce.class);
		bindService(intent, connection, 0);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 注销监听器
		unregisterReceiver(receiver);
		// 解绑服务
		unbindService(connection);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.activity_sweep);
		// 设置显示返回按钮
		// ab = getActionBar();
		// ab.setDisplayHomeAsUpEnabled(true);
		purling_add = (Button) findViewById(R.id.purling_add);
		purling_cut = (Button) findViewById(R.id.purling_cut);
		purling_add.setOnClickListener(this);
		purling_cut.setOnClickListener(this);
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
			Bundle bundle = intent.getExtras();
			// 只处理发给我的消息
			Intent intent1 = SweepActivity_devid.this.getIntent();
			if (intent1.getIntExtra("id_devid", 0) != bundle.getInt("from")){
				MyLog.d(TAG,"收到不是给我的消息");
				return;
			}else{
				MyLog.d(TAG,"收到给我的消息");
				ByteBuffer buf = ByteBuffer.allocate(256);
				buf.clear();
				byte[] data = bundle.getByteArray("data");
				buf.put(data);
				buf.flip();

				Byte LED_R = buf.get();
				Byte LED_G = buf.get();
				Byte LED_B = buf.get();
				Short LED_M = buf.getShort();
				Byte NTC = buf.get();
				Byte CDS = buf.get();
				Byte seekBar_val = buf.get();
				MyLog.d("LED_ACTIVITY","LED_M = "+ LED_M+",SB="+seekBar_val);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.purling_add:
			Short LED_M = 0;
			LED_M=1;
			upDateValue(LED_M);
			break;
		case R.id.purling_cut:
			Short LED_M1 = 0;
			LED_M1 = (short) 2;
			upDateValue(LED_M1);
			break;
		}
	}



	private void upDateValue(Short LED_M  ){
		ByteBuffer buf = ByteBuffer.allocate(256);
		buf.put((byte) 0x12);   // 消息长度
		buf.put((byte) 0x03);   // 指令
		buf.putInt((int) 0x01); // 发送者
		Intent intent = getIntent();
		buf.putInt((int) intent.getIntExtra("id_devid", 0));// 接收者

		buf.put((byte)0);
		buf.put((byte)0);
		buf.put((byte)0);
		buf.putShort(LED_M);
		buf.put((byte)0);
		buf.put((byte)0);
		buf.put((byte)0);
		//		String str=byteBufferToString(buf);
		//		MyLog.i("发送的数据包 toString", str);
		myBinder.send(buf);
		LED_M=0;
	}


	//
	public static String byteBufferToString(ByteBuffer buffer) {
		CharBuffer charBuffer = null;
		try {
			Charset charset = Charset.forName("UTF-8");
			CharsetDecoder decoder = charset.newDecoder();
			charBuffer = decoder.decode(buffer);
			buffer.flip();
			return charBuffer.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
