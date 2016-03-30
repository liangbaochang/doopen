package cn.air.doopen.utli;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import cn.air.doopen.hy.R;

/**
 * Toast统一管理
 * 
 */
public class T {

	private T() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	public static boolean isShow = true;

	/**
	 * 短时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showShort(Context context, CharSequence message) {
		if (isShow)
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 短时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showShort(Context context, int message) {
		if (isShow)
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 长时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showLong(Context context, CharSequence message) {
		if (isShow)
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	/**
	 * 长时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showLong(Context context, int message) {
		if (isShow)
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	/**
	 * 自定义显示Toast时间
	 * 
	 * @param context
	 * @param message
	 * @param duration
	 */
	public static void show(Context context, CharSequence message, int duration) {
		if (isShow)
			Toast.makeText(context, message, duration).show();
	}

	/**
	 * 自定义显示Toast时间
	 * 
	 * @param context
	 * @param message
	 * @param duration
	 */
	public static void show(Context context, int message, int duration) {
		if (isShow)
			Toast.makeText(context, message, duration).show();
	}

	/**
	 * 自定义显
	 * 
	 * @param context
	 * @param view
	 * @param duration
	 */
	public static void customTost(Context context, View view, int duration) {
		Toast toast = new Toast(context);
		toast.setView(view);
		toast.setDuration(duration);
		toast.show();
	}

	public static void setTost(Activity activity, String text) {
		LayoutInflater layout = activity.getLayoutInflater();
		View v = layout.inflate(R.layout.tost_layout, null);
		TextView tost_text = (TextView) v.findViewById(R.id.tost_text);
		tost_text.setText(text);
		Toast toast = new Toast(activity);
		toast.setGravity(Gravity.AXIS_SPECIFIED | Gravity.TOP, 0, 350);
		toast.setDuration(600);
		toast.setView(v);
		toast.show();
	}

}