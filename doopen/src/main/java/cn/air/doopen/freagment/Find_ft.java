package cn.air.doopen.freagment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import cn.air.doopen.hy.MipcaActivityCapture;
import cn.air.doopen.hy.R;
/***发现界面；这里就一个点击扫面的按钮*/
public class Find_ft extends Fragment{

	private Button find_sys;
	//	private Button jiemian1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.main_find, null);
		intiview(v);
		setview();
		return v;
	}

	private void setview() {
		find_sys.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), MipcaActivityCapture.class);
				startActivity(intent);
			}
		});

		//动画test；
		//		jiemian1.setOnClickListener(new OnClickListener() {
		//
		//			@Override
		//			public void onClick(View arg0) {
		//				//砖块下落的动画
		//				ObjectAnimator drop = ObjectAnimator.ofFloat(jiemian1, "Y",0.0f,100.0f);
		//				drop.setDuration(200);
		//				drop.setInterpolator(new BounceInterpolator());
		//				//改变砖块颜色的动画,"backgroundColor"这个属性是在View中的
		//				//Button继承自TextView继承自View，View中有对这个属性的setter/getter方法
		//				ObjectAnimator backGroundColor = ObjectAnimator.ofObject(jiemian1, "backgroundColor",new ArgbEvaluator(), 0xccee4400,0xcc000000);
		//				backGroundColor.setDuration(3000);
		//				//改变字体颜色的动画,"textColor"这个属性是TextView中的
		//				//Button继承自TextView，TextView中有对这个属性的setter/getter方法
		//				ObjectAnimator textColor = ObjectAnimator.ofObject(jiemian1, "textColor", new ArgbEvaluator(), 0xfffff0f,0xff228CC8);
		//				textColor.setDuration(3000);
		//				//新建一个AnimatorSet实例
		//				AnimatorSet set = new AnimatorSet();
		//				//将这3个动画一起执行
		//				set.playTogether(drop,backGroundColor,textColor);
		//				//启动这个动画集
		//				set.start();
		//				//				set.play(drop).with(backGroundColor).with(textColor);
		//			}
		//		});
	}

	private void intiview(View v) {
		find_sys = (Button) v.findViewById(R.id.find_sys);
		//		jiemian1 = (Button) v.findViewById(R.id.jiemian1);
	}

}

