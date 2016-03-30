package cn.air.doopen.adapter;

import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import cn.air.doopen.hy.R;
import cn.air.doopen.hyapp.MyApp;
/**设备列表适配器*/
public class FactlityActapter  extends BaseAdapter {

	private List<Map<String, Object>> mDevList;
	int currentPosition;
	int state;
	public FactlityActapter( List<Map<String, Object>> mDevList,int state) {
		this.mDevList=mDevList;
		this.state=state;
	}

	@Override
	public int getCount() {
		return mDevList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	ViewHolder holder = null;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater mInflater = LayoutInflater.from(MyApp.getContext());
			convertView = mInflater.inflate(R.layout.new_dev_item, null);
			holder.headImg = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.wifiImg = (ImageView) convertView.findViewById(R.id.imageView2);
			holder.title = (TextView) convertView.findViewById(R.id.textView1);
			holder.info = (TextView) convertView.findViewById(R.id.textView2);
			holder.item_open = (LinearLayout) convertView.findViewById(R.id.item_open);
			holder.item_move = (LinearLayout) convertView.findViewById(R.id.item_move);
			holder.item_delete = (LinearLayout) convertView.findViewById(R.id.item_delete);
			holder.layout_other = (LinearLayout) convertView.findViewById(R.id.layout_other);
			holder.factlity_seting_img = (ToggleButton) convertView.findViewById(R.id.factlity_seting_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (!mDevList.isEmpty()) {
			holder.headImg.setImageResource((Integer) mDevList.get(position).get("dev_head_img"));
			holder.title.setText((String) mDevList.get(position).get("dev_name"));
			if (state==1) {
				holder.wifiImg.setImageResource((Integer) mDevList.get(position).get("dev_wifi_img"));
				holder.info.setText((String) mDevList.get(position).get("dev_state"));
				if ((Integer) mDevList.get(position).get("dev_wifi_img") == R.drawable.wifi_active) {
					AnimationDrawable animationDrawable = (AnimationDrawable) holder.wifiImg.getDrawable();
					animationDrawable.start();
				}
			}else{
				holder.info.setText((String) mDevList.get(position).get("ver"));
			}
	
		}
		//这边没有用上；
		holder.factlity_seting_img.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean check) {
				if (check==true) {
					if (position == currentPosition) {
						holder.layout_other.setVisibility(View.VISIBLE);
						holder.item_open.setClickable(true);
						holder.item_move.setClickable(true);
						holder.item_delete.setClickable(true);
						holder.item_open.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								currentPosition = -1;
							}
						});
						holder.item_move.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								currentPosition = -1;
							}
						});
						holder.item_delete.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								AlertDialog.Builder builder = new AlertDialog.Builder(MyApp.getContext());
								builder.setIcon(android.R.drawable.ic_dialog_info);
								builder.setTitle("确定删除吗?");
								builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
									}
								});
								builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {

									}
								});
								builder.create().show();
							}
						});
					} else {
						holder.layout_other.setVisibility(View.GONE);
						holder.item_open.setClickable(false);
						holder.item_move.setClickable(false);
						holder.item_delete.setClickable(false);
					}
				}else{
					holder.layout_other.setVisibility(View.GONE);
					holder.item_open.setClickable(false);
					holder.item_move.setClickable(false);
					holder.item_delete.setClickable(false);
				}
			}
		});
		return convertView;

	}

	private class ViewHolder {
		ImageView headImg;
		ToggleButton factlity_seting_img;
		ImageView wifiImg;
		TextView title;
		TextView info;
		LinearLayout item_open,item_move,item_delete,layout_other;
	}

}
