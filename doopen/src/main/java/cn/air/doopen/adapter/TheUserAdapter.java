package cn.air.doopen.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.air.doopen.hy.R;
/**被分享用户的列表适配*/
public class TheUserAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	List<Map<String, Object>> mUserList;
	public TheUserAdapter(Context context,List<Map<String, Object>> mUserList) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
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
	public View getView(final int position, View convertView,
			ViewGroup parent) {

		ViewHolder holder = null;

		if (convertView == null) {

			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.dev_user_item, null);
			holder.name = (TextView) convertView
					.findViewById(R.id.textView1);
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText((String) mUserList.get(position)
				.get("name"));
		// 列表淡入动画
		convertView.startAnimation(AnimationUtils.loadAnimation(
				context, R.anim.fade_in));
		return convertView;

	}
	private class ViewHolder {
		TextView name;
	}
}
