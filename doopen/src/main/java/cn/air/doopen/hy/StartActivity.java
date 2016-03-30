package cn.air.doopen.hy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import cn.air.doopen.config.Deploy;
import cn.air.doopen.hy.R;
import cn.air.doopen.hy.personage.LoginActivity;
import cn.air.doopen.hyapp.MyApp;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.utli.ActivityManager;
import cn.air.doopen.utli.MyLog;
/**
 * 应用启动界面,动画加载,条件判断，检修是否有新版本。
 */
public class StartActivity extends Activity  implements Deploy{

	private SharedPreferences config;
	private Handler handler;
	private ImageView iv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityManager.getInstance().addActivity(this);
		config = getSharedPreferences("config", MODE_PRIVATE);
		setContentView(R.layout.activity_start);
		iv = (ImageView)findViewById(R.id.imageView1);
		MyApp myapp = (MyApp) getApplication();
		// 如果程序在运行中,则不显示Splash界面了
		if (myapp.isRunning) {
			Intent intent = new Intent(StartActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			MyLog.i("Start", "程序已经运行,直接进入主界面");
			finish();
		} else {
			// 淡入动画
			iv.startAnimation(AnimationUtils.loadAnimation(StartActivity.this, R.anim.fade_in));
			handler = new Handler();
			//             延时执行的事情
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					new AsyncTask<Void, Void, Void>() {
						@Override
						protected Void doInBackground(Void... params) {
							MyApp myapp = (MyApp)getApplication();
							// 检测新公开的版本
							String token = config.getString("token", "");
							// 判断是否登录标记
							if (token.length() == 0) {
								// 跳到登录界面
								Intent intent = new Intent(StartActivity.this,LoginActivity.class);
								startActivity(intent);
							} else {
								// 进行令牌登录,成功则进入我的设备
								myapp.user.login(token);
								if (myapp.user.checkState() == IotUser.IOT_STATE_OK) {
									Intent intent = new Intent(StartActivity.this,MainActivity.class);
									startActivity(intent);
									MyLog.i("Start", "令牌验证成功");
								} else {
									// 令牌失效,可能在别处登录造成的
									MyLog.e("Start", "令牌验证失败,需要登录");
									// 跳到登录界面
									Intent intent = new Intent(
											StartActivity.this,
											LoginActivity.class);
									startActivity(intent);
								}

							}
							finish();
							return null;
						}
					}.execute();
				}
			}, 1000);
		}
	}

}
