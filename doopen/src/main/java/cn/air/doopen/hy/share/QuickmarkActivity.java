package cn.air.doopen.hy.share;

import java.util.List;
import java.util.Map;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.air.doopen.config.RadioName;
import cn.air.doopen.hy.R;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.utli.MyLog;
import cn.air.doopen.utli.tool.BrightnessTools;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;
/**分享界面；*/
@ContentView(value = R.layout.activity_quickmark)
public class QuickmarkActivity extends Activity implements RadioName{
	@ViewInject(R.id.quick_dev_share_result2)/////
	private Button quick_dev_share_result2;  /////               

	@ViewInject(R.id.quick_Scan_image)/////
	private ImageView quick_Scan_image;  /////               

	@ViewInject(R.id.quick_togglutn)/////
	private ToggleButton quick_togglutn;  /////         

	private Receiverquick receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		receiver = new Receiverquick();
		IntentFilter filter = new IntentFilter();
		filter.addAction(COM_IMAGINE_INFENONE_AGREE);
		registerReceiver(receiver, filter);
		new GetQrCode().execute(1);
		setlistener();
	}


	private void setlistener() {
		quick_dev_share_result2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		quick_togglutn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean check) {
				if (check==true) {
					new GetQrCode().execute(1);
				}else{
					new GetQrCode().execute(0);
				}
			}
		});
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	//接收广播。
	class Receiverquick extends BroadcastReceiver{
		@Override
		public void onReceive(Context arg0, Intent intent) {
			String action = intent.getAction();
			if (action.equals(COM_IMAGINE_INFENONE_AGREE)) {
				Intent intit = new Intent();
				intit.setClass(getApplicationContext(), UserMessageActivity.class);
				startActivity(intit);
				finish();
			}
		}

	}
	class GetQrCode extends AsyncTask<Integer, Void, String>{
		int returned;
		@Override
		protected String doInBackground(Integer...params) {
			Intent intent = getIntent();
			IotUser user = new IotUser(getApplicationContext());
			List<Map<String, Object>> listret = user.shareDevQR(intent.getIntExtra("devid_puick", 0),intent.getStringExtra("owner_quick"),60,params[0]);
			returned=(Integer) listret.get(0).get("returned");
			if (returned==0) {
				String code=(String) listret.get(0).get("Quickmark");
				MyLog.i("二维码值", code);
				return code;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (returned==0) {
				if(result!=null){
					quick_Scan_image.setImageBitmap(BrightnessTools.createImage(result));
					MyLog.i("二维码值", result);
				}else{
					MyLog.i("获取二维码代码失败");
				}
			}else if (returned==204||returned==205) {
				Toast.makeText(getApplicationContext(), "您是被分享用户,无法分享哦！", Toast.LENGTH_LONG).show();
				finish();
			}else{
				Toast.makeText(getApplicationContext(), "获取二维码失败", Toast.LENGTH_LONG).show();
			}
		}

	}
}
