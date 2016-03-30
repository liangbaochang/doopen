package cn.air.doopen.utli.tool;
/**防止用户猛击的工具类*/
public class Utils {
	private static long lastClickTime;
	public synchronized static boolean isFastClick() {
		long time = System.currentTimeMillis();   
		if ( time - lastClickTime < 500) {   
			return false;   
		}   
		lastClickTime = time;   
		return true;   
	}
}