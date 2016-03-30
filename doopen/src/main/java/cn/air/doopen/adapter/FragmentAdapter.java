package cn.air.doopen.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
/**首页中的Freagment的适配；*/
public class FragmentAdapter extends FragmentPagerAdapter {
	private List<Fragment> fragments;

	public FragmentAdapter(List<Fragment> fragments, FragmentManager fm) {
		super(fm);
		getlist(fragments);
	}

	public List<Fragment> getlist(List<Fragment> fragments) {
		if (fragments == null) {
			fragments = new ArrayList<Fragment>();
		} else {
			this.fragments = fragments;
		}
		return fragments;
	}
	//	@Override        
	//	public void destroyItem(ViewGroup container, int position, Object object) {  
	//		//super.destroyItem(container, position, object);       
	//	}
	@Override
	public Fragment getItem(int pst) {
		return fragments.get(pst);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

}
