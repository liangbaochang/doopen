package cn.air.doopen.hy.share;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.air.doopen.adapter.User_Message_Adapter;
import cn.air.doopen.hy.R;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.utli.MyLog;
import cn.air.doopen.view.mylistview.SlideListView2;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.RentalsSunHeaderView;
/**分享消息列表；*/
public class UserMessageActivity extends Activity{
	public  String TAG="UserMessageActivity";
	private Button personage_msg_delete;
	private SlideListView2 personage_hua_listview;
	private IotUser user;
	private List<Map<String, Object>> mDevData = new ArrayList<Map<String, Object>>();
	private User_Message_Adapter mAdapter;
	private Button devshare_result_msg;
	private PtrFrameLayout mPtrFrame;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_message);
		initview();
		setlistener();
	}

	private void setlistener() {
		devshare_result_msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();				
			}
		});		
		personage_msg_delete.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showPopMenu();
			}
		});
		personage_hua_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				personage_hua_listview.scrollLeft();
			}
		});
	}

	private void initview() {
		devshare_result_msg=(Button)findViewById(R.id.devshare_result_msg);
		personage_msg_delete=(Button)findViewById(R.id.personage_msg_delete);
		personage_hua_listview=(SlideListView2)findViewById(R.id.personage_hua_listview);
		mPtrFrame = (PtrFrameLayout)findViewById(R.id.material_style_ptr_frame_usermsg);
		personage_hua_listview.initSlideMode(SlideListView2.MOD_RIGHT);
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
				setview();
				mPtrFrame.refreshComplete();
				mPtrFrame.autoRefresh(true);
			}
		}, 1000);
		mPtrFrame.setPtrHandler(new PtrHandler() {
			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				frame.postDelayed(new Runnable() {
					@Override
					public void run() {
						MyLog.i(TAG, "刷新");
						setview();
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
	}
	public void setview() {
		new msgasytask().execute(null,null,null);
	}
	class msgasytask extends AsyncTask< Void, Void, Void>{
		@Override
		protected Void doInBackground(Void... arg0) {
			mDevData.clear();
			user=new IotUser(UserMessageActivity.this);
			mDevData = user.getShareMessageList();
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (mDevData!=null) {
				mAdapter = new User_Message_Adapter(getApplicationContext(),mDevData, personage_hua_listview);
				personage_hua_listview.setAdapter(mAdapter);
			}
		}
	}
	//删除所有消息。
	static PopupWindow mpopupWindow ;
	public  void showPopMenu() {
		View view = View.inflate(getApplicationContext(), R.layout.activity_delete_dialogs, null);
		Button delete_msg_true = (Button) view.findViewById(R.id.delete_msg_true);
		Button delete_msg_false = (Button) view.findViewById(R.id.delete_msg_false);
		RelativeLayout activity_delete_dialogs_RelativeLayout = (RelativeLayout) view.findViewById(R.id.activity_delete_dialogs_RelativeLayout);
		delete_msg_true.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new deleteshareMsg().execute(null,null,null);
				setview();
				backgroundAlpha(1f);
				mpopupWindow.dismiss();
			}
		});
		delete_msg_false.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				backgroundAlpha(1f);
				mpopupWindow.dismiss();
			}
		});
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mpopupWindow.dismiss();
				backgroundAlpha(1f);
			}
		});

		view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
		activity_delete_dialogs_RelativeLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_bottom_in));
		if(mpopupWindow==null){
			mpopupWindow = new PopupWindow(getApplicationContext());
			mpopupWindow.setWidth(LayoutParams.MATCH_PARENT);
			mpopupWindow.setHeight(LayoutParams.MATCH_PARENT);
			mpopupWindow.setBackgroundDrawable(new BitmapDrawable());
			mpopupWindow.setFocusable(true);
			mpopupWindow.setOutsideTouchable(true);
		}
		mpopupWindow.setContentView(view);
		mpopupWindow.showAtLocation(personage_msg_delete, Gravity.BOTTOM, 0, 0);
		mpopupWindow.update();
		backgroundAlpha(0.5f);
	}
	private int code;
	class deleteshareMsg extends AsyncTask< Void, Void, Void>{
		@Override
		protected Void doInBackground(Void... arg0) {
			IotUser iotUser=new IotUser(getApplicationContext());
			code=iotUser.deleteDevmsg();
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (code==0) {
				Toast.makeText(getApplicationContext(), "清空消息成功", 2).show();
			}else{
				Toast.makeText(getApplicationContext(), "无法清空", 2).show();
			}
		}
	}
	/** 
	 * 设置添加屏幕的背景透明度 
	 * @param bgAlpha 
	 */  
	public void backgroundAlpha(float bgAlpha)  
	{  
		WindowManager.LayoutParams lp = getWindow().getAttributes();  
		lp.alpha=bgAlpha;
		getWindow().setAttributes(lp);  
	}  
}
