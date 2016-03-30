package cn.air.doopen.hy.device_control.xulik;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import cn.air.doopen.hy.R;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.utli.MyLog;
import cn.air.doopen.view.mylistview.SlideListView2;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.RentalsSunHeaderView;
/**滤芯复位历史的信息显示界面***/
@ContentView(value = R.layout.activity_filter_history)
public class Filter_history_Activity extends Activity implements SwipeRefreshLayout.OnRefreshListener{
	private String TAG="Filter_history_Activity";
	@ViewInject(R.id.xulik_fliter_history_result)Button xulik_fliter_history_result;
	@ViewInject(R.id.xulik_chore_msg)Button xulik_chore_msg;
	@ViewInject(R.id.personage_hua_listview_xulik)SlideListView2 personage_hua_listview_xulik;
	@ViewInject(R.id.material_style_ptr_frame_xuliki_lvxin)PtrFrameLayout mPtrFrame;
	private List<Map<String, Object>> mUserList = new ArrayList<Map<String, Object>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		init();
	}
	private void init() {
		//刷新前；
		RentalsSunHeaderView header = new RentalsSunHeaderView(this);
		header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
		header.setPadding(0, LocalDisplay.dp2px(15), 0, LocalDisplay.dp2px(10));
		header.setUp(mPtrFrame);
		mPtrFrame.setLoadingMinTime(1000);
		mPtrFrame.setDurationToCloseHeader(1500);
		mPtrFrame.setHeaderView(header);
		mPtrFrame.addPtrUIHandler(header);
		mPtrFrame.postDelayed(new Runnable() {

			@Override
			public void run() {
				MyLog.i(TAG, "刷新");
				new GetDevUserListTask().execute();
				mPtrFrame.refreshComplete();
				mPtrFrame.autoRefresh(true);
			}
		}, 1000);
		//刷新中
		mPtrFrame.setPtrHandler(new PtrHandler() {
			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				frame.postDelayed(new Runnable() {
					@Override
					public void run() {
						MyLog.i(TAG, "刷新");
						//刷新后；
						new GetDevUserListTask().execute();
						mPtrFrame.refreshComplete();
					}
				}, 10);
			}

			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
				// 默认实现，根据实际情况做改动
				return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
			}
		});
		personage_hua_listview_xulik.initSlideMode(SlideListView2.MOD_RIGHT);
		personage_hua_listview_xulik.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				personage_hua_listview_xulik.scrollLeft();
			}
		});
		xulik_fliter_history_result.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		xulik_chore_msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(Filter_history_Activity.this);
				builder.setTitle("确定清空所有记录吗");
				builder.setNegativeButton("取消", null);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						new voreTask().execute(getIntent().getIntExtra("devid", 0));
					}
				});
				builder.show();
			}
		});
	}

	@Override
	public void onRefresh() {
		new GetDevUserListTask().execute();
	}
	private int msg=-1;
	class voreTask extends AsyncTask<Integer,Integer,Integer>{
		//后面尖括号内分别是参数（例子里是线程休息时间），进度(publishProgress用到)，返回值 类型
		protected Integer doInBackground(Integer... params) {
			IotUser iotUser=new IotUser(getApplicationContext());
			msg=iotUser.eliminate_id_filter_history_xulik(params[0]);
			return msg;
		}
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (msg==0) {
				Toast.makeText(getApplicationContext(), "已清空", 1).show();
				new GetDevUserListTask().execute();
			}else{
				Toast.makeText(getApplicationContext(), "没有清空", 1).show();
			}
		}
	}
	private class GetDevUserListTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			IotUser user = new IotUser(Filter_history_Activity.this);
			mUserList.clear();
			mUserList = user.filter_historys(getIntent().getIntExtra("devid", 0));
			MyLog.i("", mUserList.toString());
			return user.checkState();
		}

		@Override
		protected void onPostExecute(Integer result) {
			Filter_history_adapter adapter = new Filter_history_adapter(getApplicationContext(), mUserList,personage_hua_listview_xulik);
			personage_hua_listview_xulik.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			if (mUserList.isEmpty()) {
			}
			if (result != IotUser.IOT_STATE_OK) {
				// 要重新登录
				Toast.makeText(Filter_history_Activity.this, "没有数据", Toast.LENGTH_LONG)
				.show();
			}
		}
	}
}
