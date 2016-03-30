package cn.air.doopen.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.widget.TextView;
import cn.air.doopen.hy.MainActivity;
import cn.air.doopen.hy.R;
import cn.air.doopen.hy.personage.LoginActivity;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.socket.TcpCommSerivce;
import cn.air.doopen.socket.TcpCommSerivce.MyBinder;
import cn.air.doopen.utli.MyLog;

public class ShowadlogActivity extends Activity {
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
		Intent intent = new Intent(this, TcpCommSerivce.class);
		bindService(intent, connection, 0);
		final TextView tv = new TextView(ShowadlogActivity.this);
		tv.setText("帐户已经在其它设备登录！");
		tv.setTextSize(20);
		tv.setPadding(20, 30, 20, 30);
		AlertDialog.Builder builder = new AlertDialog.Builder(ShowadlogActivity.this);
		builder.setCancelable(false);
		builder.setTitle("账户已在其他设备登录！").setIcon(R.drawable.login_error_icon).setView(tv)
		.setNegativeButton("退出", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				outlogin();
			}
		});
		builder.show();
	}
	void outlogin(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				IotUser user=new IotUser(getApplicationContext());
				int code=user.outlogin();
				if (code==0) {
					MyLog.i("", "登出成功！");
				}else{
					MyLog.i("", "登出失败！");
				}
			}
		}).start();
		if (binder.checkStatus() == TcpCommSerivce.STAT_ONLINE) {
			binder.logout();
			MyLog.i("已调用登出");
		}
		Intent seeting = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(seeting);
		// 注销按钮被按了//指定关闭首页；
		MainActivity.instance.finish();
		finish();
		System.exit(0);
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() != KeyEvent.ACTION_UP) {
			outlogin();
			return true;
		} else {
			return super.dispatchKeyEvent(event);
		}
	}
}
