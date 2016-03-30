package cn.air.doopen.wifi;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.text.TextUtils;
import android.util.Log;
import cn.air.doopen.utli.MyLog;

//wifi控制管理类；控制连wifi；断wifi；获取wifi列表；
public class WifiAdmin {
	private WifiManager wifiManager;
	private Context context;
	private WifiInfo mWifiInfo;
	// 扫描出的网络连接列表
	private List<ScanResult> mWifiList;
	// 网络连接列表
	List<WifiConfiguration> mWifiConfiguration;
	// 定义一个WifiLock
	WifiLock mWifiLock;

	/** 定义几种加密方式，一种是WEP，一种是WPA/WPA2，还有没有密码的情况 */
	public enum WifiCipherType {
		WIFI_CIPHER_WEP, WIFI_CIPHER_WPA_EAP, WIFI_CIPHER_WPA_PSK, WIFI_CIPHER_WPA2_PSK, WIFI_CIPHER_NOPASS
	}

	public WifiAdmin(Context context) {
		this.context = context;
		wifiManager = (WifiManager) context.getSystemService(Service.WIFI_SERVICE);
		mWifiInfo = wifiManager.getConnectionInfo();
	}

	/**
	 * 检测wifi状态 opened return true;
	 */
	public boolean checkWifiState() {
		boolean isOpen = true;
		int wifiState = wifiManager.getWifiState();

		if (wifiState == WifiManager.WIFI_STATE_DISABLED || wifiState == WifiManager.WIFI_STATE_DISABLING
				|| wifiState == WifiManager.WIFI_STATE_UNKNOWN || wifiState == WifiManager.WIFI_STATE_ENABLING) {
			isOpen = false;
		}

		return isOpen;
	}

	public boolean ConnectToNetID(int netID) {
		System.out.println("ConnectToNetID netID=" + netID);
		return wifiManager.enableNetwork(netID, true);
	}

	/** 查看以前是否也配置过这个网络 */
	public WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();

		for (WifiConfiguration existingConfig : existingConfigs) {

			if (existingConfig.SSID.toString().equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	/**
	 * These values are matched in string arrays -- changes must be kept in sync
	 */
	static final int SECURITY_NONE = 0;
	static final int SECURITY_WEP = 1;
	static final int SECURITY_PSK = 2;
	static final int SECURITY_EAP = 3;

	static int getSecurity(WifiConfiguration config) {
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
			return SECURITY_PSK;
		}
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
			return SECURITY_EAP;
		}
		return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
	}

