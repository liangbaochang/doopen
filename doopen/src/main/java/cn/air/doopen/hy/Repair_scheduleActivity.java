package cn.air.doopen.hy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.air.doopen.hyapp.MyApp;
import cn.air.doopen.socket.IotUser;
@ContentView(value=R.layout.activity_repair_schedules)
public class Repair_scheduleActivity extends Activity {
	@ViewInject(R.id.reqair_sch_text_time)
	TextView reqair_sch_text_time;
	@ViewInject(R.id.reqair_sch_text_status)
	TextView reqair_sch_text_status;
	@ViewInject(R.id.reqair_sch_text_progress)
	TextView reqair_sch_text_progress;
	@ViewInject(R.id.air_repairs_schedule_return)
	Button air_repairs_schedule_return;
	List<Map<String, Object>> mDevData = new ArrayList<Map<String, Object>>();
	private IotUser user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		user = ((MyApp)getApplication()).user;
		new GetDevListTask().execute();

	}
	public void air_repairs_schedule_returnonclick(View v){
		finish();
	}
	public class GetDevListTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			Intent getint=getIntent();
			mDevData=user.getrepair_schedule(getint.getIntExtra("msgid", -1));
			return user.checkState();
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result==IotUser.IOT_STATE_OK) {
				reqair_sch_text_time.setText("更新时间:"+mDevData.get(mDevData.size()-1).get("time"));
				reqair_sch_text_status.setText("进度状态:"+mDevData.get(mDevData.size()-1).get("status"));
				reqair_sch_text_progress.setText("进度描述:"+mDevData.get(mDevData.size()-1).get("progress"));
			}

		}

	}
}
