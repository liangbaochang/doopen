package cn.air.doopen.hy.device_control.water_control_tool;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import cn.air.doopen.hy.R;
/**各控制器的设备界面；*/
@ContentView(value = R.layout.activity_water_seting)
public class Water_setingActivity extends Activity {
	//初始化控件；
	@ViewInject(R.id.personage_upgrade)     
	private LinearLayout personage_upgrade;                
	@ViewInject(R.id.seting_result)    
	private Button seting_result;         
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
	}
	@OnClick({ R.id.personage_upgrade,R.id.seting_result})
	public void onclickview(View v){
		switch (v.getId()) {
		case  R.id.personage_upgrade://升级；
			Intent intent=new Intent(getApplicationContext(), Upgrade_Water_Activity.class);
			startActivity(intent);
			break;
		case  R.id.seting_result:
			finish();
			break;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.water_seting, menu);
		return true;
	}

}
