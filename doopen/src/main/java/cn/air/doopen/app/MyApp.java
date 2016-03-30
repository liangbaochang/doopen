package cn.air.doopen.app;
import java.util.Map;

import android.app.Application;
import cn.air.doopen.socket.IotUser;
import in.srain.cube.Cube;
/**
 * 全局类
 */
public class MyApp extends Application {
	static MyApp app;
	private String FirmwareID;//设备版本号；
	private int devid;//设备id；
	private String devtype;//设备类型；
	private boolean type_of_service;//正式测试服务器切换标志位；
	private int control_id;//控制id；
	private boolean btn_state;//按钮状态；
	@Override
	public void onCreate() {
		super.onCreate();
		app=this;
		user = new IotUser(getApplicationContext());
		//初始化Cube框架；
		Cube.onCreate(this);
		/**监控是否在主线程执行了磁盘读写的耗时操作；如果有就对话框提示。--->属于测试代码；此代码需要再第一时间启动；*/
		//		if (mInDeveloginMode) {
		//					StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
		//							.detectNetwork().penaltyLog().penaltyDialog().build());
		//		}
	}
	public static Application getContext(){
		return app;
	}
	public String getFirmwareID() {
		return FirmwareID;
	}

	public void setFirmwareID(String firmwareID) {
		FirmwareID = firmwareID;
	}

	public int getDevid() {
		return devid;
	}

	public void setDevid(int devid) {
		this.devid = devid;
	}
	public String getDevtype() {
		return devtype;
	}

	public void setDevtype(String devtype) {
		this.devtype = devtype;
	}
	/**
	 * 是否扫描和显示新设备
	 */
	public Boolean isScanNewDev = false;

	/**
	 * 是否运行中
	 */
	public Boolean isRunning = false;

	/**
	 * 是否需要刷新设备列表
	 */
	public Boolean isNeedRefreshDevList = false;
	/**
	 * 是否需要刷新设备列表
	 */
	public static String cidentifier ="";


	/**
	 * 用户对象
	 */
	public IotUser user;
	// 用于存放倒计时时间  
	public static Map<String, Long> map;  

	@Override
	public void onTerminate() {
		super.onTerminate();
		//		Cube.getInstance();
		//		Cube.onTerminate();
	}

	public boolean isType_of_service() {
		return type_of_service;
	}

	public void setType_of_service(boolean type_of_service) {
		this.type_of_service = type_of_service;
	}
	public int getControl_id() {
		return control_id;
	}
	public void setControl_id(int control_id) {
		this.control_id = control_id;
	}
	public boolean isBtn_state() {
		return btn_state;
	}
	public void setBtn_state(boolean btn_state) {
		this.btn_state = btn_state;
	}
}
