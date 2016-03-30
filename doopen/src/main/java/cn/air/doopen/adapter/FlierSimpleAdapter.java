package cn.air.doopen.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.air.doopen.hy.R;
/**水立方滤芯列表的显示适配；*/
public class FlierSimpleAdapter extends BaseAdapter {
	private ViewHolder handler;
	public Context context;
	public List<Map<String, Object>> flier;
	public FlierSimpleAdapter(Context context ,	List<Map<String, Object>> flier) {
		this.context=context;
		this.flier=flier;
	}
	@Override
	public int getCount() {
		return flier.size();
	}

	@Override
	public Object getItem(int arg0) {
		return flier.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		if (convertView == null) {
			handler = new ViewHolder();
			LayoutInflater info = LayoutInflater.from(context);
			convertView = info.inflate(R.layout.cto_spinner_layout,
					null);
			handler.fliter_txt = (TextView) convertView
					.findViewById(R.id.fliter_txt);
			handler.Filter_values = (ProgressBar) convertView
					.findViewById(R.id.Filter_values);
			convertView.setTag(handler);
		} else {
			handler = (ViewHolder) convertView.getTag();
		}
		handler.fliter_txt.setText((CharSequence) flier.get(position).get("name"));
		handler.Filter_values.setMax((Integer) flier.get(position).get("SetV"));
		handler.Filter_values.setProgress((Integer) flier.get(position).get("CntV"));
		return convertView;
	}
	public static class ViewHolder {
		TextView fliter_txt;
		ProgressBar Filter_values;
	}
}
