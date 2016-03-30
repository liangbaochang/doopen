package cn.air.doopen.freagment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.air.doopen.adapter.FactlityActapter;
import cn.air.doopen.config.RadioName;
import cn.air.doopen.hy.R;
import cn.air.doopen.hy.dev.NewDevActivity;
import cn.air.doopen.hyapp.MyApp;
import cn.air.doopen.logic.BindingListener;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.socket.TcpCommSerivce;
import cn.air.doopen.utli.MyLog;
import cn.air.doopen.view.CustomProgressDialog;
import cn.air.doopen.wifi.esptouch.activity.EsptouchActivity;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.RentalsSunHeaderView;

/**
 * 设备列表操作,
 */
public class Facility_ft extends Fragment implements RadioName {
	public static final String TAG = "DevList:";
	public static final int GET_DEV_LIST_OK = 0;
	public static final int GET_DEV_LIST_FAIL = 1;
	public static final int USER_LOGIN_OK = 0;
	public static final int USER_LOGIN_FAIL = 0;
	public static final int NTID = 2;
	public NotificationManager nm;
	private ListView mListView;
	private FactlityActapter adapter;
	private Button ib1 = null;
	private TextView tv_msg = null;
	private TextView tv_newdev = null;
	private Handler handler = null;
	private List<Map<String, Object>> mDevList = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> mDevData = new ArrayList<Map<String, Object>>();
	private IotUser user;
	private TcpCommSerivce.MyBinder myBinder;
	private MyApp app;
	private DiscoveryTask discver;
	private Timer timer;
	private BindingListener bindingListener;
	private List<Map<String, Object>> tolistdev = new ArrayList<Map<String, Object>>();;
	private CustomProgressDialog dialog;
	private PtrFrameLayout mPtrFrame;
	public boolean mInDeveloginMode = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		// 进度条对话框；
		dialog = new CustomProgressDialog(getActivity());
		dialog.show();
		user = ((MyApp) getActivity().getApplication()).user;
		MyLog.i("onCreate", "执行了");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_dev_list, null);
		app = (MyApp) getActivity().getApplicationContext();
		initview(view);
		setlistener();
		setview();
		MyLog.i("onCreateView", "执行了");
		return view;
	}

	private void setview() {
		bindingListener = new BindingListener(getActivity());
		tolistdev = mDevList;
		if (!tolistdev.isEmpty()) {
			adapter = new FactlityActapter(tolistdev,1);
			mListView.setAdapter(adapter);
		}
	}

	private void initview(View view) {
		mPtrFrame = (PtrFrameLayout) view.findViewById(R.id.material_style_ptr_frame);
		mListView = (ListView) view.findViewById(R.id.id_listview);
		tv_msg = (TextView) view.findViewById(R.id.textView2);
		tv_newdev = (TextView) view.findViewById(R.id.textView3);
		ib1 = (Button) view.findViewById(R.id.faclity_add_btn);
		registerForContextMenu(mListView);
		// header
		RentalsSunHeaderView header = new RentalsSunHeaderView(getActivity());
		header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
		header.setPadding(0, LocalDisplay.dp2px(15), 0, LocalDisplay.dp2px(10));
		header.setUp(mPtrFrame);
		mPtrFrame.setLoadingMinTime(1000);
		mPtrFrame.setDurationToCloseHeader(1500);
		mPtrFrame.setHeaderView(header);
		mPtrFrame.addPtrUIHandler(header);
	}

	private void setlistener() {
		// 刷新；
		mPtrFrame.setPtrHandler(new PtrHandler() {
			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				frame.postDelayed(new Runnable() {
					@Override
					public void run() {
						MyLog.i(TAG, "刷新");
						mPtrFrame.refreshComplete();
						new GetDevListTask().execute();
						if (adapter != null) {
							adapter.notifyDataSetChanged();
						}
						// boolean state=WifiAdmin.isWiFiActive(getActivity());
						// if (state!=true) {
						// T.setTost(getActivity(), "请打开wifi");
						// }
						new DiscoveryTask().execute();
					}
				}, 10);
			}

			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
				// 默认实现，根据实际情况做改动
				return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
			}
		});
		tv_newdev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), NewDevActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		ib1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent jd_lb_cb=new Intent(getActivity(), EsptouchActivity.class);
				getActivity().startActivity(jd_lb_cb);
				//								new PopupWindowViewClass(getActivity()).showPopMenu(ib1);
			}
		});

		// 绑定列表点击事件
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				bindingListener.bindingonclick(mDevData, app, arg2);
			}
		});

	}

	@Override
	public void onDestroy() {
		MyLog.d(TAG, "窗口销毁");
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
		MyLog.i("onDestroy", "执行了");
	}

	@Override
	public void onPause() {
		super.onPause();
		MyApp myapp = (MyApp) getActivity().getApplication();
		myapp.isScanNewDev = false;
		if (timer != null) {
			timer.cancel();
		}
		MyLog.i("onPause", "执行了");
	}

	@Override
	public void onResume() {
		super.onResume();
		MyLog.i("onResume", "执行了");
		// 恢复登录
		Intent sendstartsevice = new Intent();
		sendstartsevice.setAction("startserivce");
		getActivity().sendBroadcast(sendstartsevice);
		// 启动服务；
		Intent intent = new Intent(getActivity(), TcpCommSerivce.class);
		this.getActivity().getApplicationContext().bindService(intent, connection, Service.BIND_AUTO_CREATE);
		MyApp myapp = (MyApp) getActivity().getApplication();
		myapp.isScanNewDev = true;
		// 查询设备列表；
		new GetDevListTask().execute();
		// 定时获取设备热点；
		timer = new Timer();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				discver = new DiscoveryTask();
				discver.execute();
				MyLog.i(TAG, "每6秒更新一次；");
			}
		};
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
				handler.sendEmptyMessage(0);
			}
		};
		timer.schedule(task, 3000, 6000);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		MyLog.i(TAG, "context item seleted ID=" + menuInfo.position);
		switch (item.getItemId()) {
		case R.id.item3:
			MyLog.i(TAG, "修改名称");
			final EditText inputName = new EditText(getActivity());
			inputName.setPadding(20, 10, 20, 10);
			inputName.setBackgroundResource(R.drawable.login_editbox);
			inputName.setHint("输入新的设备名称");
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setView(inputName).setNegativeButton("取消", null);

			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					MyLog.d(TAG, inputName.getText().toString());
					new AsyncTask<Void, Void, Void>() {
						@Override
						protected void onPostExecute(Void result) {
							super.onPostExecute(result);
							if (user.checkState() == IotUser.IOT_STATE_OK) {
								Toast.makeText(getActivity(), "修改成功!", Toast.LENGTH_LONG).show();
								new GetDevListTask().execute();
							} else {
								Toast.makeText(getActivity(), "修改失败!", Toast.LENGTH_LONG).show();
							}
						}

						@Override
						protected Void doInBackground(Void... params) {
							user.renameDev(inputName.getText().toString(),
									(Integer) (mDevData.get(menuInfo.position).get("id")));
							return null;
						}

					}.execute();
				}
			});
			builder.show();

			break;
		case R.id.item4:
			MyLog.i(TAG, "删除设备");
			new AsyncTask<Void, Void, Boolean>() {
				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					if (result) {
						if (user.checkState() == IotUser.IOT_STATE_OK) {
							adapter.notifyDataSetChanged();
						}
						new GetDevListTask().execute();
						Toast.makeText(getActivity(), "解绑成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getActivity(), "解绑失败", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				protected Boolean doInBackground(Void... params) {
					user.unbindDev((Integer) mDevData.get((int) menuInfo.position).get("id"));
					if (user.checkState() == IotUser.IOT_STATE_OK) {
						RefreshList();
						return true;
					} else {
						return false;
					}
				}
			}.execute();
			break;
		default:
			return super.onContextItemSelected(item);
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.dev_list_menu, menu);
	}

	final ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			myBinder = (TcpCommSerivce.MyBinder) service;
			if (myBinder.checkStatus() != TcpCommSerivce.STAT_ONLINE) {
				myBinder.login(new IotUser(getActivity()).getToken());
			}
		}
	};

	public class GetDevListTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			return RefreshList();
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result== IotUser.IOT_STATE_OK) {
				if (!mDevList.isEmpty()) {
					adapter = new FactlityActapter(mDevList,1);
					mListView.setAdapter(adapter);
					((MyApp) getActivity().getApplication()).isNeedRefreshDevList = false;
				}
			}else{

			}
			if (dialog != null) {
				dialog.dismiss();
			}
		}

	}

	/**
	 * 更新设备列表,只能运行在线程
	 */
	protected int RefreshList() {
		mDevData = user.getDevList();
		if (user.checkState() == IotUser.IOT_STATE_OK) {
			mDevList.clear();
			mDevList = BindingListener.getdata(mDevData);
			MyLog.i("Factlity mdevList", "a " + mDevList.toString());
		}
		return user.checkState();
	}

	// 发现周围wifi里符合特定格式的设备的异步任务
	class DiscoveryTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			MyApp myapp = (MyApp) getActivity().getApplication();
			if ((result != 0) && myapp.isScanNewDev) {
				tv_newdev.setText("发现新设备,点击查看");
				tv_newdev.setVisibility(View.VISIBLE);
			} else {
				tv_newdev.setVisibility(View.GONE);
			}
			// if (!myBinder.isConnected()) {
			// tv_msg.setText("网络不通,当前服务离线!");
			// tv_msg.setVisibility(View.VISIBLE);
			// } else {
			// tv_msg.setVisibility(View.GONE);
			// }
		}

		@Override
		protected Integer doInBackground(Void... params) {
			int count = bindingListener.search(user);
			return count;
		}
	}
}
