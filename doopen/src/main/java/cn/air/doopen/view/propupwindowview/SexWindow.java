package cn.air.doopen.view.propupwindowview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.air.doopen.hy.R;
import cn.air.doopen.hy.device_control.WaterActivity;
import cn.air.doopen.view.PickerView;
import cn.air.doopen.view.PickerView.onSelectListener;

public class SexWindow {
	static PopupWindow mpopupWindow ;
	Context context;
	private String cext="男";
	private int sexmsg=-1;
	public SexWindow(Context context) {
		this.context=context;
	}
	public  void showPopMenu(LinearLayout btn,final TextView viewtv) {
		View view = View.inflate(context, R.layout.sexwindow_layout, null);
		Button sexwindow_btn = (Button) view.findViewById(R.id.sexwindow_btn);
		PickerView faclity_select_cb_line4 = (PickerView) view.findViewById(R.id.pickview_sex);
		RelativeLayout sexindow_reiative = (RelativeLayout) view.findViewById(R.id.sexindow_reiative);

		List<String> data = new ArrayList<String>();
		data.add("男");
		data.add("女");
		data.add("保密");
		faclity_select_cb_line4.setData(data);
		view.setFocusable(true); // 这个很重要  
		view.setFocusableInTouchMode(true);  
		view.setOnKeyListener(new OnKeyListener()
		{
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if (event.getAction() == KeyEvent.ACTION_DOWN ||keyCode == KeyEvent.KEYCODE_BACK)
//					backgroundAlpha(1f);
				mpopupWindow.dismiss();
				return false;
			}
		});
		faclity_select_cb_line4.setOnSelectListener(new onSelectListener()
		{

			@Override
			public void onSelect(String text){
				cext=text;
			}
		});
		sexwindow_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewtv.setText(cext);
				if (cext.equals("保密")) {
					sexmsg=0;
				}
				if (cext.equals("男")) {
					sexmsg=1;
				}
				if (cext.equals("女")) {
					sexmsg=2;
				}
				//实例化SharedPreferences对象（第一步）
				SharedPreferences mySharedPreferences= context.getSharedPreferences("sex_msg",
						Activity.MODE_PRIVATE);
				//实例化SharedPreferences.Editor对象（第二步）
				SharedPreferences.Editor editor = mySharedPreferences.edit();
				//用putString的方法保存数据
				editor.putInt("sex_code",  sexmsg);
				//提交当前数据
				editor.commit(); 
				mpopupWindow.dismiss();
				//				backgroundAlpha(1f);
			}
		});
		faclity_select_cb_line4.setSelected(0);
		faclity_select_cb_line4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(context, WaterActivity.class);
				context.startActivity(intent);
				mpopupWindow.dismiss();
				//				backgroundAlpha(1f);
			}
		});
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mpopupWindow.dismiss();
				//				backgroundAlpha(1f);
			}
		});
		//viewfinder_mask
		view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
		sexindow_reiative.startAnimation(AnimationUtils.loadAnimation(context, R.anim.push_bottom_in));
		if(mpopupWindow==null){
			mpopupWindow = new PopupWindow(view);
			mpopupWindow.setWidth(LayoutParams.MATCH_PARENT);
			mpopupWindow.setHeight(LayoutParams.MATCH_PARENT);
			mpopupWindow.setBackgroundDrawable(new BitmapDrawable());
			mpopupWindow.setFocusable(true);
			mpopupWindow.setOutsideTouchable(true);
		}
		mpopupWindow.setContentView(view);
		mpopupWindow.showAtLocation(btn, Gravity.BOTTOM, 0, 0);
		mpopupWindow.update();
		//		backgroundAlpha(0.5f);
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
