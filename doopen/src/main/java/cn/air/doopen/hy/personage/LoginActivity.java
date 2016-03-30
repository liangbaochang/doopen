package cn.air.doopen.hy.personage;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.air.doopen.hy.MainActivity;
import cn.air.doopen.hy.R;
import cn.air.doopen.hyapp.MyApp;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.utli.MyLog;
import cn.air.doopen.utli.T;
import cn.air.doopen.view.RippleBackground;
import cn.air.doopen.widget.RoundImageView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * 登录界面,实现登录远程服务器,获取Token和Cookies用于后续操作授权
 * @author xwm
 */
public class LoginActivity extends Activity  {
	protected static final int USER_LOGIN_OK = 0;
	public static final int USER_LOGIN_FAIL = 1;
	private Button bt;
	private Button bt2;
	private AutoCompleteTextView tv_name;
	private EditText et_pwd;
	private Dialog pd;
	private Button rest_pwd_btn;
	private mHomeKeyEventReceiver mHomeKeyEventReceiver;
	private ToggleButton Status_config;
	private RoundImageView personage_personal_details;
	private LinearLayout server_code_layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		init();
		setview();
		setliener();
	}
	private void init() {
		mHomeKeyEventReceiver=new mHomeKeyEventReceiver();
		//注册广播  
		registerReceiver(mHomeKeyEventReceiver, new IntentFilter(  
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS));  
	}
	private void setview() {
		tv_name = (AutoCompleteTextView) findViewById(R.id.phone);
		personage_personal_details = (RoundImageView) findViewById(R.id.personage_personal_details);
		et_pwd = (EditText) findViewById(R.id.password);
		bt = (Button) findViewById(R.id.email_sign_in_button);
		server_code_layout = (LinearLayout) findViewById(R.id.server_code_layout);
		Status_config = (ToggleButton) findViewById(R.id.Status_config);
		bt2 = (Button) findViewById(R.id.reg_button);
		rest_pwd_btn = (Button) findViewById(R.id.rest_pwd_btn);
	}
	private void setliener() {
		final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
		final MyApp app=(MyApp) getApplicationContext();
		//默认是测试服务器；
		app.setType_of_service(true);
		//正式测试服务器切换；
		Status_config.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked==true) {
					app.setType_of_service(true);
					MyLog.i("正式");
				}else{
					app.setType_of_service(false);
					MyLog.i("测试");
				}
			}
		});
		//显示切换按钮；
		personage_personal_details.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				server_code_layout.setVisibility(View.VISIBLE);
				return false;
			}
		});
		//登录；
		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				rippleBackground.startRippleAnimation();
				bt.setClickable(false);
				bt.setText("登录...");
				new AsyncTask<Void, Void, Integer>() {
					@Override
					protected void onPreExecute() {
						super.onPreExecute();
					}

					@Override
					protected void onPostExecute(Integer result) {
						super.onPostExecute(result);
						if (pd != null && pd.isShowing()) {
							pd.dismiss();
						}
						rippleBackground.stopRippleAnimation();
						bt.setClickable(true);
						bt.setText("登录");
						//登录情况；
						switch (result) {
						case -1:
							T.setTost(LoginActivity.this, "登录失败，请检查网络连接");
							break;
						case 0:
							Intent intent = new Intent(LoginActivity.this,
									MainActivity.class);
							intent.putExtra("needLoad", true);
							startActivity(intent);
							T.setTost(LoginActivity.this, "登录成功");
							finish();
							break;
						case 102:
							T.setTost(LoginActivity.this, "帐号密码验证失败");
							break;
						case 103:
							Toast.makeText(LoginActivity.this, "Token令牌验证失败",
									Toast.LENGTH_LONG).show();
							break;
						case 104:
							Toast.makeText(LoginActivity.this, "用户不存在",
									Toast.LENGTH_LONG).show();
							break;
						case 105:
							Toast.makeText(LoginActivity.this, "用户已禁用",
									Toast.LENGTH_LONG).show();
							break;
						}
					}

					@Override
					protected Integer doInBackground(Void... params) {
						IotUser user = new IotUser(LoginActivity.this);
						int code = user.login(tv_name.getText().toString(), et_pwd
								.getText().toString(),app.isType_of_service());
						if (user.checkState() == IotUser.IOT_STATE_OK) {
							return code;
						} else {
							return code;
						}
					}

				}.execute(null, null, null);
			}
		});
		bt2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
			}
		});
		rest_pwd_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						RestPwdActivity.class);
				startActivity(intent);
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		unregisterReceiver(mHomeKeyEventReceiver); 
		super.onDestroy();
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() != KeyEvent.ACTION_UP) {
			exitBy2Click(); //调用双击退出函数 
			return true;
		} else {
			return super.dispatchKeyEvent(event);
		}
	}

	/** * 双击退出函数 */
	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", 1).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
		} else {
			finish();
			System.exit(0);
		}
	}
	/** 
	 * 监听是否点击了home键将客户端推到后台 
	 */  
	class mHomeKeyEventReceiver extends BroadcastReceiver {  
		String SYSTEM_REASON = "reason";  
		String SYSTEM_HOME_KEY = "homekey";  
		String SYSTEM_HOME_KEY_LONG = "recentapps";  

		@Override  
		public void onReceive(Context context, Intent intent) {  
			String action = intent.getAction();  
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {  
				String reason = intent.getStringExtra(SYSTEM_REASON);  
				if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {  
					//表示按了home键,程序到了后台  
					System.exit(0);
				}else if(TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)){  
					//表示长按home键,显示最近使用的程序列表  
				}  
			}   
		}  
	};  
}
