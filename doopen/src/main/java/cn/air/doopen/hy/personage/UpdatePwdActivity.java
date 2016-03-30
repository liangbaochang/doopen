package cn.air.doopen.hy.personage;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.air.doopen.hy.R;
import cn.air.doopen.socket.IotUser;
/**修改密码界面*/
@ContentView(value = R.layout.activity_update_pwd)
public class UpdatePwdActivity extends Activity {
	@ViewInject(R.id.update_pwd_old_edit)/////旧密码；
	private EditText update_pwd_old_edit;  /////               
	@ViewInject(R.id.update_pwd_new_edit)/////新密码；
	private EditText update_pwd_new_edit;  /////               
	@ViewInject(R.id.update_pwd_newto_edit)/////新密码；
	private EditText update_pwd_newto_edit;  /////               

	@ViewInject(R.id.update_pwd_new_btn)/////确认修改；
	private Button update_pwd_new_btn;  /////               
	@ViewInject(R.id.devshare_result_msg)/////确认修改；
	private Button devshare_result_msg;  /////               
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		setview();
	}

	private void setview() {
		devshare_result_msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();				
			}
		});
		update_pwd_new_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// 弹出对话框
				new AsyncTask<Void, Void, Integer>(){
					@Override
					protected void onPostExecute(Integer result) {
						// TODO Auto-generated method stub
						super.onPostExecute(result);
						if (result==0) {
							Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_LONG).show();
						}else{
							Toast.makeText(getApplicationContext(), "修改失败！", Toast.LENGTH_LONG).show();
						}
					}
					@Override
					protected Integer doInBackground(Void... params) {
						IotUser user = new IotUser(getApplicationContext());
						String old=update_pwd_old_edit.getText().toString();
						String newold=update_pwd_new_edit.getText().toString();
						String newto=update_pwd_newto_edit.getText().toString();
						if (!newold.equals(newto)) {
							Toast.makeText(getApplicationContext(), "输入新密码不一致", Toast.LENGTH_LONG).show();
							return null;
						}else{
							int code=user.update_pwd(old, newto);
							return code;
						}
					}
				}.execute(null,null,null);

			}
		});
	}
}
