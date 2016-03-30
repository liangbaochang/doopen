package cn.air.doopen.Receiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.air.doopen.config.RadioName;
import cn.air.doopen.view.ShowadlogActivity;
/**账号在其他设备登录后接收广播，这是全局广播*/
public class UpdateviewReceiver extends BroadcastReceiver implements RadioName{
	@Override
	public void onReceive( Context context, Intent intent) {
		//对话框提示；
		Intent intent2=new Intent(context, ShowadlogActivity.class);
		intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent2);

		//		// 通知栏提示。
		//		Notification.Builder builder = new Notification.Builder(context);
		//		Intent notificationIntent = new Intent(context, LoginActivity.class);
		//		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		//		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		//		builder.setContentTitle("家电在联");
		//		builder.setContentText("账号已在其他设备登录！");
		//		builder.setContentIntent(pendingIntent);
		//		builder.setSmallIcon(R.drawable.logo5);
		//		builder.setTicker("点击重新登录");
		//		Notification notification = builder.getNotification();
		//		notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
		//		notification.defaults |= Notification.DEFAULT_SOUND;
		//		NotificationManager nm = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
		//		nm.notify(1, notification);
	}
}
