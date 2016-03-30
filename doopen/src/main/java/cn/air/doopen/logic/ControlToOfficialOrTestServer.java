package cn.air.doopen.logic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import cn.air.doopen.freagment.Demonstratefreagment.LEDCtrlfragment;
import cn.air.doopen.freagment.Demonstratefreagment.LEDCtrlfragment_devid;
import cn.air.doopen.freagment.Demonstratefreagment.TricolourFragment;
import cn.air.doopen.freagment.Demonstratefreagment.TricolourFragment_devid;
import cn.air.doopen.freagment.airA.ContorlAir;
import cn.air.doopen.freagment.airA.ContorlAir_devid;
import cn.air.doopen.hy.device_control.WaterActivity;
import cn.air.doopen.hy.device_control.WaterActivity_devid;
import cn.air.doopen.hy.device_control.xulik.Xulik_WaterActivity;
import cn.air.doopen.hy.device_control.xulik.Xulik_WaterActivity_devid;
import cn.air.doopen.hyapp.MyApp;
/**测试正式服务器的切换。*/
public class ControlToOfficialOrTestServer {
	private SharedPreferences config;
	private boolean code_service;
	public ControlToOfficialOrTestServer() {
		config = MyApp.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
		code_service = config.getBoolean("code_service", true);
	}
	public  Intent controldevtype() {
		if (code_service==true) {
			Intent intent = new Intent(MyApp.getContext(), Xulik_WaterActivity.class);
			return intent;
		}else{
			Intent intent = new Intent(MyApp.getContext(), Xulik_WaterActivity_devid.class);
			return intent;
		}
	}
	public  Intent controldevtype_water() {
		if (code_service==true) {
			Intent intent = new Intent(MyApp.getContext(), WaterActivity.class);
			return intent;
		}else{
			Intent intent = new Intent(MyApp.getContext(), WaterActivity_devid.class);
			return intent;
		}
	}
	public  Object air_contor() {
		if (code_service==true) {
			return new ContorlAir();
		}else{
			return new ContorlAir_devid();
		}
	}
	public  Object demo_contor_LED() {
		if (code_service==true) {
			return new LEDCtrlfragment();
		}else{
			return new LEDCtrlfragment_devid();
		}
	}
	public  Object demo_contor() {
		if (code_service==true) {
			return new TricolourFragment();
		}else{
			return new TricolourFragment_devid();
		}
	}

	public String socket_url(){
		if (code_service==true) {
			//正式服务器socket地址；`
			return "120.24.84.21";
		}else{
			//	测试服务器地址socket地址；
			return "121.40.104.149";
		}
	}
	public String http_url(){
		if (code_service==true) {
			//正式服务器socket地址；`
			return "https://iot.inlinkage.com/server/iot/api/app.php";
		}else{
			//	测试服务器地址socket地址；
			return "https://iotdemo.inlinkage.com/server/iot/api/app.php";
		}
	}
	public String  update_app(){
		if (code_service==true) {
			//正式服务器socket地址；
			return "http://iot.inlinkage.com/server/iot/update/getapp.php?token=";
		}else{
			//	测试服务器地址socket地址；
			return "http://iotdemo.inlinkage.com/server/iot/update/getapp.php?token=";
		}
	}
	public String add_http_img_url(){
		if (code_service==true) {
			//正式服务器socket地址；`
			return "http://iot.inlinkage.com/server/iot/api/uploadhead.php";
		}else{
			//	测试服务器地址socket地址；
			return "http://iotdemo.inlinkage.com/server/iot/api/uploadhead.php";
		}
	}
	public String Avatar_Downloader_url(String token){
		if (code_service==true) {
			//正式服务器socket地址；`
			return "http://iot.inlinkage.com/server/iot/api/gethead.php?token="+token;
		}else{
			//	测试服务器地址socket地址；
			return "http://iotdemo.inlinkage.com/server/iot/api/gethead.php?token="+token;
		}
	}
	//	final String socket_url="120.24.84.21";
	//	//正式http协议ip地址；
	//	final String http_url="https://iot.inlinkage.com/server/iot/api/app.php";
	//	//	更新app版本；此为正式的；
	//	final String update_app="http://iot.inlinkage.com/server/iot/update/getapp.php?token=";
	//	//	/上传图片地址正式地址；
	//	String add_http_img_url="http://iot.inlinkage.com/server/iot/api/uploadhead.php";
	//	//	测试服务器地址socket地址；
	//	public static final String socket_url="121.40.104.149";
	//	//	测试服务器ip地址；
	//	public static  String http_url="https://iotdemo.inlinkage.com/server/iot/api/app.php";
	//	//更新app版本；此为测试的；
	//	public static final String update_app="http://iotdemo.inlinkage.com/server/iot/update/getapp.php?token=";
	//	///上传图片地址测试地址；
	//	public static  String add_http_img_url="http://iotdemo.inlinkage.com/server/iot/api/uploadhead.php";

	//	/上传图片
	//	public static  String add_http_img_url="http://ioserver.inlinkage.com/server/iot/api/uploadhead.php";
}
