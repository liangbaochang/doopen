package cn.air.doopen.logic.Abstractobj.Dev_abstr_excption_cont;

import cn.air.doopen.hy.device_control.water_control_tool.Water_control_tool;
import cn.air.doopen.hyapp.MyApp;
import cn.air.doopen.logic.Abstractobj.Exception_control_abstractclass;
import cn.air.doopen.socket.TcpCommSerivce.MyBinder;
/**异常控制操作*/
public class Xulik_Abst extends Exception_control_abstractclass{
	
	@Override
	public void ExceptioncontrolAbstractMethod(MyBinder myBinder) {
		//保持控制状态发送控制
		MyApp mapp=(MyApp) MyApp.getContext().getApplicationContext();
		Water_control_tool.opendevwash(mapp.getControl_id(), mapp.isBtn_state(), mapp.getDevid(), myBinder);
	}
}
