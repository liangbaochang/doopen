package cn.air.doopen.adapter;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.air.doopen.hy.R;
import cn.air.doopen.logic.asyTask.VoteAsyTask;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.utli.MyLog;
import cn.air.doopen.view.mylistview.SlideListView2;
import net.tsz.afinal.FinalBitmap;
/**分享消息列表的适配*/
public class User_Message_Adapter  extends BaseAdapter{

	private ViewHolder handler;
	public Context context;
	public List<Map<String, Object>> msgs;
	private FinalBitmap fb;
	private int VOREVALUE=0;
	SlideListView2 personage_hua_listview;
	public User_Message_Adapter(Context context ,List<Map<String, Object>> msgs,SlideListView2 personage_hua_listview) {
		this.context=context;
		this.msgs=msgs;
		this.personage_hua_listview=personage_hua_listview;
		//图片显示初始化；
		fb = FinalBitmap.create(context);//
	}
	@Override
	public int getCount() {
		return msgs.size();
	}

	@Override
	public Object getItem(int arg0) {
		return msgs.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			handler = new ViewHolder();
			LayoutInflater info = LayoutInflater.from(context);
			convertView = info.inflate(R.layout.persongage_msg_listview,
					null);
			handler.factlity_adapter_zt_msg = (TextView) convertView
					.findViewById(R.id.factlity_adapter_zt_msg);
			handler.factlity_adapter_state = (TextView) convertView
					.findViewById(R.id.factlity_adapter_state_msg);
			handler.fact_adapter_time = (TextView) convertView
					.findViewById(R.id.fact_adapter_time);
			handler.consent = (TextView) convertView.findViewById(R.id.consent);
			handler.refuse = (TextView) convertView.findViewById(R.id.refuse);
			handler.factlity_adapter_name = (TextView) convertView
					.findViewById(R.id.factlity_adapter_name_msg);
			handler.fact_adapter_iamge = (ImageView) convertView
					.findViewById(R.id.fact_adapter_iamge_msg);
			handler.dev_msg_agree = (RelativeLayout) convertView
					.findViewById(R.id.dev_msg_agree);
			handler.dev_msg_refuse = (RelativeLayout) convertView
					.findViewById(R.id.dev_msg_refuse);
			handler.dev_msg_refuse_delete = (RelativeLayout) convertView
					.findViewById(R.id.dev_msg_refuse_delete);
			handler.msg_relatlvelayout = (GridLayout) convertView
					.findViewById(R.id.msg_relatlvelayout);
			handler.llayout_right = (LinearLayout) convertView
					.findViewById(R.id.llayout_right);
			handler.msg_delte_lien = (LinearLayout) convertView
					.findViewById(R.id.msg_delte_lien);

			convertView.setTag(handler);
		} else {
			handler = (ViewHolder) convertView.getTag();
		}
		final Map<String, Object> msg = msgs.get(position);
		MyLog.i("", msg);
		//		状态：0新消息1已查阅或提取2已答复    ----status
		int status=(Integer) msg.get("status");
		//		应答状态0无，1通过2拒绝          ---------answer
		int answer=(Integer) msg.get("answer");
		handler.factlity_adapter_name.setText(""+(String)msg.get("modelname"));
		handler.fact_adapter_time.setText(""+msg.get("time"));
		switch (status) {
		case 0:
			handler.factlity_adapter_state.setText(""+msg.get("content"));
			handler.factlity_adapter_zt_msg.setText("点击同意");
			handler.msg_relatlvelayout.setVisibility(View.VISIBLE);
			handler.dev_msg_refuse.setVisibility(View.VISIBLE);
			handler.dev_msg_agree.setVisibility(View.VISIBLE);
			handler.llayout_right.setVisibility(View.VISIBLE);
			break;
		case 1:
			handler.factlity_adapter_state.setText(""+msg.get("content"));
			break;
		case 2:
			switch (answer) {
			case 1:
				handler.refuse.setText("已同意");
				handler.factlity_adapter_zt_msg.setText("已同意");
				handler.factlity_adapter_state.setText(""+msg.get("content"));
				handler.dev_msg_refuse.setVisibility(View.GONE);
				handler.dev_msg_agree.setFocusable(false);
				handler.dev_msg_agree.setBackgroundResource(R.color.white);
				break;
			case 2:
				handler.refuse.setText("已拒绝");
				handler.factlity_adapter_zt_msg.setText("已拒绝");
				handler.factlity_adapter_state.setText(""+msg.get("content"));
				handler.dev_msg_refuse.setVisibility(View.GONE);
				handler.dev_msg_agree.setFocusable(false);
				handler.dev_msg_agree.setBackgroundResource(R.color.white);
				break;
			}
			break;
		}
		//		if (fact.getImgurl()!=null) {
		//			fb.display(handler.fact_adapter_iamge,fact.getImgurl());
		//		}
		//同意，拒绝，删除；
		handler.dev_msg_agree.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				VOREVALUE=1;
				VoteAsyTask asyTask=new VoteAsyTask(context);
				asyTask.voretask((Integer)msgs.get(position).get("id"),VOREVALUE);
				personage_hua_listview.slideBack();
				new msgasytask().execute();
			}
		});
		handler.dev_msg_refuse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				VOREVALUE=2;
				VoteAsyTask asyTask=new VoteAsyTask(context);
				asyTask.voretask((Integer)msgs.get(position).get("id"),VOREVALUE);
				personage_hua_listview.slideBack();
				new msgasytask().execute();
			}
		});
		handler.dev_msg_refuse_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				VoteAsyTask asyTask=new VoteAsyTask(context);
				asyTask.deleteshareMsg((Integer)msgs.get(position).get("id"));
				personage_hua_listview.slideBack();
				new msgasytask().execute();
			}
		});
		return convertView;
	}

	public static class ViewHolder {
		ImageView fact_adapter_iamge;
		TextView factlity_adapter_name,fact_adapter_time,consent,refuse;
		GridLayout  msg_relatlvelayout;
		RelativeLayout dev_msg_agree,dev_msg_refuse,dev_msg_refuse_delete;
		LinearLayout llayout_right,msg_delte_lien;
		TextView factlity_adapter_state,factlity_adapter_zt_msg;
	}

	class msgasytask extends AsyncTask< Void, Void, Void>{
		@Override
		protected Void doInBackground(Void... arg0) {
			msgs.clear();
			IotUser user = new IotUser(context);
			msgs = user.getShareMessageList();
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (msgs!=null) {
				User_Message_Adapter mAdapter = new User_Message_Adapter(context,msgs, personage_hua_listview);
				personage_hua_listview.setAdapter(mAdapter);
				notifyDataSetChanged();
			}
		}
	}
}
