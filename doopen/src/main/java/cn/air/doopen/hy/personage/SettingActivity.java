package cn.air.doopen.hy.personage;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableRow;
import cn.air.doopen.hy.MainActivity;
import cn.air.doopen.hy.R;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.socket.TcpCommSerivce;
import cn.air.doopen.socket.TcpCommSerivce.MyBinder;
import cn.air.doopen.utli.MyLog;
/**设置界面；*/
public class SettingActivity extends Activity implements OnClickListener{
	private Button bt_logout;
	private TableRow tr_debug;
	private TableRow tr_userhome;
	private TableRow update_pwd;
	private Button seting_result_seting;
	MyBinder binder;
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			binder = (TcpCommSerivce.MyBinder) service;
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		bt_logout = (Button)findViewById(R.id.button1);
		bt_logout.setOnClickListener(this);

		update_pwd = (TableRow)findViewById(R.id.update_pwd);
		update_pwd.setOnClickListener(this);

		tr_debug = (TableRow)findViewById(R.id.more_page_debug);
		tr_debug.setOnClickListener(this);

		tr_userhome = (TableRow)findViewById(R.id.more_page_row0);
		tr_userhome.setOnClickListener(this);

		seting_result_seting = (Button)findViewById(R.id.seting_result_seting);
		seting_result_seting.setOnClickListener(this);
		Intent intent = new Intent(this, TcpCommSerivce.class);
		bindService(intent, connection, 0);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button1:
			new AsyncTask<Integer, Integer, Integer>(){
				@Override
				protected void onPreExecute() {
					bt_logout.setText("正在登出...");
					bt_logout.setEnabled(false);
				};
				@Override
				protected Integer doInBackground(Integer... params) {
					IotUser user=new IotUser(getApplicationContext());
					int code=user.outlogin();
					return code;
				}
				@Override
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					bt_logout.setEnabled(true);
					if (result==0) {
						bt_logout.setText("登出成功");
						if (binder.checkStatus() == TcpCommSerivce.STAT_ONLINE) {
							binder.logout();
							MyLog.i("已调用登出");
						}
						// 转到登录窗口;
						Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						// 注销按钮被按了//指定关闭首页；
						MainActivity.instance.finish();
						SettingActivity.this.finish();
					}else{
						bt_logout.setText("登出失败");
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						bt_logout.setText("退出登录");
					}
				}
			}.execute();
			break;
		case R.id.more_page_debug:
			break;
		case R.id.more_page_row0:
			// 转到用户中心
			Intent intent2 = new Intent(this, UserCenterActivity.class);
			startActivity(intent2);
			break;
		case R.id.update_pwd:
			// 修改密码
			Intent updatepwd = new Intent(this, UpdatePwdActivity.class);
			startActivity(updatepwd);
			break;
		case R.id.seting_result_seting:
			finish();
			break;
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
}
