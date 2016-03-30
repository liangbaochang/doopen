package cn.air.doopen.Receiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import cn.air.doopen.config.RadioName;
/**断开连接后接收广播*/
public class InentRecaiver extends BroadcastReceiver implements RadioName{
	@Override
	public void onReceive( Context context, Intent intent) {
		Toast.makeText(context, "APP和服务器断开了连接，请检查网络设置", Toast.LENGTH_SHORT).show();
	}
}
