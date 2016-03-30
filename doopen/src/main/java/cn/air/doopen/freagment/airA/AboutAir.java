package cn.air.doopen.freagment.airA;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.air.doopen.hy.R;

/**空气净化器的关于界面；*/
public class AboutAir extends Fragment{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.air_about_txt, null);
		return v;
	}
}
