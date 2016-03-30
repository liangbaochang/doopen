package cn.air.doopen.view.propupwindowview;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
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
import android.widget.Toast;
import cn.air.doopen.hy.R;
import cn.air.doopen.view.wheelview.NumericWheelAdapter;
import cn.air.doopen.view.wheelview.OnWheelChangedListener;
import cn.air.doopen.view.wheelview.WheelView;

public class Choosebirthday {
	static PopupWindow mpopupWindow ;
	static Calendar calendar = Calendar.getInstance();
	static int year = calendar.get(Calendar.YEAR);
	private static int START_YEAR = 1900, END_YEAR = year;
	Context context;
	public Choosebirthday(Context context) {
		this.context=context;
	}
	public  void showPopMenu(LinearLayout line,final TextView textView) {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);

		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		View view = View.inflate(context, R.layout.time_layout, null);
		final RelativeLayout birthday = (RelativeLayout) view.findViewById(R.id.birthday);
		final Button btn_datetime_sure = (Button) view.findViewById(R.id.btn_datetime_sure);
		// 年
		final WheelView wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
		wv_year.setCyclic(true);// 可循环滚动
		wv_year.setLabel("年");// 添加文字
		wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

		// 月
		final WheelView wv_month = (WheelView) view.findViewById(R.id.month);
		wv_month.setAdapter(new NumericWheelAdapter(1, 12));
		wv_month.setCyclic(true);
		wv_month.setLabel("月");
		wv_month.setCurrentItem(month);

		// 日
		final WheelView wv_day = (WheelView) view.findViewById(R.id.day);
		wv_day.setCyclic(true);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdapter(1, 29));
			else
				wv_day.setAdapter(new NumericWheelAdapter(1, 28));
		}
		wv_day.setLabel("日");
		wv_day.setCurrentItem(day - 1);
		view.setFocusable(true); // 这个很重要  
		view.setFocusableInTouchMode(true);  
		view.setOnKeyListener(new OnKeyListener()
		{
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_BACK)
//					backgroundAlpha(1f);
				mpopupWindow.dismiss();
				return false;
			}
		});

		btn_datetime_sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 如果是个数,则显示为"02"的样式
				String parten = "00";
				// 设置日期的显示
				DecimalFormat decimal = new DecimalFormat(parten);
//				Toast.makeText(context, (wv_year.getCurrentItem() + START_YEAR) + "-"
//						+ decimal.format((wv_month.getCurrentItem() + 1)) + "-"
//						+ decimal.format((wv_day.getCurrentItem() + 1)) + " ", Toast.LENGTH_SHORT).show();

				textView.setText((wv_year.getCurrentItem() + START_YEAR) + "-"
						+ decimal.format((wv_month.getCurrentItem() + 1)) + "-"
						+ decimal.format((wv_day.getCurrentItem() + 1)) + " ");
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
		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + START_YEAR;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String
						.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(wv_month
						.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0)
							|| year_num % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
							.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);

		// 根据屏幕密度来指定选择器字体的大小
		int textSize = 0;
		textSize = 12;
		wv_day.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;

		//viewfinder_mask
		view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
		birthday.startAnimation(AnimationUtils.loadAnimation(context, R.anim.push_bottom_in));
		if(mpopupWindow==null){
			mpopupWindow = new PopupWindow(view,LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,false);   
			mpopupWindow.setBackgroundDrawable(new BitmapDrawable());
			mpopupWindow.setFocusable(true);
			//设置点击窗口外边窗口消失     
			mpopupWindow.setOutsideTouchable(false);    
		}
		mpopupWindow.setContentView(view);
		mpopupWindow.showAtLocation(line, Gravity.BOTTOM, 0, 0);
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
