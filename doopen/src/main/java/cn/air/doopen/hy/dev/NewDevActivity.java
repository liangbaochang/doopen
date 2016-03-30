package cn.air.doopen.hy.dev;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.air.doopen.hy.R;
import cn.air.doopen.logic.BindingListener;
import cn.air.doopen.wifi.LinkDevActivity;
import cn.air.doopen.wifi.WifiAdmin;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**发现新设备列表界面；点击之后进入配置界面；完成配置后设备绑定改账户下；进入主界面；*/
public class NewDevActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {
	public static final int OK = 0;
	public static final int NO_DEV = 1;
	public static final int NO_WIFI = 2;
	private SwipeRefreshLayout mSwipeLayout;
	private ListView mListView;
	WifiAdmin wifiAdmin = null;
	private ArrayList<HashMap<String, Object>> list;
	private BaseAdapter adapter;
	private Button find_dev_result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_dev);
		//newWIF管理对象；
		wifiAdmin = new WifiAdmin(this);
		list = new ArrayList<HashMap<String, Object>>();
		adapter = new MyAdapter(this);
		mListView = (ListView) findViewById(R.id.id_listview);
		find_dev_result = (Button) findViewById(R.id.find_dev_result);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
		//设置下拉的监听和颜色；
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeColors(Color.RED);
		mListView.setAdapter(adapter);
		// 绑定列表点击事件
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// 跳转到配置界面,传入ssid
				Intent intent = new Intent(NewDevActivity.this,
						LinkDevActivity.class);
				//						ConfigAddDevActivity.class);
				intent.putExtra("ssid",(String)list.get(arg2).get("dev_ssid"));
				intent.putExtra("bssid",(String)list.get(arg2).get("dev_bssid"));
				intent.putExtra("channel", (Integer) list.get(arg2).get("channel"));
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});
		find_dev_result.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		new DiscoveryTask().execute();
	}
	//下拉后调用此方法加载数据；
	@Override
	public void onRefresh() {
		new DiscoveryTask().execute();
	}

	//搜索WIFI热点的异步任务方法；
	class DiscoveryTask extends AsyncTask<Void, Void, Integer> {

		protected Integer doInBackground(Void... params) {
			if (wifiAdmin.checkState() == WifiManager.WIFI_STATE_ENABLED) {
				list.clear();
				BindingListener bindingListener=new  BindingListener(NewDevActivity.this);
				list= bindingListener.RefreshList(wifiAdmin);
				if (!list.isEmpty()) {
					return OK;
				} else {
					return NO_DEV;
				}
			} else {
				return NO_WIFI;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == NO_WIFI) {
				Toast.makeText(NewDevActivity.this, "请开启手机WIFI",
						Toast.LENGTH_LONG).show();
			} else if (result == NO_DEV) {
				Toast.makeText(NewDevActivity.this, "没有新设备",
						Toast.LENGTH_LONG).show();
			} else if (result == OK) {

			}
			adapter.notifyDataSetChanged();
			mSwipeLayout.setRefreshing(false);
		}

	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.new_dev_item, null);
				holder.headImg = (ImageView) convertView
						.findViewById(R.id.imageView1);
				holder.wifiImg = (ImageView) convertView
						.findViewById(R.id.imageView2);
				holder.title = (TextView) convertView
						.findViewById(R.id.textView1);
				holder.info = (TextView) convertView
						.findViewById(R.id.textView2);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.headImg.setImageResource((Integer) list.get(position).get(
					"dev_head_img"));
			holder.wifiImg.setImageResource((Integer) list.get(position).get(
					"dev_wifi_img"));
			holder.title.setText((String) list.get(position).get("dev_name"));
			holder.info.setText((String) list.get(position).get("dev_bssid"));
			return convertView;
		}
		private class ViewHolder {
			ImageView headImg;
			ImageView wifiImg;
			TextView title;
			TextView info;
		}
	}
}
