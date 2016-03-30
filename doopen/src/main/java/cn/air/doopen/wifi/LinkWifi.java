package cn.air.doopen.wifi;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import cn.air.doopen.utli.MyLog;

public class LinkWifi {

	private WifiManager wifiManager;
	private Context context;

	/** 定义几种加密方式，一种是WEP，一种是WPA/WPA2，还有没有密码的情况 */
	public enum WifiCipherType {
		WIFI_CIPHER_WEP, WIFI_CIPHER_WPA_EAP, WIFI_CIPHER_WPA_PSK, WIFI_CIPHER_WPA2_PSK, WIFI_CIPHER_NOPASS
	}

	public LinkWifi(Context context) {
		this.context = context;
		wifiManager = (WifiManager) context
				.getSystemService(Service.WIFI_SERVICE);
	}

	/**
	 * 检测wifi状态 opened return true;
	 */
	public boolean checkWifiState() {
		boolean isOpen = true;
		int wifiState = wifiManager.getWifiState();

		if (wifiState == WifiManager.WIFI_STATE_DISABLED
				|| wifiState == WifiManager.WIFI_STATE_DISABLING
				|| wifiState == WifiManager.WIFI_STATE_UNKNOWN
				|| wifiState == WifiManager.WIFI_STATE_ENABLING) {
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
		List<WifiConfiguration> existingConfigs = wifiManager
				.getConfiguredNetworks();

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
	public WifiCipherType getCipherType(Context context, String ssid) {
		WifiCipherType type = null;
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> list = wifiManager.getScanResults();
		for (ScanResult scResult : list) {
			if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
				String capabilities = scResult.capabilities;
				Log.i("hefeng","capabilities=" + capabilities);
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
		MyLog.i(ssid, "type==="+type);
		return type;
	}
	//连接wifi；
	public void CreateWifiInfo(String ssid , String pwd) {
		WifiConfiguration config = CreateWifiInfo2(ssid,pwd, getCipherType(context, ssid));
		if (config != null) {
			MyLog.i(""+config, "config=="+config);
			wifiManager.enableNetwork(wifiManager.addNetwork(config), true);
		} else {
			MyLog.i("config", "config=="+null);
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

	/** 配置一个连接 */
	public WifiConfiguration CreateWifiInfo2(String SSID,
			String password, WifiCipherType type) {
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
			config.allowedAuthAlgorithms
			.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers
			.set(WifiConfiguration.GroupCipher.WEP104);
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
			config.allowedPairwiseCiphers
			.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedPairwiseCiphers
			.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN
					| WifiConfiguration.Protocol.WPA);

		} else if (type == WifiCipherType.WIFI_CIPHER_WPA_PSK) {
			Log.w("Wmt", "WPA加密，密码" + password);

			config.preSharedKey = "\"" + password + "\"";
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

			config.allowedPairwiseCiphers
			.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedPairwiseCiphers
			.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN
					| WifiConfiguration.Protocol.WPA);

		} else if (type == WifiCipherType.WIFI_CIPHER_WPA2_PSK) {
			Log.w("Wmt", "WPA2-PSK加密，密码=======" + password);

			config.preSharedKey = "\"" + password + "\"";
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

			config.allowedPairwiseCiphers
			.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedPairwiseCiphers
			.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

		} else {
			return null;
		}

		return config;
	}

	private int getMaxPriority() {
		List<WifiConfiguration> localList = this.wifiManager
				.getConfiguredNetworks();
		int i = 0;
		Iterator<WifiConfiguration> localIterator = localList.iterator();
		while (true) {
			if (!localIterator.hasNext())
				return i;
			WifiConfiguration localWifiConfiguration = (WifiConfiguration) localIterator
					.next();
			if (localWifiConfiguration.priority <= i)
				continue;
			i = localWifiConfiguration.priority;
		}
	}

	private int shiftPriorityAndSave() {
		List<WifiConfiguration> localList = this.wifiManager
				.getConfiguredNetworks();
		sortByPriority(localList);
		int i = localList.size();
		for (int j = 0;; ++j) {
			if (j >= i) {
				this.wifiManager.saveConfiguration();
				return i;
			}
			WifiConfiguration localWifiConfiguration = (WifiConfiguration) localList
					.get(j);
			localWifiConfiguration.priority = j;
			this.wifiManager.updateNetwork(localWifiConfiguration);
		}
	}

	private void sortByPriority(List<WifiConfiguration> paramList) {
		Collections.sort(paramList, new SjrsWifiManagerCompare());
	}

	class SjrsWifiManagerCompare implements Comparator<WifiConfiguration> {
		public int compare(WifiConfiguration paramWifiConfiguration1,
				WifiConfiguration paramWifiConfiguration2) {
			return paramWifiConfiguration1.priority
					- paramWifiConfiguration2.priority;
		}
	}
}
