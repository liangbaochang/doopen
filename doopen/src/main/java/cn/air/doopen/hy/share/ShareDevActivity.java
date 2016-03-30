package cn.air.doopen.hy.share;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cn.air.doopen.hy.R;
import cn.air.doopen.socket.IotUser;
/**分享设备界面；上一个界面传设备id过来；在这个界面访问服务器获取二维码内容编码，获取成功之后产生二维码图片；还可以通过输入其他用户的账号；访问服务器进行搜索判断；做出相应界面处理；需要：截取二维码图片后可以分享二维码图片给别人；使别人可以远程控制职智能设备；*/
public class ShareDevActivity extends Activity {
	private ImageView sweepIV;
	private Button bt1;
	private EditText et1;

	private Button dev_share_result;
	private Button quick_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_dev);
		sweepIV = (ImageView)findViewById(R.id.Scan_image);
		et1 = (EditText)findViewById(R.id.edt_operator_name);
		bt1 = (Button)findViewById(R.id.share_true_btn);
		quick_btn = (Button)findViewById(R.id.quick_btn);
		dev_share_result = (Button)findViewById(R.id.dev_share_result);
		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AsyncTask<Void, Void, Integer>(){
					@Override
					protected void onPostExecute(Integer result) {
						super.onPostExecute(result);
						if(result==0){
							Toast.makeText(getApplicationContext(), "分享成功,去消息中心同意吧！", Toast.LENGTH_LONG).show();
						}else if (result==204||result==205) {
							Toast.makeText(getApplicationContext(), "您是被分享用户,无法分享哦！", Toast.LENGTH_LONG).show();
						}else if (result==208) {
							Toast.makeText(getApplicationContext(), "管理员已变更", Toast.LENGTH_LONG).show();
						}else{
							Toast.makeText(getApplicationContext(), "分享失败", Toast.LENGTH_LONG).show();
						}
					}
					IotUser user = new IotUser(getApplicationContext());
					@Override
					protected Integer doInBackground(Void... params) {
						if(et1.getText().toString().length() > 0){
							int code=user.shareDev(et1.getText().toString(), getIntent().getIntExtra("devid", 0));
							return code;
						}
						return -1;
					}

				}.execute();
			}
		});
		dev_share_result.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		quick_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent pet = getIntent();
				int devid = pet.getIntExtra("devid", 0);
				String username = pet.getStringExtra("owner");
				Intent intent=new Intent(getApplicationContext(), QuickmarkActivity.class);
				intent.putExtra("devid_puick", devid);
				intent.putExtra("owner_quick", username);
				startActivity(intent);
			}
		});
	}


}
