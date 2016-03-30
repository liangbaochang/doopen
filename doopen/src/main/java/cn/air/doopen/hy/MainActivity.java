package cn.air.doopen.hy;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.air.doopen.adapter.FragmentAdapter;
import cn.air.doopen.freagment.Facility_ft;
import cn.air.doopen.freagment.Find_ft;
import cn.air.doopen.freagment.Personage_ft;
import cn.air.doopen.hy.R;
import cn.air.doopen.hy.R.color;
import cn.air.doopen.utli.MyLog;
import android.widget.RadioButton;
import android.widget.Toast;
/**主界面*/
public class MainActivity extends FragmentActivity  implements OnCheckedChangeListener{
	private ViewPager shouchang_viewpager;
	private Personage_ft personage;
	private Find_ft find;
	private Facility_ft facility;
	private List<Fragment> fragments;
	private RadioButton[] scbtn;
	private int screenWidth;
	private int tablineWidth;
	private int currentPagePositon;
	private InnerPageChangeListener pageChangeListener;
	private InnerTextViewClickListener titleClickListener;
	public static MainActivity instance;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_main);
		instance=this;
		shouchang_viewpager = (ViewPager) findViewById(R.id.main_viewpager);
		scbtn = new RadioButton[3];
		scbtn[0] = (RadioButton) findViewById(R.id.rBtn_Msg);
		scbtn[1] = (RadioButton) findViewById(R.id.rBtn_Fre);
		scbtn[2] = (RadioButton) findViewById(R.id.rBtn_Plu);
		scbtn[0] .setOnCheckedChangeListener(this);
		scbtn[1] .setOnCheckedChangeListener(this);
		scbtn[2] .setOnCheckedChangeListener(this);
		scbtn[0].setChecked(true);
		fragments = new ArrayList<Fragment>();
		personage = new Personage_ft();
		find = new Find_ft();
		facility = new Facility_ft();
		fragments.add(facility);
		fragments.add(find);
		fragments.add(personage);
		FragmentAdapter adapter = new FragmentAdapter(fragments, getSupportFragmentManager());
		shouchang_viewpager.setAdapter(adapter);
		pageChangeListener = new InnerPageChangeListener();
		shouchang_viewpager.setOnPageChangeListener(pageChangeListener);
		titleClickListener = new InnerTextViewClickListener();
		for (int i = 0; i < scbtn.length; i++) {
			scbtn[i].setOnClickListener(titleClickListener);
		}
		//		 1.获取当前版本号
		try {
			PackageManager pm = getPackageManager();
			PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);//getPackageName()是你当前类的包名，0代表是获取版本信息
			String name = pi.versionName;
			int code = pi.versionCode;
			MyLog.i("程序版本号", "版本号"+name+"内部版本号"+code);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private class InnerTextViewClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			int position = 0;
			switch (v.getId()) {
			case R.id.rBtn_Msg:
				position = 0;
				
				break;
			case R.id.rBtn_Fre:
				position = 1;
				break;
			case R.id.rBtn_Plu:
				position = 2;
				break;
			}
			shouchang_viewpager.setCurrentItem(position);
		}
	}

	/**
	 * 
	 * @param toPosition
	 */
	private void moveTabline(int toPosition) {
		Animation animation = null;
		float fromXDelta = getDeltaByPosition(currentPagePositon);
		float toXDelta = getDeltaByPosition(toPosition);
		animation = new TranslateAnimation(fromXDelta, toXDelta, 0, 0);
		animation.setDuration(300);
		animation.setFillAfter(true);
	}

	private float getDeltaByPosition(int position) {
		float offset = (screenWidth / 4.0f - tablineWidth) / 2;
		switch (position) {
		case 1:
			offset = screenWidth / 3 + offset;
			break;
		}
		return offset;
	}

	private class InnerPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int position) {
			for (int i = 0; i < scbtn.length; i++) {
				scbtn[i].setTextColor(color.blueness);
			}
			scbtn[position].setTextColor(color.black);
			currentPagePositon = position;

			if (position==0) {
				scbtn[1].setChecked(false);
				scbtn[2].setChecked(false); 
				scbtn[0].setChecked(true); 
			}

			if (position==1) {
				scbtn[1].setChecked(true);
				scbtn[2].setChecked(false); 
				scbtn[0].setChecked(false); 
			}

			if (position==2) {
				scbtn[1].setChecked(false);
				scbtn[2].setChecked(true); 
				scbtn[0].setChecked(false); 
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

	}


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.rBtn_Msg:
			if (isChecked) {
				scbtn[1].setChecked(false);
				scbtn[2].setChecked(false);
				scbtn[0].setChecked(true);
			} else {
			}
			break;
		case R.id.rBtn_Fre:
			if (isChecked) {
				scbtn[2].setChecked(false);
				scbtn[0].setChecked(false);
				scbtn[1].setChecked(true);
			} else {
			}
			break;
		case R.id.rBtn_Plu:
			if (isChecked) {
				scbtn[1].setChecked(false);
				scbtn[0].setChecked(false);
				scbtn[2].setChecked(true);
			} else {
			}
			break;
		}
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() != KeyEvent.ACTION_UP) {
			exitBy2Click(); //调用双击退出函数 
			return true;
		} else {
			return super.dispatchKeyEvent(event);
		}
	}

	/** * 双击退出函数 */
	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", 1).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
		} else {
			finish();
			System.exit(0);
		}
	}

}
