package cn.air.doopen.utli;

import java.net.CookieStore;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;

/**退出所有activity；*/
public class ActivityManager {

	private List<Activity> activityList = new LinkedList<Activity>();
	private static ActivityManager instance;

	private ActivityManager() {
	}

	// 单例模式中获取唯一的MyApplication实例
	public static ActivityManager getInstance() {
		if (null == instance) {
			instance = new ActivityManager();
		}
		return instance;
	}

	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// 遍历所有Activity并finish
	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		//System.exit(0);
	}

	private CookieStore cookies;  
	public CookieStore getCookie(){   
		return cookies;
	}
	public void setCookie(CookieStore cks){
		cookies = cks;
	}

}
