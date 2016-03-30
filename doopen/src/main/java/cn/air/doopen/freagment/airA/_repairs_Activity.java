package cn.air.doopen.freagment.airA;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.air.doopen.hy.R;
import cn.air.doopen.hyapp.MyApp;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.view.propupwindowview.City_select_class;
@ContentView(value=R.layout.activity__repairs)
public class _repairs_Activity extends Activity {
	@ViewInject(R.id.air_repairs_return)//返回
	Button air_repairs_return;
	@ViewInject(R.id.air_repairs_sumbit)//提交
	Button air_repairs_sumbit;
	@ViewInject(R.id.air_repairs_name)//名字
	EditText air_repairs_name;
	@ViewInject(R.id.air_repairs_phone)//手机
	EditText air_repairs_phone;
	@ViewInject(R.id.air_repairs_fault)//故障
	EditText air_repairs_fault;
	@ViewInject(R.id.air_repairs_site)//详细地址
	EditText air_repairs_site;
	@ViewInject(R.id.air_myuser_site)
	TextView air_myuser_site;
	@ViewInject(R.id.air_site_text)
	LinearLayout air_site_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
	}

	@OnClick({ R.id.air_site_text,R.id.air_repairs_return,R.id.air_repairs_sumbit})
	public void onclickview(View v){
		switch (v.getId()) {
		case  R.id.air_site_text:       
			City_select_class id=new City_select_class(this);
			id.showPopMenu(air_myuser_site, air_site_text);
			break;
		case  R.id.air_repairs_return:          
			finish();
			break;
		case  R.id.air_repairs_sumbit:     
			if (air_myuser_site.getText().toString().equals("")
					||air_repairs_fault.getText().toString().equals("")
					||air_repairs_name.getText().toString().equals("")
					||air_repairs_phone.getText().toString().equals("")) {
				Toast.makeText(getApplicationContext(), "信息填写不完整", Toast.LENGTH_SHORT).show();
			}else{
				new  AsyncTask<Integer, Void, Integer>(){
					@Override
					protected Integer doInBackground(Integer... result) {
						IotUser iotUser=new IotUser(MyApp.getContext());
						MyApp app=(MyApp) getApplicationContext();
						int code=iotUser.addrepairs(app.getDevid(),
								app.getDevtype(), 
								air_repairs_fault.getText().toString(),
								air_repairs_name.getText().toString(), 
								air_repairs_phone.getText().toString(),
								air_myuser_site.getText().toString()+air_repairs_site.getText().toString()
								);
						return code;
					}
					@Override
					protected void onPostExecute(Integer result) {
						super.onPostExecute(result);
						if (result==0) {
							Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
							finish();
						}else{
							Toast.makeText(getApplicationContext(), "修改失败", Toast.LENGTH_SHORT).show();
						}
					};
				}.execute();
			}
			break;
		}
	}

}
