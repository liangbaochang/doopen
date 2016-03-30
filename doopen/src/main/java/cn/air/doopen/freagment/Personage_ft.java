package cn.air.doopen.freagment;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.air.doopen.freagment.airA.Repair_List_Activity;
import cn.air.doopen.hy.R;
import cn.air.doopen.hy.personage.AboutActivity;
import cn.air.doopen.hy.personage.FeedbackActivity;
import cn.air.doopen.hy.personage.SettingActivity;
import cn.air.doopen.hy.personage.UserCenterActivity;
import cn.air.doopen.hy.share.DevShareActivity;
import cn.air.doopen.hy.share.UserMessageActivity;
import cn.air.doopen.hyapp.MyApp;
import cn.air.doopen.logic.ControlToOfficialOrTestServer;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.utli.MyLog;
import cn.air.doopen.widget.RoundImageView;
import net.tsz.afinal.FinalBitmap;
/**个人中心界面；*/
public class Personage_ft extends Fragment implements OnClickListener {

	private LinearLayout persongae_invite_folk, personage_share, personage_msg_sentre, gw_msg, personage_tickling,personage_us;
	private RoundImageView personage_personal_details;
	private TextView dev_msg_size;
	private LinearLayout personage_update;
	private ProgressBar pb1;
	private LinearLayout gw_seting;
	private TextView dev_share_size;
	private TextView username_phone;
	private int msglen=-1;
	private ImageView hint_image;
	private int devlen=-1;
	private List<Map<String, Object>> muserlist = new ArrayList<Map<String, Object>>();
	private Long kong=-1L;
	private String app_msg[];
	private Handler handlerupdate = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				hint_image.setVisibility(View.VISIBLE);
				final TextView tv = new TextView(getActivity());
				if (app_msg.equals("")) {
					tv.setText("下载新版本");
				}else{
					tv.setText(app_msg[0]);
				}
				tv.setTextSize(20);
				tv.setPadding(20, 30, 20, 30);
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("发现新版本").setIcon(android.R.drawable.ic_dialog_alert).setView(tv)
						.setNegativeButton("取消", null);
				builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						hint_image.setVisibility(View.GONE);
						IotUser user=new IotUser(getActivity());
						Uri appuri=user.udpatgeappurl(Integer.parseInt(app_msg[1]));
						Intent downloadIntent = new Intent(Intent.ACTION_VIEW, appuri);
						startActivity(downloadIntent);
					}
				});
				builder.show();

			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyLog.i("onCreate执行了");
		new usernamephone().execute();
		new updateapp().execute();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_personage, null);
		MyLog.i("onCreateView执行了");
		// 初始化界面；
		initview(view);
		// 监听事件；
		listenerview();
		return view;
	}
	private void listenerview() {
		personage_personal_details.setOnClickListener(this);
		persongae_invite_folk.setOnClickListener(this);
		personage_share.setOnClickListener(this);
		personage_msg_sentre.setOnClickListener(this);
		gw_msg.setOnClickListener(this);
		personage_tickling.setOnClickListener(this);
		personage_update.setOnClickListener(this);
		personage_us.setOnClickListener(this);
		gw_seting.setOnClickListener(this);
	}

	private void initview(View v) {
		personage_personal_details = (RoundImageView) v.findViewById(R.id.personage_personal_details);
		dev_msg_size = (TextView) v.findViewById(R.id.dev_msg_size);
		dev_share_size = (TextView) v.findViewById(R.id.dev_share_size);
		username_phone = (TextView) v.findViewById(R.id.username_phone);
		persongae_invite_folk = (LinearLayout) v.findViewById(R.id.persongae_invite_folk);
		personage_share = (LinearLayout) v.findViewById(R.id.personage_share);
		personage_msg_sentre = (LinearLayout) v.findViewById(R.id.personage_msg_sentre);
		gw_msg = (LinearLayout) v.findViewById(R.id.gw_msg);
		personage_tickling = (LinearLayout) v.findViewById(R.id.personage_tickling);
		personage_us = (LinearLayout) v.findViewById(R.id.personage_us);
		personage_update = (LinearLayout) v.findViewById(R.id.personage_update);
		gw_seting = (LinearLayout) v.findViewById(R.id.gw_seting);
		pb1 = (ProgressBar) v.findViewById(R.id.progressBar1);
		hint_image = (ImageView) v.findViewById(R.id.hint_image);
	}
	@Override
	public void onResume() {
		super.onResume();
		MyLog.i("onResume执行了");
		new devsharetask().execute();
		new msgasytasksize().execute();
		//下载显示头像；
		FinalBitmap fb = FinalBitmap.create(getActivity());//
		String imgurl=new ControlToOfficialOrTestServer().Avatar_Downloader_url(new IotUser(getActivity()).getToken());
		MyLog.i(imgurl);
		fb.display(personage_personal_details,imgurl);
		if (devlen!=-1) {
			dev_share_size.setText(""+devlen);
		}
		if (msglen!=-1) {
			dev_msg_size.setText(""+msglen);
		}
		if (kong!=-1) {
			username_phone.setText(""+kong);
		}
	}
	@Override
	public void onStart() {
		super.onStart();
		MyLog.i("onStart执行了");
	}
	@Override
	public void onClick(View v) {
		Intent intit = new Intent();
		switch (v.getId()) {
			case R.id.personage_personal_details:
				Intent us = new Intent(getActivity(), UserCenterActivity.class);
				startActivity(us);
				break;
			case R.id.persongae_invite_folk:
				Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
				break;
			case R.id.personage_share:
				Intent intent1 = new Intent(getActivity(), DevShareActivity.class);
				startActivity(intent1);
				break;
			case R.id.personage_msg_sentre:// 消息中心；
				intit.setClass(getActivity(), UserMessageActivity.class);
				startActivity(intit);
				break;
			case R.id.gw_msg://保修
				Intent Repair = new Intent(getActivity(), Repair_List_Activity.class);
				Repair.putExtra("isshow", false);
				startActivity(Repair);
				break;
			case R.id.personage_tickling: // 转到反馈输入窗口
				Intent Feedb = new Intent(getActivity(), FeedbackActivity.class);
				startActivity(Feedb);
				break;
			case R.id.personage_us: // 转到关于窗口
				Intent aboutintent = new Intent(getActivity(), AboutActivity.class);
				startActivity(aboutintent);
				break;
			case R.id.personage_update: // 检测更新
				//	UpdateTask.personage_ft(getActivity(), pb1);
				new updateapp().execute();
				break;
			case R.id.gw_seting:// 转到设置窗口
				Intent seeting = new Intent(getActivity(), SettingActivity.class);
				startActivity(seeting);
				break;

		}
	}
	class msgasytasksize extends AsyncTask< Integer, Void, Integer>{
		private IotUser user;
		private List<Map<String, Object>> mDevData=new ArrayList<Map<String,Object>>();
		@Override
		protected Integer doInBackground(Integer... arg0) {
			user=new IotUser(getActivity());
			mDevData = user.getShareMessageList();
			msglen=mDevData.size();
			return null;
		}
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (msglen!=-1) {
				dev_msg_size.setText(""+msglen);
			}
		}
	}
	class devsharetask extends AsyncTask< Void, Void, Void>{
		private IotUser user;
		private List<Map<String, Object>> devsharelist=new ArrayList<Map<String,Object>>();
		@Override
		protected Void doInBackground(Void... arg0) {
			user=new IotUser(getActivity());
			devsharelist =user.getDevList();
			devlen=devsharelist.size();
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (devlen!=-1) {
				dev_share_size.setText(""+devlen);
			}
		}
	}
	//获取用户phone；
	class usernamephone extends AsyncTask<Void, Void, Void>{
		@Override
		protected Void doInBackground(Void... arg0) {
			IotUser user=new IotUser(getActivity());
			muserlist=user.getuserMsg();
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (muserlist!=null) {
				kong=(Long) muserlist.get(0).get("phone");
				if (kong!=-1) {
					username_phone.setText(""+kong);
				}
			}
		}
	}
	class updateapp extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			MyApp myapp = (MyApp)getActivity().getApplication();
			// 检测新公开的版本
			app_msg=myapp.user.checkAppUpgrade();
			if (myapp.user.checkState() == IotUser.IOT_STATE_OK){
				handlerupdate.sendEmptyMessage(0x123);
			}
			return null;
		}
	};
}