package cn.air.doopen.hy.device_control.xulik;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.air.doopen.hy.R;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.view.mylistview.SlideListView2;
/**滤芯复位历史列表适配*/
public class Filter_history_adapter extends BaseAdapter{
	Context context;
	private LayoutInflater mInflater;
	List<Map<String, Object>> mUserList;
	SlideListView2 personage_hua_listview;
	public Filter_history_adapter(Context context,List<Map<String, Object>> mUserList,SlideListView2 personage_hua_listview) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.personage_hua_listview=personage_hua_listview;
		this.mUserList=mUserList;
	}

	@Override
	public int getCount() {
		return mUserList.size();
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
	public View getView(final int position, View convertView,ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.xulik_filter_history, null);
			holder.filter_name = (TextView) convertView
					.findViewById(R.id.filter_name);
			holder.filter_id = (TextView) convertView
					.findViewById(R.id.filter_id);
			holder.filter_time = (TextView) convertView
					.findViewById(R.id.filter_time);
			holder.dev_msg_refuse_air = (RelativeLayout) convertView
					.findViewById(R.id.dev_msg_refuse_air);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.filter_id.setText(""+(Integer)mUserList.get(position).get("filter_id"));
		holder.filter_name.setText((String) mUserList.get(position).get("filter_name"));
		holder.filter_time.setText((String) mUserList.get(position).get("filter_time"));
		holder.dev_msg_refuse_air.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				new voreTask().execute((Integer)mUserList.get(position).get("filter_id"),(Integer)mUserList.get(position).get("devid"));
				personage_hua_listview.slideBack();
				new msgasytask().execute((Integer)mUserList.get(position).get("devid"));
			}
		});
		return convertView;
	}

	private class ViewHolder {
		TextView filter_id;
		TextView filter_name;
		TextView filter_time;
		RelativeLayout dev_msg_refuse_air;
	}
	private int msg=-1;
	class voreTask extends AsyncTask<Integer,Integer,Integer>{
		//后面尖括号内分别是参数（例子里是线程休息时间），进度(publishProgress用到)，返回值 类型
		protected Integer doInBackground(Integer... params) {
			IotUser iotUser=new IotUser(context);
			 msg=iotUser.delsete_id_filter_history(params[0], params[1]);
			return msg;
		}
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (msg==0) {
				Toast.makeText(context, "已删除", 1).show();
			}else{
				Toast.makeText(context, "未删除", 1).show();
			}
		}
	}
	class msgasytask extends AsyncTask< Integer, Void, Void>{
		@Override
		protected Void doInBackground(Integer... arg0) {
			mUserList.clear();
			IotUser user = new IotUser(context);
			mUserList = user.filter_historys(arg0[0]);
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (mUserList!=null) {
				Filter_history_adapter mAdapter = new Filter_history_adapter(context,mUserList, personage_hua_listview);
				personage_hua_listview.setAdapter(mAdapter);
				notifyDataSetChanged();
			}
		}
	}
}
