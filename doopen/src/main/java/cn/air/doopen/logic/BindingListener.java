package cn.air.doopen.logic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.Toast;
import cn.air.doopen.config.Deploy;
import cn.air.doopen.freagment.airA.AirAActivity;
import cn.air.doopen.hy.R;
import cn.air.doopen.hy.device_control.DemonstrateActivity;
import cn.air.doopen.hyapp.MyApp;
import cn.air.doopen.logic.Abstractobj.Exception_control_abstractclass;
import cn.air.doopen.logic.Abstractobj.Dev_abstr_excption_cont.Haiyi_air_abst;
import cn.air.doopen.logic.Abstractobj.Dev_abstr_excption_cont.Xulik_Abst;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.socket.TcpCommSerivce.MyBinder;
import cn.air.doopen.utli.MyLog;
import cn.air.doopen.wifi.WifiAdmin;

/**设备列表管理类；根据设备类型点击进入不同的控制界面。*/
public class BindingListener implements Deploy{

	static Context context;
	public BindingListener(Context context) {
		BindingListener.context=context;
	}
	/**根据设备类型点击进入不同的控制界面*/
	public void bindingonclick(List<Map<String, Object>> mDevData,MyApp app,int arg2){
		if (!mDevData.isEmpty()) {
			if (Integer.parseInt(mDevData.get(arg2).get("client_id").toString()) != 0) {
				if (mDevData.get(arg2).get("type").toString().equals(water_dev_type)) {
					Intent intent = new ControlToOfficialOrTestServer().controldevtype_water();
					intent.putExtra("clientID", (Integer) mDevData.get(arg2).get("client_id"));
					intent.putExtra("id_devid", (Integer) (mDevData.get(arg2).get("id")));
					intent.putExtra("devkey",  (String) (mDevData.get(arg2).get("devkey")));
					app.setFirmwareID((String)mDevData.get(arg2).get("ver"));
					app.setDevid((Integer) (mDevData.get(arg2).get("id")));
					app.setDevtype((String) (mDevData.get(arg2).get("type").toString()));
					//实例化SharedPreferences对象（第一步）
					SharedPreferences mySharedPreferences= context.getSharedPreferences("dev_msg",
							Activity.MODE_PRIVATE);
					//实例化SharedPreferences.Editor对象（第二步）
					SharedPreferences.Editor editor = mySharedPreferences.edit();
					//用putString的方法保存数据
					editor.putInt("on_clientID_sh",  (Integer) mDevData.get(arg2).get("client_id"));
					//提交当前数据
					editor.commit(); 
					context.startActivity(intent);
				} else if (mDevData.get(arg2).get("type").toString().equals(demo_dev_type)) {
					Intent intent = new Intent(context, DemonstrateActivity.class);
					intent.putExtra("clientID", (Integer) mDevData.get(arg2).get("client_id"));
					intent.putExtra("id_devid", (Integer) (mDevData.get(arg2).get("id")));
					intent.putExtra("devid",  (String) (mDevData.get(arg2).get("devkey")));
					app.setFirmwareID((String)mDevData.get(arg2).get("ver"));
					app.setDevid((Integer) (mDevData.get(arg2).get("id")));
					app.setDevtype((String) (mDevData.get(arg2).get("type").toString()));
					context.startActivity(intent);
				} else if (mDevData.get(arg2).get("type").toString().equals(air_dev_type)) {
					Intent intent = new Intent(context, AirAActivity.class);
					intent.putExtra("clientID", (Integer) mDevData.get(arg2).get("client_id"));
					intent.putExtra("id_devid", (Integer) (mDevData.get(arg2).get("id")));
					intent.putExtra("devid",  (String) (mDevData.get(arg2).get("devkey")));
					app.setFirmwareID((String)mDevData.get(arg2).get("ver"));
					app.setDevid((Integer) (mDevData.get(arg2).get("id")));
					app.setDevtype((String) (mDevData.get(arg2).get("type").toString()));
					context.startActivity(intent);
				} else if (mDevData.get(arg2).get("type").toString().equals(water_xulik_dev_type)) {
					Intent intent = new ControlToOfficialOrTestServer().controldevtype();
					intent.putExtra("clientID", (Integer) mDevData.get(arg2).get("client_id"));
					intent.putExtra("id_devid", (Integer) (mDevData.get(arg2).get("id")));
					intent.putExtra("devid",  (String) (mDevData.get(arg2).get("devkey")));
					app.setFirmwareID((String)mDevData.get(arg2).get("ver"));
					app.setDevid((Integer) (mDevData.get(arg2).get("id")));
					app.setDevtype((String) (mDevData.get(arg2).get("type").toString()));
					context.startActivity(intent);
				}else{
					Toast.makeText(context, "该类型设备暂时不支持操作!", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(context, "不在线!不能操作设备！", Toast.LENGTH_LONG).show();
			}
		}else{
			Toast.makeText(context, "请刷新列表", 2).show();
		}
	}

	/**搜索到热点之后进行设备类型匹配显示；*/
	public ArrayList<HashMap<String, Object>> RefreshList( WifiAdmin wifiAdmin  ) {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		wifiAdmin = new WifiAdmin((Activity) context);
		if (wifiAdmin.checkState() == WifiManager.WIFI_STATE_ENABLED) {
			wifiAdmin.startScan(); // 扫描wifi热点，前提是wifi已经打开
			List<ScanResult> wifiList = wifiAdmin.getWifiList();
			String devType = null;
			String devKey = null;
			for (int index = 0; index < wifiList.size(); index++) {
				if (wifiList.get(index).SSID.length() > 12) {
					devType = wifiList.get(index).SSID.substring(0, 6);
					MyLog.i(devType, "wifi名称"+devType);
					devKey = wifiList.get(index).SSID.substring(8);
					MyLog.d("NewDev", "发现设备");
					if (devType.equals(water_dev_type)) {
						// 智能灯标识
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("dev_head_img", R.drawable.water_burfer_true);
						map.put("dev_wifi_img", R.drawable.wifi_icon3);
						map.put("dev_name", water_dev_name);
						map.put("dev_ssid",wifiList.get(index).SSID);
						map.put("devkey", devKey);
						map.put("dev_bssid", wifiList.get(index).BSSID);
						map.put("channel", WifiAdmin.getChannelByFrequency(wifiList.get(index).frequency));
						list.add(map);
					} else if ( devType.equals(demo_dev_type)) {
						// 智能标识
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("dev_head_img", R.drawable.water_burfer_true);
						map.put("dev_wifi_img", R.drawable.wifi_icon3);
						map.put("dev_name", demo_dev_name);
						map.put("dev_ssid",wifiList.get(index).SSID);
						map.put("devkey", devKey);
						map.put("dev_bssid", wifiList.get(index).BSSID);
						map.put("channel", WifiAdmin.getChannelByFrequency(wifiList.get(index).frequency));
						list.add(map);
					} else if ( devType.equals(air_dev_type)) {
						// 智能标识
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("dev_head_img", R.drawable.air_duoopen_login_true);
						map.put("dev_wifi_img", R.drawable.wifi_active);
						map.put("dev_name", air_dev_name);
						map.put("dev_ssid",wifiList.get(index).SSID);
						map.put("devkey", devKey);
						map.put("dev_bssid", wifiList.get(index).BSSID);
						map.put("channel", WifiAdmin.getChannelByFrequency(wifiList.get(index).frequency));
						list.add(map);
					}else if ( devType.equals("INL008")) {
						// 智能标识
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("dev_head_img", R.drawable.water_burfer_true);
						map.put("dev_wifi_img", R.drawable.wifi_active);
						map.put("dev_name", water_xulik_dev_name);
						map.put("dev_ssid",wifiList.get(index).SSID);
						map.put("devkey", devKey);
						map.put("dev_bssid", wifiList.get(index).BSSID);
						map.put("channel", WifiAdmin.getChannelByFrequency(wifiList.get(index).frequency));
						list.add(map);
					}
					else {
						MyLog.d("NewDev", "未知的设备类型");
					}
				}
			}
		}
		return list;
	}

	public static List<Map<String, Object>> getdata(List<Map<String, Object>> mDevData){
		List<Map<String, Object>> mDevlist= new ArrayList<Map<String, Object>>();
		for (int index = 0; index < mDevData.size(); index++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("dev_name", mDevData.get(index).get("name"));
			map.put("client_id", mDevData.get(index).get("client_id"));
			map.put("id", mDevData.get(index).get("id"));
			map.put("owner", mDevData.get(index).get("owner"));
			map.put("type", mDevData.get(index).get("type"));
			map.put("devkey",mDevData.get(index).get("devkey"));
			map.put("ver", mDevData.get(index).get("ver"));
			// 分类图标
			if (mDevData.get(index).get("type").equals(demo_dev_type)) {
				if ((Integer) (mDevData.get(index).get("client_id")) > 0) {
					// 在线
					map.put("dev_head_img", R.drawable.ic_device_t6_online);
					map.put("dev_wifi_img", R.drawable.wifi_active);
					map.put("dev_state", "在线");
				} else {
					// 不在线
					map.put("dev_head_img", R.drawable.ic_device_t6_offline);
					map.put("dev_wifi_img", R.drawable.wifi_icon0);
					map.put("dev_state", "离线");
				}

				if (mDevData.get(index).get("name").equals("未命名")) {
					map.put("dev_name", demo_dev_name);
				}

			} else if (mDevData.get(index).get("type").equals(water_dev_type)) {
				if ((Integer) (mDevData.get(index).get("client_id")) > 0) {
					// 在线
					map.put("dev_head_img", R.drawable.water_burfer_true);
					map.put("dev_wifi_img", R.drawable.wifi_active);
					map.put("dev_state", "在线");
				} else {
					// 不在线
					map.put("dev_head_img", R.drawable.water_burfer_false);
					map.put("dev_wifi_img", R.drawable.wifi_icon0);
					map.put("dev_state", "离线");
				}

				if (mDevData.get(index).get("name").equals("未命名")) {
					map.put("dev_name", water_dev_name);
				}

			} else if (mDevData.get(index).get("type").equals(air_dev_type)) {
				if ((Integer) (mDevData.get(index).get("client_id")) > 0) {
					// 在线
					map.put("dev_head_img", R.drawable.air_duoopen_login_true);
					map.put("dev_wifi_img", R.drawable.wifi_active);
					map.put("dev_state", "在线");
				} else {
					// 不在线
					map.put("dev_head_img", R.drawable.air_duoopen_login_false);
					map.put("dev_wifi_img", R.drawable.wifi_icon0);
					map.put("dev_state", "离线");
				}
				if (mDevData.get(index).get("name").equals("未命名")) {
					map.put("dev_name", air_dev_name);
				}
			} else if (mDevData.get(index).get("type").equals(water_xulik_dev_type)) {
				if ((Integer) (mDevData.get(index).get("client_id")) > 0) {
					// 在线
					map.put("dev_head_img", R.drawable.water_burfer_true);
					map.put("dev_wifi_img", R.drawable.wifi_active);
					map.put("dev_state", "在线");
				} else {
					// 不在线
					map.put("dev_head_img", R.drawable.water_burfer_false);
					map.put("dev_wifi_img", R.drawable.wifi_icon0);
					map.put("dev_state", "离线");
				}
				if (mDevData.get(index).get("name").equals("未命名")) {
					map.put("dev_name", water_xulik_dev_name);
				}
			}else {
				// 未知设备类型
				MyLog.i("DevList", "未知设备类型");
				map.put("dev_head_img", R.drawable.ic_device_light_off);
				map.put("dev_wifi_img", R.drawable.wifi_icon0);
				map.put("dev_name", "来自星星的设备");
				map.put("dev_state", "");
			}
			mDevlist.add(map);
		}
		return mDevlist;
	}
	/**根据设备id获取得到连接号的方法*/
	public static int getclient(List<Map<String, Object>> mDevData,int devid){
		int clientid=0;
		for (int index = 0; index < mDevData.size(); index++) {
			MyLog.i(""+mDevData.get(index).get("id"), ""+devid);
			if (mDevData.get(index).get("id").equals(devid)) {
				clientid=(Integer) mDevData.get(index).get("client_id");
				MyLog.i("id="+mDevData.get(index).get("id"), "devid="+devid+"client_id="+mDevData.get(index).get("client_id"));
			}
		}
		return clientid;
	}
	/***热点的搜索*/
	public int search(IotUser user){
		WifiAdmin wifiAdmin = new WifiAdmin((Activity) context);
		if (wifiAdmin.checkState() == WifiManager.WIFI_STATE_ENABLED) {
			wifiAdmin.startScan();
			List<ScanResult> wifiList = wifiAdmin.getWifiList();
			String tmp = null;
			int count = 0;
			MyLog.i("size==="+wifiList.size());
			for (int index = 0; index < wifiList.size(); index++) {
				if (wifiList.get(index).SSID.length() >12) {
					tmp = wifiList.get(index).SSID.substring(0, 6);
					// 通过这样的前缀字符识别设备INL008              HO7611
					if ( tmp.equals("INL008") || tmp.equals("INL110")|| tmp.equals("INL007")|| tmp.equals("HO7611")) {
						count++;
						MyLog.i(""+count, "count名称"+tmp);
					}
				}
			}
			if (count > 0) {
				return count;
			}
		}
		return 0;
	}

	/**
	 * 更新设备列表,只能运行在线程
	 */
	public static int RefreshList(int devid) {
		int clientidto = -1;
		List<Map<String, Object>> mDevData = new ArrayList<Map<String, Object>>();
		IotUser user =new IotUser(context);
		mDevData = user.getDevList();
		if (user.checkState() == IotUser.IOT_STATE_OK) {
			clientidto = BindingListener.getclient(mDevData, devid);
		}
		return clientidto;
	}
	/**异常控制；*/
	public static void  abnormity_control(MyBinder myBinder){
		MyApp mapp=(MyApp) MyApp.getContext().getApplicationContext();
		MyLog.i("设备类型=="+mapp.getDevtype());
		Exception_control_abstractclass abstractclass=null;
		if (mapp.getDevtype()!=null) {
			if (mapp.getDevtype().equals("INL008")) {
				abstractclass=new Xulik_Abst();
			}else if (mapp.getDevtype().equals("HO7611")) {
				abstractclass=new Haiyi_air_abst();
			}
			if (abstractclass!=null) {
				start(abstractclass, myBinder);
			}
		}
	}
	//总调用；
	public static void start(Exception_control_abstractclass abstractclass,MyBinder myBinder) {
		abstractclass.ExceptioncontrolAbstractMethod(myBinder);
	}
}
