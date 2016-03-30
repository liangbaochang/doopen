package cn.air.doopen.freagment.airA;
import java.util.ArrayList;
import java.util.List;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import cn.air.doopen.adapter.FragmentAdapter;
import cn.air.doopen.hy.R;
import cn.air.doopen.logic.ControlToOfficialOrTestServer;
/**信息；售后；分享；关于；空气净化器主界面*/
public class AirAActivity extends FragmentActivity {

	private CustomViewPager seek_viewpager;
	private List<Fragment> freagments;
	private RadioButton[] btn;
	// 屏幕的宽度
	private int screenWidth;
	// tabline图片的宽度
	private int tablineWidth;
	// 当前的页面position
	private int currentPagePositon;
	// 定义字符串数组作为提示的文本;
	private ImageView seek_img_red;
	private InnerPageChangeListener pageChangeListener;
	private InnerTextViewClickListener titleClickListener;
	public static  String autoCompleteTextView1data;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_air_a);
		setview();
		setadapter();
	}
	private void setview() {
		seek_viewpager=(CustomViewPager)findViewById(R.id.Air_viewpager);
		btn=new RadioButton[5];
		btn[0]=(RadioButton)findViewById(R.id.Air_message);
		btn[1]=(RadioButton)findViewById(R.id.rBtn_Fre);
		btn[2]=(RadioButton)findViewById(R.id.Air_shoping);
		btn[3]=(RadioButton)findViewById(R.id.Air_share);
		btn[4]=(RadioButton)findViewById(R.id.Air_about);
		seek_img_red=(ImageView)findViewById(R.id.Air_msg);
	}
	
	//自定显示；
	private void setadapter() {
		freagments=new ArrayList<Fragment>();
		AustinAir air=new AustinAir();
		StoreAir storeAir=new StoreAir();
		ShareAir  shareAir=new ShareAir();
		AboutAir aboutAir=new AboutAir();
		freagments.add((Fragment) new ControlToOfficialOrTestServer().air_contor());
		freagments.add(air);
		freagments.add(storeAir);
		freagments.add(shareAir);
		freagments.add(aboutAir);
		FragmentAdapter adapter=new FragmentAdapter(freagments, getSupportFragmentManager());
		seek_viewpager.setAdapter(adapter);
		// 初始化ViewPager的PageChangerListener;
		pageChangeListener = new InnerPageChangeListener();
		// 为ViewPager设置OnPageChangerListener;
		seek_viewpager.setOnPageChangeListener(pageChangeListener);
		// 初始化标题文字点击的监听器
		titleClickListener = new InnerTextViewClickListener();
		// 为ViewPager的标题设置监听器
		for (int i = 0; i < btn.length; i++) {
			btn[i].setOnClickListener(titleClickListener);
		}
		// 计算屏幕的宽度;
		screenWidth = getResources().getDisplayMetrics().widthPixels;
		// 计算tabline图片的宽度;
		tablineWidth = BitmapFactory.decodeResource(getResources(),
				R.drawable.slide_triangle).getWidth();
		// 初始化tabline的位置;
		moveTabline(0);
	}

	/**
	 * 标题文字的点击事件的监听器
	 * 
	 * @author tarena
	 * 
	 */
	private class InnerTextViewClickListener implements View.OnClickListener {
		//		wById(R.id.Air_message);
		//		btn[1]=(RadioButton)findViewById(R.id.rBtn_Fre);
		//		btn[2]=(RadioButton)findViewById(R.id.Air_shoping);
		//		btn[3]=(RadioButton)findViewById(R.id.Air_share);
		//		btn[4]=(RadioButton)findViewById(R.id.Air_about);
		@Override
		public void onClick(View v) {
			int position = 0;
			switch (v.getId()) {
			case R.id.Air_message:
				position = 0;
				break;
			case R.id.rBtn_Fre:
				position = 1;
				break;
			case R.id.Air_shoping:
				position = 2;
				break;
			case R.id.Air_share:
				position = 3;
				break;
			case R.id.Air_about:
				position = 4;
				break;
			}
			// 设置显示索引为???的子级页面
			seek_viewpager.setCurrentItem(position);
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
		seek_img_red.startAnimation(animation);
	}
	/**
	 * 根据位置计算偏移量
	 * 
	 * @param position
	 *            页面的position
	 * @return 显示当前页面时tabline的偏移量
	 */
	private float getDeltaByPosition(int position) {
		float offset = (screenWidth / 5.0f - tablineWidth) / 2;
		switch (position) {
		case 1:
			offset = screenWidth / 5+ offset;
			break;
		case 2:
			offset = screenWidth / 5*2+ offset;
			break;
		case 3:
			offset = screenWidth /5*3 + offset;
			break;
		case 4:
			offset = screenWidth / 5*4+ offset;
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
			for (int i = 0; i < btn.length; i++) {
				btn[i].setTextColor(Color.WHITE);
			}
			// 当前页面的标题文字设置为黑色
			btn[position].setTextColor(Color.parseColor("#000000"));
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


}
