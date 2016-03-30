package cn.air.doopen.utli;

import android.util.Log;
/**统一统一管理；控制是否显示*/
public class MyLog {
	//采用日志调试多线程资源竞争问题的方法；这种方法能够尽量避免日志代码多言线程运行影响调试的过程。
	/**static class LogEntry{
		public long threadid;
		public String meassage;
		public Object Data;
	}
	private LogEntry[] _entries=null;
	private Object _lock=new Object();
	public void add(String mag,Object data){
		_entries=new LogEntry[1024];
		int index=0;
		synchronized (_lock) {
			index=++index%1024;
		}
		LogEntry entry=new LogEntry();
		entry.Data=data;
		entry.meassage=mag;
		Thread thread=Thread.currentThread();
		entry.threadid=thread.getId();
		_entries[index]=entry;
	}*/
	//控制显示；
	static boolean flag=true;

	public static  void i(String tag,Object msg){
		if (flag) {
			Log.i(tag, ""+msg);
		}
	}
	public static  void i(Object msg){
		if (flag) {
			Log.i("打印日志", ""+msg);
		}
	}
	public static void e(String tag,Object msg){
		if (flag) {
			Log.e("打印日志", ""+msg);
		}
	}
	public static void e(Object msg){
		if (flag) {
			Log.e("打印日志", ""+msg);
		}
	}
	public static void d(String tag,Object msg){
		if (flag) {
			Log.d("打印日志", ""+msg);
		}
	}
	public static void d(Object msg){
		if (flag) {
			Log.d("打印日志", ""+msg);
		}
	}
	public static void w(String tag,Object msg){
		if (flag) {
			Log.w("打印日志", ""+msg);
		}
	}
	public static void w(Object msg){
		if (flag) {
			Log.w("打印日志", ""+msg);
		}
	}
}
