package cn.air.doopen.hy.device_control;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import cn.air.doopen.adapter.FragmentAdapter;
import cn.air.doopen.hy.R;
import cn.air.doopen.hy.device_control.water_control_tool.Water_setingActivity;
import cn.air.doopen.hy.share.ShareDevActivity;
import cn.air.doopen.logic.ControlToOfficialOrTestServer;
/**演示板主界面；*/
public class DemonstrateActivity  extends FragmentActivity implements OnClickListener{
	private ViewPager shouchang_viewpager;
	private List<Fragment> fragments;
	private Button[] scbtn;
	private Button seting_imgbtn_led;
	private Button share_imbtn_dev_led;
	// 屏幕的宽度
	private int screenWidth;
	// tabline图片的宽度
	private int tablineWidth;
	// 当前的页面position
	private int currentPagePositon;
	private InnerPageChangeListener pageChangeListener;
	private InnerTextViewClickListener titleClickListener;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.demonstrate_mian_lay);
		shouchang_viewpager=(ViewPager)findViewById(R.id.shouchang_viewpager);
		scbtn=new Button[2];
		scbtn[0]=(Button)findViewById(R.id.shouchang_button);
		scbtn[1]=(Button)findViewById(R.id.shouchang_button2);
		seting_imgbtn_led = (Button) findViewById(R.id.seting_imgbtn_led);
		share_imbtn_dev_led = (Button) findViewById(R.id.share_imbtn_dev_led);
		fragments=new ArrayList<Fragment>();
		fragments.add((Fragment) new ControlToOfficialOrTestServer().demo_contor_LED());
		fragments.add((Fragment) new ControlToOfficialOrTestServer().demo_contor());
		FragmentAdapter adapter=new FragmentAdapter(fragments, getSupportFragmentManager());
		shouchang_viewpager.setAdapter(adapter);
		// 初始化ViewPager的PageChangerListener;
		pageChangeListener = new InnerPageChangeListener();
		// 为ViewPager设置OnPageChangerListener;
		shouchang_viewpager.setOnPageChangeListener(pageChangeListener);
		// 初始化标题文字点击的监听器
		titleClickListener = new InnerTextViewClickListener();
		// 为ViewPager的标题设置监听器
		for (int i = 0; i < scbtn.length; i++) {
			scbtn[i].setOnClickListener(titleClickListener);
		}
		// 计算屏幕的宽度;
		screenWidth = getResources().getDisplayMetrics().widthPixels;
		// 计算tabline图片的宽度;
		tablineWidth = BitmapFactory.decodeResource(getResources(),
				R.drawable.add_3).getWidth();
		// 初始化tabline的位置;
		seting_imgbtn_led.setOnClickListener(DemonstrateActivity.this);
		share_imbtn_dev_led.setOnClickListener(DemonstrateActivity.this);
	}
	/**
	 * 标题文字的点击事件的监听器
	 * 
	 * @author tarena
	 * 
	 */
	private class InnerTextViewClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			int position = 0;
			switch (v.getId()) {
			case R.id.shouchang_button2:
				position = 0;
				break;
			case R.id.shouchang_button:
				position = 1;
				break;
			}
			// 设置显示索引为???的子级页面
			shouchang_viewpager.setCurrentItem(position);
		}
	}
	/**
	 * 动画移动tabline图片
	 * 
	 * @param toPosition
	 *            被移动到的页面的position
	 */
	private void moveTabline(int toPosition) {
		Animation animation = null;
		float fromXDelta = getDeltaByPosition(currentPagePositon);
		float toXDelta = getDeltaByPosition(toPosition);
		animation = new TranslateAnimation(fromXDelta, toXDelta, 0, 0);
		animation.setDuration(300);
		animation.setFillAfter(true);
	}
	/**
	 * 根据位置计算偏移量
	 * 
	 * @param position
	 *            页面的position
	 * @return 显示当前页面时tabline的偏移量
	 */
	private float getDeltaByPosition(int position) {
		float offset = (screenWidth / 4.0f - tablineWidth) / 2;
		switch (position) {
		case 1:
			offset = screenWidth / 3 + offset;
			break;
		}
		return offset;
	}
	/**
	 * 页面切换监听器
	 * 
	 * @author tarena
	 * 
	 */
	private class InnerPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int position) {
			// 将所有标题文字设置为灰色
			for (int i = 0; i < scbtn.length; i++) {
				scbtn[i].setBackgroundResource(R.drawable.btn_style_four_focused);
			}
			// 当前页面的标题文字设置为黑色
			scbtn[position].setBackgroundResource(R.drawable.btn_style_four_normal);
			// 移动tabline图片
			moveTabline(position);
			// 更新当前页面的position
			currentPagePositon = position;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

	}
	@Override
	public void onClick(View v) {
		String devkey_id;
		switch (v.getId()) {
		case R.id.seting_imgbtn_led:
			Intent upgrade=new Intent(DemonstrateActivity.this, Water_setingActivity.class);
			startActivity(upgrade);
			break;
		case R.id.share_imbtn_dev_led:
			Intent intent1 =getIntent();
			int clientID = intent1.getIntExtra("clientID", -1);
			devkey_id = intent1.getStringExtra("devkey_id");
			Intent intent = new Intent(DemonstrateActivity.this, ShareDevActivity.class);
			intent.putExtra("devkey",devkey_id);
			intent.putExtra("devid", clientID);
			startActivity(intent);
			break;
		}		
	}

}
