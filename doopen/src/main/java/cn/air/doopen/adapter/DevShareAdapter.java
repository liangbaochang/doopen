package cn.air.doopen.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.air.doopen.hy.R;
import cn.air.doopen.hy.share.ShareDevActivity;
import cn.air.doopen.hy.share.ShareDevUserActivity;
import cn.air.doopen.utli.MyLog;
import cn.air.doopen.view.mylistview.SlideListView2;
/**分享管理界面的适配器；*/
public class DevShareAdapter  extends BaseAdapter{

	private Context context;
	private List<Map<String, Object>> mDevList;
	SlideListView2 personage_hua_listview;
	public DevShareAdapter(Context context, List<Map<String, Object>> mDevList ,SlideListView2 personage_hua_listview) {
		this.context = context;
		this.mDevList=mDevList;
		this.personage_hua_listview=personage_hua_listview;
	}

	@Override
	public int getCount() {
		return mDevList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mDevList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater mInflater = LayoutInflater.from(context);
			convertView = mInflater.inflate(R.layout.dev_share_list_layout, null);
			holder.headImg = (ImageView) convertView.findViewById(R.id.dev_share_item_img);
			holder.title = (TextView) convertView.findViewById(R.id.dev_share_item_name);
			holder.info = (TextView) convertView.findViewById(R.id.dev_share_item_state);
			holder.dev_msg_agree = (RelativeLayout) convertView
					.findViewById(R.id.dev_msg_agree_dev_share);
			holder.dev_msg_refuse = (RelativeLayout) convertView
					.findViewById(R.id.dev_msg_refuse_dev_share);
			holder.llayout_right = (LinearLayout) convertView
					.findViewById(R.id.llayout_right_dev_share);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (mDevList!=null) {
			//显示数据；
			holder.headImg.setImageResource((Integer) mDevList.get(position).get("dev_head_img"));
			holder.title.setText((String) mDevList.get(position).get("dev_name"));
			holder.info.setText((String) mDevList.get(position).get("dev_state"));
		}
		holder.dev_msg_agree.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//传值跳转到分享用户管理界面；
				Intent intent1 = new Intent(context, ShareDevUserActivity.class);
				intent1.putExtra("devid", (Integer) (mDevList.get(position).get("id")));
				intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent1);
				personage_hua_listview.slideBack();
			}
		});
		holder.dev_msg_refuse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//传值跳转到分享界面；
				Intent intent = new Intent(context, ShareDevActivity.class);
				intent.putExtra("devkey", (String) (mDevList.get(position).get("devkey")));
				intent.putExtra("devid", (Integer) (mDevList.get(position).get("id")));
				intent.putExtra("owner", (String) (mDevList.get(position).get("owner")));
				MyLog.i("", "owner="+(String) (mDevList.get(position).get("owner")));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				personage_hua_listview.slideBack();
			}
		});
		return convertView;
	}

	private class ViewHolder {
		ImageView headImg;
		TextView title;
		TextView info;
		RelativeLayout dev_msg_agree,dev_msg_refuse;
		LinearLayout llayout_right;
	}
}
