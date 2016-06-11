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
			return "IP";
		}else{
			//	测试服务器地址socket地址；
			return "IP";
		}
	}
	public String http_url(){
		if (code_service==true) {
			//正式服务器socket地址；`
			return "IP";
		}else{
			//	测试服务器地址socket地址；
			return "IP";
		}
	}
	public String  update_app(){
		if (code_service==true) {
			//正式服务器socket地址；
			return "IP";
		}else{
			//	测试服务器地址socket地址；
			return "IP";
		}
	}
	public String add_http_img_url(){
		if (code_service==true) {
			//正式服务器socket地址；`
			return "IP";
		}else{
			//	测试服务器地址socket地址；
			return "IP";
		}
	}
	public String Avatar_Downloader_url(String token){
		if (code_service==true) {
			//正式服务器socket地址；`
			return "IP";
		}else{
			//	测试服务器地址socket地址；
			return "IP";
		}
	}

}
