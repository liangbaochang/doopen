package cn.air.doopen.hy.share;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import cn.air.doopen.adapter.TheUserAdapter;
import cn.air.doopen.hy.R;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.view.mylistview.SlideListView;
import cn.air.doopen.view.mylistview.SlideListView.RemoveDirection;
import cn.air.doopen.view.mylistview.SlideListView.RemoveListener;
/**获取分享给别人后的用户信息；拥有删除分享用户功能；侧滑手势*/
public class ShareDevUserActivity extends Activity  implements SwipeRefreshLayout.OnRefreshListener{
	private List<Map<String, Object>> mUserList = new ArrayList<Map<String, Object>>();
	private SlideListView mListView;
	private TheUserAdapter adapter;
	private SwipeRefreshLayout mSwipeLayout;
	private Button devshare_result_user;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_dev_user);
		new GetDevUserListTask().execute();
		devshare_result_user = (Button) findViewById(R.id.devshare_result_user);
		mListView = (SlideListView) findViewById(R.id.id_listview_activity_share_dev_user);
		mSwipeLayout = (SwipeRefreshLayout)findViewById(R.id.id_swipe_ly);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeColors(Color.BLUE);
		setlistener();
	}
	
	@Override
	public void onRefresh() {
		new GetDevUserListTask().execute();
	}

	private class GetDevUserListTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			IotUser user = new IotUser(ShareDevUserActivity.this);
			mUserList.clear();
			mUserList = user.getDevUserList(getIntent().getIntExtra("devid", 0));
			return user.checkState();
		}

		@Override
		protected void onPostExecute(Integer result) {
			adapter = new TheUserAdapter(getApplicationContext(), mUserList);
			mListView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			mSwipeLayout.setRefreshing(false);
			if (mUserList.isEmpty()) {
				Toast.makeText(ShareDevUserActivity.this, "没有用户", Toast.LENGTH_SHORT)
				.show();
			}
		}

	}

	private void setlistener() {
		devshare_result_user.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		mListView.setRemoveListener(new RemoveListener() {
			@Override
			public void removeItem(RemoveDirection direction, final int position) {
				// 弹出对话框
				new AsyncTask<Void, Void, Integer>(){
					@Override
					protected void onPostExecute(Integer result) {
						super.onPostExecute(result);
						if(result == IotUser.IOT_STATE_OK){
							Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_LONG).show();
						}else{
							Toast.makeText(getApplicationContext(), "删除失败", Toast.LENGTH_LONG).show();
						}
					}
					IotUser user = new IotUser(getApplicationContext());
					@Override
					protected Integer doInBackground(Void... params) {
						user.unshareDev((Integer) mUserList.get(position).get("id"), getIntent().getIntExtra("devid", 0));
						new GetDevUserListTask().execute(null, null, null);
						return user.checkState();
					}
				}.execute();
			}
		});
	}
}
