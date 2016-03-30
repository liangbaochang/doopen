package cn.air.doopen.view.propupwindowview;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import cn.air.doopen.freagment.airA.AirAActivity;
import cn.air.doopen.hy.R;
import cn.air.doopen.hy.device_control.DemonstrateActivity;
import cn.air.doopen.hy.device_control.WaterActivity;
import cn.air.doopen.hy.device_control.xulik.Xulik_WaterActivity;
import cn.air.doopen.wifi.esptouch.activity.EsptouchActivity;
//预览界面！
public class PopupWindowViewClass {
	static PopupWindow mpopupWindow;
	Context context;
	public PopupWindowViewClass(Context context) {
		this.context=context;
	}
	public  void showPopMenu(Button btn) {
		View view = View.inflate(context, R.layout.alert_dialog, null);
		Button btn_take_photo = (Button) view.findViewById(R.id.btn_cancel);
		LinearLayout faclity_seekbor_jd_lb_cb = (LinearLayout) view.findViewById(R.id.faclity_seekbor_jd_lb_cb);
		LinearLayout faclity_select_cb_line2 = (LinearLayout) view.findViewById(R.id.faclity_select_cb_line2);
		LinearLayout faclity_select_cb_line3 = (LinearLayout) view.findViewById(R.id.faclity_select_cb_line3);
		LinearLayout faclity_select_cb_line4 = (LinearLayout) view.findViewById(R.id.faclity_select_cb_line4);
		LinearLayout faclity_select_cb_line5 = (LinearLayout) view.findViewById(R.id.faclity_select_cb_line5);
		RelativeLayout arltAd_relative = (RelativeLayout) view.findViewById(R.id.arltAd_relative);
		btn_take_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mpopupWindow.dismiss();
			}
		});
		faclity_seekbor_jd_lb_cb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent jd_lb_cb=new Intent(context, EsptouchActivity.class);
				context.startActivity(jd_lb_cb);
				mpopupWindow.dismiss();
			}
		});
		faclity_select_cb_line5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent Purling=new Intent(context, AirAActivity.class);
				context.startActivity(Purling);
				mpopupWindow.dismiss();
			}
		});
		faclity_select_cb_line4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent water=new Intent(context, WaterActivity.class);
				context.startActivity(water);
				mpopupWindow.dismiss();
			}
		});
		faclity_select_cb_line2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(context, DemonstrateActivity.class);
				context.startActivity(intent);
				mpopupWindow.dismiss();
			}
		});
		faclity_select_cb_line3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(context, Xulik_WaterActivity.class);
				context.startActivity(intent);
				mpopupWindow.dismiss();
			}
		});
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mpopupWindow.dismiss();
			}
		});
		//viewfinder_mask
		view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
		arltAd_relative.startAnimation(AnimationUtils.loadAnimation(context, R.anim.push_bottom_in));
		if(mpopupWindow==null){
			mpopupWindow = new PopupWindow(context);
			mpopupWindow.setWidth(LayoutParams.MATCH_PARENT);
			mpopupWindow.setHeight(LayoutParams.MATCH_PARENT);
			mpopupWindow.setBackgroundDrawable(new BitmapDrawable());
			mpopupWindow.setFocusable(true);
			mpopupWindow.setOutsideTouchable(true);
		}
		mpopupWindow.setContentView(view);
		mpopupWindow.showAtLocation(btn, Gravity.BOTTOM, 0, 0);
		mpopupWindow.update();
	}
	//	/** 
	//	 * 设置添加屏幕的背景透明度 
	//	 * @param bgAlpha 
	//	 */  
	//	public void backgroundAlpha(float bgAlpha)  
	//	{  
	//		WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();  
	//		lp.alpha=bgAlpha;
	//		((Activity) context).getWindow().setAttributes(lp);  
	//	}  
}
