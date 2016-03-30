package cn.air.doopen.hy.share;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.air.doopen.adapter.DevShareAdapter;
import cn.air.doopen.hy.R;
import cn.air.doopen.hyapp.MyApp;
import cn.air.doopen.logic.BindingListener;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.view.CustomProgressDialog;
import cn.air.doopen.view.mylistview.SlideListView2;
import android.widget.Button;
/**设备分享列表；显示的是设备列表点击后可管理分享和分享*/
public class DevShareActivity extends Activity {
	public  String TAG="UserMessageActivity";
	private Button devshare_result;
	private SlideListView2 devshare_listview;
	private IotUser user;
	private List<Map<String, Object>> mDevData = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> mDevlist= new ArrayList<Map<String, Object>>();
	private CustomProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dev_share);
		dialog =new CustomProgressDialog(DevShareActivity.this);  
		dialog.show();  
		initview();

	}

	private void initview() {
		devshare_result=(Button)findViewById(R.id.devshare_result);
		devshare_listview=(SlideListView2)findViewById(R.id.devshare_listview);
		devshare_listview.initSlideMode(SlideListView2.MOD_RIGHT);
		devshare_result.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		devshare_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				devshare_listview.scrollLeft();
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
		setview();
	}
	public  void setview() {
		user = ((MyApp) getApplication()).user;
		new msgasytask().execute(null,null,null);
	}
	class msgasytask extends AsyncTask< Void, Void, Integer>{
		@Override
		protected Integer doInBackground(Void... arg0) {
			mDevData.clear();
			return RefreshList();
		}
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (mDevData!=null) {
				DevShareAdapter mAdapter = new DevShareAdapter(getApplicationContext(),mDevData, devshare_listview);
				devshare_listview.setAdapter(mAdapter);
				if (dialog!=null) {
					dialog.dismiss();
				}
			}
		}
	}
	/**
	 * 更新设备列表,只能运行在线程
	 */
	protected int RefreshList() {
		mDevlist = user.getDevList();
		if (user.checkState() == IotUser.IOT_STATE_OK) {
			mDevData=BindingListener.getdata(mDevlist);
		}
		return user.checkState();
	}
}
