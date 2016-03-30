package cn.air.doopen.hy.personage;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import cn.air.doopen.hy.R;
/**关于我们主界面*/
public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		TextView version_text=(TextView)findViewById(R.id.version_text);
		try {
			//获取app的版本号显示
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			version_text.setText("版本号:"+info.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void about_btn_result_onclick(View v){
		finish();
	}
}
