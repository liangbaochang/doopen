package cn.air.doopen.Receiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import cn.air.doopen.config.RadioName;
/**离线后接收关播，这个是全局广播*/
public class StateReceiver extends BroadcastReceiver implements RadioName{
	@Override
	public void onReceive( Context context, Intent intent) {
		Toast.makeText(context, "设备已离线，暂时无法控制操作！等待设备上线后才可以控制", Toast.LENGTH_LONG).show();
	}
}