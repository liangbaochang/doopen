package cn.air.doopen.freagment.airA;

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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import cn.air.doopen.adapter.FactlityActapter;
import cn.air.doopen.hy.R;
import cn.air.doopen.hy.Repair_scheduleActivity;
import cn.air.doopen.hyapp.MyApp;
import cn.air.doopen.logic.BindingListener;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.utli.MyLog;
@ContentView(value=R.layout.activity_repair__list)
public class Repair_List_Activity extends Activity {
	@ViewInject(R.id.air_repairs_list_return)
	Button air_repairs_list_return;
	@ViewInject(R.id.air_repairs_list_add)
	Button air_repairs_list_add;
	@ViewInject(R.id.air_repairs_list_view)
	ListView mListView;
	private List<Map<String, Object>> mDevData = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> mDevList = new ArrayList<Map<String, Object>>();
	private IotUser user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		user = ((MyApp)getApplication()).user;
		setonclik();
		Intent intent=getIntent();
		boolean ishow=intent.getBooleanExtra("isshow", true);
		if (ishow==false) {
			air_repairs_list_add.setVisibility(View.GONE);
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		new GetDevListTask().execute();
	}
	private void setonclik() {
		air_repairs_list_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		air_repairs_list_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(Repair_List_Activity.this, _repairs_Activity.class));
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent=new Intent(Repair_List_Activity.this, Repair_scheduleActivity.class);
				intent.putExtra("msgid", (Integer)mDevList.get(position).get("id"));
				startActivity(intent);
			}
		});
	}

	public class GetDevListTask extends AsyncTask<Void, Void, Integer> {

		private FactlityActapter adapter;

		@Override
		protected Integer doInBackground(Void... params) {
			return RefreshList();
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (!mDevList.isEmpty()) {
				adapter = new FactlityActapter(mDevList,2);
				mListView.setAdapter(adapter);
			}
		}

	}
	/**
	 * 更新设备列表,只能运行在线程
	 */
	protected int RefreshList() {
		mDevData = user.getrepairist();
		if (user.checkState() == IotUser.IOT_STATE_OK) {
			mDevList.clear();
			mDevList = BindingListener.getdata(mDevData);
			MyLog.i("Factlity mdevList", "a " + mDevList.toString());
		}
		return user.checkState();
	}
}
