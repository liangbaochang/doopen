package cn.air.doopen.hy.personage;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.air.doopen.hy.R;
import cn.air.doopen.hyapp.MyApp;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.view.TimeButton;
/**找回密码界面。*/
public class RestPwdActivity extends Activity {
	private EditText et1,send_verification_edit,et2;
	private Button bt1;
	TimeButton send_verification;
	private sendcode name;
	private Button result_pwd_result_msg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rest_pwd);
		send_verification_edit = (EditText) findViewById(R.id.rest_pwd_phone_send_verification_edit);
		et1 = (EditText) findViewById(R.id.rest_pwd_phone);
		et2 = (EditText) findViewById(R.id.rest_pwd_pwassd);
		bt1 = (Button) findViewById(R.id.rest_pwd_phone_reg_button);
		result_pwd_result_msg = (Button) findViewById(R.id.result_pwd_result_msg);
		send_verification = (TimeButton) findViewById(R.id.rest_pwd_phone_send_verification);
		send_verification.onCreate(savedInstanceState);  
		send_verification.setTextAfter("秒后重新获取").setTextBefore("点击获取验证码").setLenght(60 * 1000);  
		bt1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(send_verification_edit.getText().toString().trim())||TextUtils.isEmpty(et1.getText().toString().trim())||TextUtils.isEmpty(et2.getText().toString().trim())) {
					Toast.makeText(RestPwdActivity.this, "有信息未填",
							Toast.LENGTH_SHORT).show();
				}else{
					new reginstrasy().execute();
				}
			}
		});
		send_verification.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (send_verification_edit.getText().toString().isEmpty()&&et1.getText().toString().isEmpty()) {
					Toast.makeText(getApplicationContext(), "验证码不能为空",Toast.LENGTH_SHORT).show();
					return;
				}else{
					name=new sendcode();
					name.execute();
				}
			}
		});
		result_pwd_result_msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	class reginstrasy extends AsyncTask<Void, Void, Integer>{
		private int code;
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			switch (result) {
			case 0:
				Toast.makeText(getApplicationContext(), "找回密码成功",
						Toast.LENGTH_LONG).show();
				// 跳到注册界面
				Intent intent = new Intent(RestPwdActivity.this,
						LoginActivity.class);
				startActivity(intent);
				finish();
				break;
			case 102:
				Toast.makeText(RestPwdActivity.this, "帐号密码验证失败",
						Toast.LENGTH_SHORT).show();
				break;
			case 103:
				Toast.makeText(RestPwdActivity.this, "Token令牌验证失败",
						Toast.LENGTH_SHORT).show();
				break;
			case 104:
				Toast.makeText(RestPwdActivity.this, "用户不存在",
						Toast.LENGTH_SHORT).show();
				break;
			case 105:
				Toast.makeText(RestPwdActivity.this, "用户已禁用",
						Toast.LENGTH_SHORT).show();
				break;
			case 106:
				Toast.makeText(RestPwdActivity.this, "短信验证码错误",
						Toast.LENGTH_SHORT).show();
				break;
			case 107:
				Toast.makeText(RestPwdActivity.this, "短信验证码已过期",
						Toast.LENGTH_SHORT).show();
				break;
			case 108:
				Toast.makeText(RestPwdActivity.this, "验证码已发送，5分钟后可以重新获取",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
		@Override
		protected Integer doInBackground(Void... params) {
			IotUser user = new IotUser(RestPwdActivity.this);
			if (MyApp.cidentifier!=null) {
				code=user.restpwd(et1.getText().toString(), et2.getText().toString(),MyApp.cidentifier,Integer.parseInt(send_verification_edit.getText().toString()));
			}
			if (user.checkState() == IotUser.IOT_STATE_OK) {
				return code;
			} else {
				return code;
			}
		}
	}
	class sendcode extends AsyncTask<Void, Void, Integer>{
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			switch (result) {
			case 0:
				Toast.makeText(getApplicationContext(), "已发送",
						Toast.LENGTH_LONG).show();
				break;
			case 108:
				Toast.makeText(getApplicationContext(), "5分钟后可重新获取",
						Toast.LENGTH_LONG).show();
				break;
			case 106:
				Toast.makeText(RestPwdActivity.this, "短信验证码错误",
						Toast.LENGTH_SHORT).show();
				break;
			case 107:
				Toast.makeText(RestPwdActivity.this, "短信验证已过期",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}

		@Override
		protected Integer doInBackground(Void... params) {
			if (!TextUtils.isEmpty(et1.getText().toString().trim())) {
				IotUser user = new IotUser(RestPwdActivity.this);
				int code=user.sendverification(et1.getText().toString());
				if (user.checkState() == IotUser.IOT_STATE_OK) {
					return code;
				} else {
					return code;
				}
			}
			return null;
		}
	}
}