	// type;
	public WifiCipherType getCipherType(Context context, String ssid) {
		WifiCipherType type = null;
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> list = wifiManager.getScanResults();
		for (ScanResult scResult : list) {
			if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
				String capabilities = scResult.capabilities;
				Log.i("hefeng", "capabilities=" + capabilities);
				if (!TextUtils.isEmpty(capabilities)) {
					if (capabilities.contains("WPA2-PSK")) {
						// WPA-PSK加密
						type = WifiCipherType.WIFI_CIPHER_WPA2_PSK;
					} else if (capabilities.contains("WPA-PSK")) {
						// WPA-PSK加密
						type = WifiCipherType.WIFI_CIPHER_WPA_PSK;
					} else if (capabilities.contains("WPA-EAP")) {
						// WPA-EAP加密
						type = WifiCipherType.WIFI_CIPHER_WPA_EAP;
					} else if (capabilities.contains("WEP")) {
						// WEP加密
						type = WifiCipherType.WIFI_CIPHER_WEP;
					} else {
						// 无密码
						type = WifiCipherType.WIFI_CIPHER_NOPASS;
					}
				}
			}
		}
		MyLog.i(ssid, "type===" + type);
		return type;
	}

	/**
	 * @author sky Email vipa1888@163.com QQ:840950105 获取当前的网络状态 -1：没有网络
	 *         1：WIFI网络2：wap网络3：net网络
	 * @param context
	 * @return
	 */
	public static int getAPNType(Context context) {
		int netType = -1;
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			MyLog.e("networkInfo.getExtraInfo()", "networkInfo.getExtraInfo() is " + networkInfo.getExtraInfo());
			if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
				netType = 3;
			} else {
				netType = 2;
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = 1;
		}
		return netType;
	}

	public String getCiphercontains(Context context, String ssid) {
		String capabilitiesstr = null;
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> list = wifiManager.getScanResults();
		for (ScanResult scResult : list) {
			if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
				String capabilities = scResult.capabilities;
				Log.i("hefeng", "capabilities=" + capabilities);
				if (!TextUtils.isEmpty(capabilities)) {
					if (capabilities.contains("WPA2-PSK")) {
						// WPA-PSK加密
						capabilitiesstr = "psk2";
					} else if (capabilities.contains("WPA-PSK")) {
						// WPA-PSK加密
						capabilitiesstr = "psk";
					} else if (capabilities.contains("WPA-EAP")) {
						// WPA-EAP加密
						capabilitiesstr = "eap";
					} else if (capabilities.contains("WEP")) {
						// WEP加密
						capabilitiesstr = "wep";
					} else {
						// 无密码
						capabilitiesstr = "";
					}
				}
			}
		}
		return capabilitiesstr;
	}

	// 连接wifi；
	public void CreateWifiInfo(String ssid, String pwd) {
		// 如果本机已经配置过的话
		if (IsExsits(ssid) != null) {
			MyLog.i("IsExsits", "IsExsits");
			final int netID = IsExsits(ssid).networkId;
			System.out.println(
					"wifiManager.getConnectionInfo().getNetworkId()=" + wifiManager.getConnectionInfo().getNetworkId());
			if (wifiManager.getConnectionInfo().getNetworkId() == netID) {
				wifiManager.disconnect();
			} else {
				WifiConfiguration config = IsExsits(ssid);
				setMaxPriority(config);
				ConnectToNetID(config.networkId);
			}
		}
		String containsstrs = getCiphercontains(context, ssid);
		if (containsstrs != null) {
			MyLog.i("containsstrs", "containsstrs");
			if (!containsstrs.equals("")) {
				WifiConfiguration config = CreateWifiInfo2(ssid, pwd, getCipherType(context, ssid));
				if (config != null) {
					MyLog.i("" + config, "config==" + config);
					wifiManager.enableNetwork(wifiManager.addNetwork(config), true);
				} else {
					MyLog.i("config", "config==" + null);
				}
			} else {
				WifiConfiguration config = CreateWifiInfo2(ssid, "", getCipherType(context, ssid));
				if (config != null) {
					MyLog.i("" + config, "config==" + config);
					wifiManager.enableNetwork(wifiManager.addNetwork(config), true);
				} else {
					MyLog.i("config", "config==" + null);
				}
			}
		}
	}

	public WifiConfiguration setMaxPriority(WifiConfiguration config) {
		int priority = getMaxPriority() + 1;
		if (priority > 99999) {
			priority = shiftPriorityAndSave();
		}
		config.priority = priority; // 2147483647;
		System.out.println("priority=" + priority);
		wifiManager.updateNetwork(config);
		// 本机之前配置过此wifi热点，直接返回
		return config;
	}

	// 更新获取当前连接信息
	public void updateWifiInfo() {
		// 取得WifiInfo对象
		mWifiInfo = wifiManager.getConnectionInfo();
	}

	// 得到MAC地址
	public String getMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	// 连接指定ID的网络
	public void connectWifi(int netId) {
		wifiManager.enableNetwork(netId, false);
		wifiManager.reconnect();
	}

	public void removeAP(int APid) {
		wifiManager.removeNetwork(APid);
	}

	// 得到连接的ID
	public int getNetworkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	// 得到WifiInfo的所有信息包
	public String getWifiInfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	// 得到接入点的SSID
	public String getSSID() {
		if (mWifiInfo == null) {
			return "null";
		} else {
			MyLog.i("mWifiInfo.getSSID()", mWifiInfo.getSSID());
			if (mWifiInfo.getSSID() != null) {
				MyLog.i("replace", mWifiInfo.getSSID().replace("\"", ""));
				return mWifiInfo.getSSID().replace("\"", "");

			}
		}
		return "null";
	}

	// 得到接入点的BSSID
	public String getBSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	// 得到IP地址
	public int getIPAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	// 打开WIFI网卡
	public void openWifi() {
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
	}

	// 关闭WIFI网卡
	public void closeWifi() {
		if (wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(false);
		}
	}

	// 检查当前WIFI网卡状态
	public int checkState() {
		return wifiManager.getWifiState();
	}

	// 锁定WifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// 解锁WifiLock
	public void releaseWifiLock() {
		// 判断时候锁定
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}


	// 创建一个WifiLock
	public void creatWifiLock() {
		mWifiLock = wifiManager.createWifiLock("Test");
	}

	// 添加一个网络并连接
	public void addNetwork(WifiConfiguration wcg) {
		int wcgID = wifiManager.addNetwork(wcg);
		wifiManager.enableNetwork(wcgID, true);
		wifiManager.reconnect();
		// mWifiManager.updateNetwork(wcg);
		// System.out.println("a=" + wcgID);
		// System.out.println("b--" + b);
	}

	/**
	 * 移除所有同名节点
	 * 
	 * @param SSID
	 */

	public void disableAllWifiConfiguration() {
		List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			wifiManager.disableNetwork(existingConfig.networkId);
			wifiManager.disconnect();
		}
		wifiManager.saveConfiguration();
	}

	// 断开指定ID的网络
	public void disconnectWifi(int netId) {
		wifiManager.disableNetwork(netId);
		wifiManager.disconnect();
	}

	// 判断是否连接上wifi；
	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetworkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	// 扫描附近AP列表,获取历史AP列表
	public void startScan() {
		wifiManager.startScan();
		// 得到扫描结果
		mWifiList = wifiManager.getScanResults();
		// 得到配置好的网络连接
		mWifiConfiguration = wifiManager.getConfiguredNetworks();
	}
	// 得到网络列表
	public List<ScanResult> getWifiList() {
		if (mWifiList == null){
			MyLog.i("err", "居然为空列表");
		}
		return mWifiList;
	}
	/** 配置一个连接 */
	public WifiConfiguration CreateWifiInfo2(String SSID, String password, WifiCipherType type) {
		int priority;
		WifiConfiguration config = this.IsExsits(SSID);
		if (config != null) {
			// Log.w("Wmt", "####之前配置过这个网络，删掉它");
			// wifiManager.removeNetwork(config.networkId); // 如果之前配置过这个网络，删掉它
			// 本机之前配置过此wifi热点，调整优先级后，直接返回
			return setMaxPriority(config);
		}

		config = new WifiConfiguration();
		/* 清除之前的连接信息 */
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		config.status = WifiConfiguration.Status.ENABLED;
		// config.BSSID = BSSID;
		// config.hiddenSSID = true;

		priority = getMaxPriority() + 1;
		if (priority > 99999) {
			priority = shiftPriorityAndSave();
		}
		config.priority = priority; // 2147483647;
		/* 各种加密方式判断 */
		if (type == WifiCipherType.WIFI_CIPHER_NOPASS) {
			Log.w("Wmt", "没有密码");
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		} else if (type == WifiCipherType.WIFI_CIPHER_WEP) {
			Log.w("Wmt", "WEP加密，密码" + password);
			config.preSharedKey = "\"" + password + "\"";
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		} else if (type == WifiCipherType.WIFI_CIPHER_WPA_EAP) {
			Log.w("Wmt", "WPA_EAP加密，密码" + password);
			config.preSharedKey = "\"" + password + "\"";
			config.hiddenSSID = true;
			config.status = WifiConfiguration.Status.ENABLED;
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN | WifiConfiguration.Protocol.WPA);

		} else if (type == WifiCipherType.WIFI_CIPHER_WPA_PSK) {
			Log.w("Wmt", "WPA加密，密码" + password);

			config.preSharedKey = "\"" + password + "\"";
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN | WifiConfiguration.Protocol.WPA);

		} else if (type == WifiCipherType.WIFI_CIPHER_WPA2_PSK) {
			Log.w("Wmt", "WPA2-PSK加密，密码=======" + password);

			config.preSharedKey = "\"" + password + "\"";
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

		} else {
			return null;
		}

		return config;
	}

	private int getMaxPriority() {
		List<WifiConfiguration> localList = this.wifiManager.getConfiguredNetworks();
		int i = 0;
		Iterator<WifiConfiguration> localIterator = localList.iterator();
		while (true) {
			if (!localIterator.hasNext())
				return i;
			WifiConfiguration localWifiConfiguration = (WifiConfiguration) localIterator.next();
			if (localWifiConfiguration.priority <= i)
				continue;
			i = localWifiConfiguration.priority;
		}
	}

	private int shiftPriorityAndSave() {
		List<WifiConfiguration> localList = this.wifiManager.getConfiguredNetworks();
		sortByPriority(localList);
		int i = localList.size();
		for (int j = 0;; ++j) {
			if (j >= i) {
				this.wifiManager.saveConfiguration();
				return i;
			}
			WifiConfiguration localWifiConfiguration = (WifiConfiguration) localList.get(j);
			localWifiConfiguration.priority = j;
			this.wifiManager.updateNetwork(localWifiConfiguration);
		}
	}

	private void sortByPriority(List<WifiConfiguration> paramList) {
		Collections.sort(paramList, new SjrsWifiManagerCompare());
	}

	class SjrsWifiManagerCompare implements Comparator<WifiConfiguration> {
		public int compare(WifiConfiguration paramWifiConfiguration1, WifiConfiguration paramWifiConfiguration2) {
			return paramWifiConfiguration1.priority - paramWifiConfiguration2.priority;
		}
	}

	/**
	 * 取得当前连接的信道
	 * 
	 * @return
	 */
	public int getCurrentChannel() {
		mWifiInfo = wifiManager.getConnectionInfo();
		mWifiList = wifiManager.getScanResults();
		for (ScanResult result : mWifiList) {
			if (result.BSSID.equalsIgnoreCase(mWifiInfo.getBSSID()) && result.SSID
					.equalsIgnoreCase(mWifiInfo.getSSID().substring(1, mWifiInfo.getSSID().length() - 1))) {
				return getChannelByFrequency(result.frequency);
			}
		}

		return -1;
	}

	/**
	 * 根据频率获得信道
	 * 
	 * @param frequency
	 * @return
	 */
	public static int getChannelByFrequency(int frequency) {
		int channel = -1;
		switch (frequency) {
		case 2412:
			channel = 1;
			break;
		case 2417:
			channel = 2;
			break;
		case 2422:
			channel = 3;
			break;
		case 2427:
			channel = 4;
			break;
		case 2432:
			channel = 5;
			break;
		case 2437:
			channel = 6;
			break;
		case 2442:
			channel = 7;
			break;
		case 2447:
			channel = 8;
			break;
		case 2452:
			channel = 9;
			break;
		case 2457:
			channel = 10;
			break;
		case 2462:
			channel = 11;
			break;
		case 2467:
			channel = 12;
			break;
		case 2472:
			channel = 13;
			break;
		case 2484:
			channel = 14;
			break;
		case 5745:
			channel = 149;
			break;
		case 5765:
			channel = 153;
			break;
		case 5785:
			channel = 157;
			break;
		case 5805:
			channel = 161;
			break;
		case 5825:
			channel = 165;
			break;
		}
		return channel;
	}

	public static boolean isWiFiActive(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] infos = connectivity.getAllNetworkInfo();
			if (infos != null) {
				for (NetworkInfo ni : infos) {
					if (ni.getTypeName().equals("WIFI") && ni.isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	// .........
}