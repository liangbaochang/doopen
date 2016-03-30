package cn.air.doopen.hy.device_control.water_control_tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.air.doopen.hy.R;
import cn.air.doopen.hyapp.MyApp;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.utli.MyLog;
/**查询设备的版本和更新*/
@ContentView(value = R.layout.activity_upgrade__water)
public class Upgrade_Water_Activity extends Activity {
	@ViewInject(R.id.upgrade_result)     
	private Button upgrade_result;               
	@ViewInject(R.id.update_firmware_btn)    
	private Button update_firmware_btn;              
	@ViewInject(R.id.versions_msg)        
	private TextView versions_msg;              
	@ViewInject(R.id.update_time_text)              
	private TextView update_time_text;               
	@ViewInject(R.id.versions_describe)           
	private TextView versions_describe;         
	@ViewInject(R.id.update_msg_txt)        
	private TextView update_msg_txt;         
	@ViewInject(R.id.upgrade_line)     
	private LinearLayout upgrade_line;             
	private MyApp app;
	private IotUser iotUser;
	private Handler handler;
	private int UPDATE_TEXT=1;
	private int UPDATE_BTN_MSG=2;
	private int ANONYMOUS=3;
	private int ret;
	List<Map<String, Object>> map=new ArrayList<Map<String,Object>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		app=(MyApp) getApplicationContext();
		Versions();
	}
	//启动界面后查询；
	private void Versions() {
		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if (msg.arg1==UPDATE_TEXT) {
					if (!map.isEmpty()) {
						upgrade_line.setVisibility(View.VISIBLE);
						versions_msg.setText(""+map.get(0).get("ver"));
						update_time_text.setText(""+map.get(0).get("time"));
						versions_describe.setText(""+map.get(0).get("info"));
					}
				}
				if (msg.arg1==ANONYMOUS) {
					upgrade_line.setVisibility(View.GONE);
					update_msg_txt.setVisibility(View.VISIBLE);
				}
				if (msg.arg1==UPDATE_BTN_MSG) {
					if ((Integer)msg.obj==0) {
						Toast.makeText(getApplicationContext(), "请求成功", 1).show();
						update_firmware_btn.setText("已发出请求");
					}else{
						Toast.makeText(getApplicationContext(), "请求失败", 1).show();
					}
				}
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				MyLog.i("app.getFirmwareID()", app.getFirmwareID()+"app.getDevtype()=="+app.getDevtype());
				iotUser=new IotUser(getApplicationContext());
				map=iotUser.getfirmware_update(app.getFirmwareID(), app.getDevtype());
				Message msg=handler.obtainMessage();
				if (map==null||map.size()==0) {
					msg.arg1=ANONYMOUS;
				}else{
					msg.arg1=UPDATE_TEXT;
				}
				handler.sendMessage(msg);
			}
		}).start();

	}
	@OnClick({ R.id.upgrade_result, R.id.update_firmware_btn})
	public void onclickview(View v){
		switch (v.getId()) {
		case  R.id.upgrade_result:
			finish();
			break;
		case  R.id.update_firmware_btn:
			//请求更新设备硬件；
			new Thread(new Runnable() {
				@Override
				public void run() {
					iotUser=new IotUser(getApplicationContext());
					ret=iotUser.update_firmware((Integer)map.get(0).get("fileId"), app.getDevid());
					Message msg=handler.obtainMessage();
					msg.arg1=UPDATE_BTN_MSG;
					msg.obj=ret;
					handler.sendMessage(msg);
				}
			}).start();
			break;
		}
	}

}
