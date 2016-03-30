package cn.air.doopen.logic.Abstractobj.Dev_abstr_excption_cont;
import cn.air.doopen.logic.Abstractobj.Exception_control_abstractclass;
import cn.air.doopen.socket.TcpCommSerivce.MyBinder;
import cn.air.doopen.utli.MyLog;
/**异常控制操作；*/
public class Haiyi_air_abst extends Exception_control_abstractclass{

	@Override
	public void ExceptioncontrolAbstractMethod(MyBinder myBinder) {
//		MyApp mapp=(MyApp) MyApp.getContext().getApplicationContext();
//		Water_control_tool.opendevwash(mapp.getControl_id(), mapp.isBtn_state(), mapp.getDevid(), myBinder);
		//此处调用控制代码；
		MyLog.i("海一的控制命令被调用了！");
	}

}
