package cn.air.doopen.hy.device_control.xulik;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.air.doopen.config.RadioName;
import cn.air.doopen.hy.R;
import cn.air.doopen.hy.device_control.water_control_tool.Water_control_tool;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.socket.TcpCommSerivce;
import cn.air.doopen.utli.MyLog;
/**净水器设备的滤芯值控制复位界面；点击复位之后更新复位值，测试服务器的界面；*/
@ContentView(value = R.layout.activity_filter_details)
public class Filter_details_Activity_devid extends Activity implements RadioName{
	@ViewInject(R.id.xulik_filter_share_result)   
	private Button xulik_filter_share_result;               
	@ViewInject(R.id.xulik_filter_submit_btn)   
	private Button xulik_filter_submit_btn;               
	@ViewInject(R.id.xulik_filter_selecot_btn)   
	private Button xulik_filter_selecot_btn;                 
	@ViewInject(R.id.xulik_shate_txt)     
	private TextView xulik_shate_txt;    
	@ViewInject(R.id.xulik_from_txt)   
	private TextView xulik_from_txt;      
	@ViewInject(R.id.xulik_too_filter)   
	private TextView xulik_too_filter;    
	private String TAG="Filter_details_Activity";
	private int clientID;
	private int filter_from;
	private int filter_too;
	private String devkey_id;
	private TcpCommSerivce.MyBinder myBinder;
	private MyReceiver receiver = null;
	public List<Map<String, Integer>> Setvlist = new ArrayList<Map<String, Integer>>();
	public List<Map<String, Integer>> Cntvlist = new ArrayList<Map<String, Integer>>();
	//调用service；
	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			myBinder = (TcpCommSerivce.MyBinder) service;
		}
	};
	private int index=-1;
	private int id_devid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		setdata();
	}
	@Override
	protected void onResume() {
		super.onResume();
		// 注册广播接收器,接收来自TcpCom的广播；
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(COM_IMAGINE_INFENONE_SOCKET_TCPCOMM_OFFINE);
		filter.addAction(COM_IMAGINE_INFENONE_SOEKET_TCPCOMM_SOCKET_MSG);//
		filter.addAction(CN_XIEWEIMING_APP_TCPCOMM);
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
	//添加监听事件；
	@OnClick({ R.id.xulik_filter_share_result, R.id.xulik_filter_submit_btn,R.id.xulik_filter_selecot_btn})
	public void onclick(View v){
		switch (v.getId()) {
		case R.id.xulik_filter_share_result:
			finish();
			break;
		case R.id.xulik_filter_submit_btn:
			if (filter_too!=-1) {
				//滤芯复位；
				AlertDialog.Builder builder = new AlertDialog.Builder(Filter_details_Activity_devid.this);
				builder.setTitle("确认复位滤芯吗？");
				builder.setNegativeButton("取消", null);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Water_control_tool.opendevwashfu((byte)index, id_devid,filter_from, myBinder);
						MyLog.i(TAG, "index="+index+"clientID"+index+"myBinder"+myBinder);
						new sendshare().execute();
						new FeedbackTask().execute();
					}
				});
				builder.show();
			}
			break;
		case R.id.xulik_filter_selecot_btn:
			Intent intent=new Intent(getApplicationContext(), Filter_history_Activity.class);
			intent.putExtra("devid",id_devid);
			startActivity(intent);
			break;
		}
	}


	/**
	 * 获取广播数据
	 */
	public class MyReceiver extends BroadcastReceiver {
		private short cmd;
		private byte cmdH;
		private byte acmdL;
		private int SetV;
		private int CntV;

		@Override
		public void onReceive(Context context, Intent actionreceiver) {
			String action = actionreceiver.getAction();
			if (action.equals(COM_IMAGINE_INFENONE_SOEKET_TCPCOMM_SOCKET_MSG)) {
				Toast.makeText(getApplicationContext(), "APP和服务器断开了连接，请检查网络设置", Toast.LENGTH_SHORT).show();
			}
			if (action.equals(COM_IMAGINE_INFENONE_SOCKET_TCPCOMM_OFFINE)) {
				Toast.makeText(getApplicationContext(), "设备已离线，暂时无法控制操作！等待设备上线后才可以控制", Toast.LENGTH_SHORT).show();
			}
			if (action.equals(CN_XIEWEIMING_APP_TCPCOMM_ONLINE)) {
				new sendshare().execute(null,null,null);
			}
			if (action.equals(CN_XIEWEIMING_APP_TCPCOMM)) {
				Bundle bundle = actionreceiver.getExtras();
				// 只处理发给我的消息
				MyLog.i(TAG, "clientID=" + id_devid + " from=" + bundle.getInt("from"));
				if (id_devid != bundle.getInt("from")) {
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
								"指令=" + cmd + " 显示设备是否开机 = isopen=" + isopen + " =code=" + code + "开关状态=isuv" + isuv);
						switch (code) {
						case 0:case 1:// 制水；-------case 2:// 缺水------------case 3:// 水满；------------
							break;
						case 4:// 检修---------------case 5:// 冲洗；--------------case 6:// 欠费；case 7:// 关机；-----------case 8:// 漏水；
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
						MyLog.i(TAG + "接收数据", " 指" + " 令字" + cmd + "原水值；=" + TDS1 + " 纯水值 = TDS2=" + TDS2
								+ " 水的质量；=waterq=" + waterq);
						float pent=((float)TDS1-(float)TDS2)/(float)TDS1;
						break;
					case 3:
						Setvlist.clear();
						Cntvlist.clear();
						if (datalen < 18)
							break;
						int count = (datalen - 18) / 4;// 求出有多少组滤芯；总长度-命令字-设备码-制水累计量-剩余可制水量/4；
						// 生成数据源
						for (int i = 1; i <= count; i++) {
							byte SetVa = buf.get();
							byte SetVb = buf.get();
							SetV = Water_control_tool.getTransform(SetVb, SetVa); // 滤芯设定值；
							MyLog.i(TAG + "接收数据",  "n组滤芯设定值；=" + SetV);
							//							每个Map结构为一条数据，key与Adapter中定义的String数组中定义的一一对应。
							Map<String, Integer> map = new HashMap<String, Integer>();
							map.put("SetV",  SetV);
							Setvlist.add(map);
						}
						for (int i = 1; i <= count; i++) {
							byte CntVa = buf.get();
							byte CntVb = buf.get();
							CntV = Water_control_tool.getTransform(CntVb, CntVa);// 剩余值；
							MyLog.i(TAG + "接收数据","n组滤芯累积值= CntV=" + CntV);
							//							每个Map结构为一条数据，key与Adapter中定义的String数组中定义的一一对应。
							Map<String, Integer> map = new HashMap<String, Integer>();
							map.put("CntV",  CntV);
							Cntvlist.add(map);
						}
						Log.i(TAG, "index="+index);
						xulik_from_txt.setText(""+(Setvlist.get(index-1).get("SetV")-Cntvlist.get(index-1).get("CntV"))+"小时");
						// 列表淡入动画
						xulik_from_txt.startAnimation(AnimationUtils.loadAnimation(
								context, R.anim.fade_in));
						xulik_too_filter.setText(""+Cntvlist.get(index-1).get("CntV")+"小时");
						xulik_too_filter.startAnimation(AnimationUtils.loadAnimation(
								context, R.anim.fade_in));
						int WaterCntb = buf.getInt();// 制水累计量
						int Paper = buf.getInt();// 剩余可制水量
						Water_control_tool.returndata(id_devid, acmdL, cmdH, myBinder);
						Setvlist.clear();
						Cntvlist.clear();
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
				new sendshare().execute(null,null,null);
			}
		}
	}
	//复位；
	class FeedbackTask extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result){
				//				Toast.makeText(Filter_details_Activity.this, "复位成功", Toast.LENGTH_LONG).show();
			}else{
				//				Toast.makeText(Filter_details_Activity.this, "复位失败", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			IotUser user = new IotUser(getApplicationContext());
			user.add_filter_history(devkey_id, id_devid);
			if(user.checkState() == IotUser.IOT_STATE_OK){
				return true;
			}else{
				return false;
			}
		}
	}
	//更新显示滤芯值；
	private void setdata() {
		Intent intent1 = Filter_details_Activity_devid.this.getIntent();
		devkey_id = intent1.getStringExtra("filter_type");
		clientID = intent1.getIntExtra("filter_clientID", -1);
		filter_from = intent1.getIntExtra("filter_from", -1);
		id_devid = intent1.getIntExtra("id_devid", -1);
		filter_too = intent1.getIntExtra("filter_too", -1);
		index = intent1.getIntExtra("index", -1);
		xulik_shate_txt.setText(devkey_id);
		if (filter_too!=-1) {
			xulik_from_txt.setText(""+(filter_too-filter_from)+"小时");
		}else{
			xulik_from_txt.setText(""+0+"小时");
		}
		if (filter_from!=-1) {
			xulik_too_filter.setText(""+filter_from+"小时");
		}else{
			xulik_too_filter.setText(""+0+"小时");
		}
	}
	//查询；
	class sendshare extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Water_control_tool.sendstatus((short) 1, id_devid, myBinder);
				Thread.sleep(100);
				Water_control_tool.sendstatus((short) 2, id_devid, myBinder);
				Thread.sleep(100);
				Water_control_tool.sendstatus((short) 3, id_devid, myBinder);
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
