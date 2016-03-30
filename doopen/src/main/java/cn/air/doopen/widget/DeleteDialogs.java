package cn.air.doopen.widget;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import cn.air.doopen.hy.R;
import cn.air.doopen.socket.IotUser;

public class DeleteDialogs extends Activity {

	private Button delete_msg_true;
	private Button delete_msg_false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_dialogs);
		initview();
	}
	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	private void initview() {
		delete_msg_true = (Button) this.findViewById(R.id.delete_msg_true);
		delete_msg_false = (Button) this.findViewById(R.id.delete_msg_false);
		delete_msg_true.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new deleteshareMsg().execute(null,null,null);
			}
		});
		delete_msg_false.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	private int code;
	class deleteshareMsg extends AsyncTask< Void, Void, Void>{
		@Override
		protected Void doInBackground(Void... arg0) {
			IotUser iotUser=new IotUser(getApplicationContext());
			code=iotUser.deleteDevmsg();
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (code==0) {
				Toast.makeText(getApplicationContext(), "清空消息成功", 2).show();
			}else{
				Toast.makeText(getApplicationContext(), "清空消息失败", 2).show();
			}
		}
	}
}
