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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.air.doopen.config.RadioName;
import cn.air.doopen.hy.R;
import cn.air.doopen.socket.TcpCommSerivce;
import cn.air.doopen.utli.ActivityManager;
import cn.air.doopen.utli.MyLog;
/**洒水器控制界面；使用连接号控制*/
public class PurlingActivity extends Activity implements OnClickListener,RadioName {

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
	private TextView tiem_last;
	private ProgressBar time_progresBar;
	private addupdatePb add;
	private jianAsyncTask cut;
	private	int i = 0;
	private int t=0;
	@Override
	protected void onResume() {
		super.onResume();
		// 注册广播接收器,接收来自TcpCom的广播
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(CN_XIEWEIMING_APP_TCPCOMM);
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
		setContentView(R.layout.activity_purling);
		// 设置显示返回按钮
		// ab = getActionBar();
		// ab.setDisplayHomeAsUpEnabled(true);
		purling_add = (Button) findViewById(R.id.purling_add);
		purling_cut = (Button) findViewById(R.id.purling_cut);
		tiem_last = (TextView) findViewById(R.id.tiem_last);
		time_progresBar = (ProgressBar) findViewById(R.id.time_progresBar);
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
			Intent intent1 = PurlingActivity.this.getIntent();
			if (intent1.getIntExtra("clientID", 0) != bundle.getInt("from")){
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
			add=new addupdatePb();
			add.execute(1300);
			if (cut!=null) {
				cut.cancel(true);  
			}

			break;
		case R.id.purling_cut:
			Short LED_M1 = 0;
			LED_M1 = (short) 2;
			upDateValue(LED_M1);
			cut=new jianAsyncTask();
			cut.execute(1300);
			if (add!=null) {
				add.cancel(true);  
			}
			break;
		}
	}



	private void upDateValue(Short LED_M  ){
		ByteBuffer buf = ByteBuffer.allocate(256);

		buf.put((byte) 0x12);   // 消息长度
		buf.put((byte) 0x03);   // 指令
		buf.putInt((int) 0x01); // 发送者
		Intent intent = getIntent();
		buf.putInt((int) intent.getIntExtra("clientID", 0));// 接收者
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


	class addupdatePb extends AsyncTask<Integer,Integer,String>{
		//后面尖括号内分别是参数（例子里是线程休息时间），进度(publishProgress用到)，返回值 类型
		protected String doInBackground(Integer... params) {
			while(true){
				i+=1300;
				t+=1;
				time_progresBar.setProgress(i);
				publishProgress(t);
				if(isCancelled()) return null;// Task被取消了，马上退出循环
				try {
					Thread.sleep(params[0]);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (i>=130000) {
					return null;
				}
				if (t<=0) {
					return null;
				}
				if (t>=100) {
					return null;
				}
			}
		}
		protected void onProgressUpdate(Integer... progress) {
			tiem_last.setText(String.valueOf(progress[0]+"%"));
			//这个函数在doInBackground调用publishProgress时触发，虽然调用时只有一个参数
			//但是这里取到的是一个数组,所以要用progesss[0]来取值
			//第n个参数就用progress[n]来取值
		}
		protected void onPostExecute(String result) {
			setTitle(result);
			//doInBackground返回时触发，换句话说，就是doInBackground执行完后触发
			//这里的result就是上面doInBackground执行后的返回值，所以这里是"执行完毕"
		}
	}
	class jianAsyncTask extends AsyncTask<Integer,Integer,String>{
		//后面尖括号内分别是参数（例子里是线程休息时间），进度(publishProgress用到)，返回值 类型
		protected String doInBackground(Integer... params) {
			while(true){
				i-=1300;
				t-=1;
				time_progresBar.setProgress(i);
				publishProgress(t);
				if(isCancelled()) return null;// Task被取消了，马上退出循环
				try {
					Thread.sleep(params[0]);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (i>=130000) {
					return null;
				}
				if (t<=0) {
					return null;
				}
				if (t>=99) {
					return null;
				}
			}
		}
		protected void onProgressUpdate(Integer... progress) {
			tiem_last.setText(String.valueOf(progress[0]+"%"));
			//这个函数在doInBackground调用publishProgress时触发，虽然调用时只有一个参数
			//但是这里取到的是一个数组,所以要用progesss[0]来取值
			//第n个参数就用progress[n]来取值
		}
		protected void onPostExecute(String result) {
			setTitle(result);
			//doInBackground返回时触发，换句话说，就是doInBackground执行完后触发
			//这里的result就是上面doInBackground执行后的返回值，所以这里是"执行完毕"
		}

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
