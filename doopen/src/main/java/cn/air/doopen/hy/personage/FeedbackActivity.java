package cn.air.doopen.hy.personage;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.air.doopen.hy.R;
import cn.air.doopen.socket.IotUser;
/**反馈提交界面；*/
public class FeedbackActivity extends Activity {
	private Button bt1;
	private EditText et1;
	private Button feed_btn_result;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_feedback);
		feed_btn_result = (Button)findViewById(R.id.feed_btn_result);
		bt1 = (Button)findViewById(R.id.button1);
		et1 = (EditText)findViewById(R.id.editText1);

		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new FeedbackTask().execute(null,null,null);
			}
		});
		feed_btn_result.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	class FeedbackTask extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result){
				Toast.makeText(FeedbackActivity.this, "提交成功", Toast.LENGTH_LONG).show();
				et1.setText("");
				FeedbackActivity.this.finish();
			}else{
				Toast.makeText(FeedbackActivity.this, "提交失败", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			IotUser user = new IotUser(getApplicationContext());
			user.feedback(et1.getText().toString());
			et1.setSelection(et1.getText().toString().length());
			if(user.checkState() == IotUser.IOT_STATE_OK){
				return true;
			}else{
				return false;
			}
		}
	}
}
