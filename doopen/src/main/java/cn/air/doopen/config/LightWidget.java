package cn.air.doopen.config;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import cn.air.doopen.utli.MyLog;

/**
 * 指定类型设备的快捷控制面板暂时还没有用到；
 * @author xwm
 *
 */
public class LightWidget extends AppWidgetProvider {
    String TAG = "LightWidget";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        MyLog.d(TAG, "onEnabled");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        MyLog.d(TAG, "onDeleted");
    }

}
