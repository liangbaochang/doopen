package cn.air.doopen.view.propupwindowview;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

public class City_select_class implements OnWheelChangedListener{
	private Context context;
	public City_select_class(Context context) {
		this.context=context;
	}
	/**
	 * 把全国的省市区的信息以json的格式保存，解析完成后赋值为null
	 */
	private JSONObject mJsonObj;

	/**
	 * 所有省
	 */
	private String[] mProvinceDatas;
	/**
	 * key - 省 value - 市s
	 */
	private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	/**
	 * key - 市 values - 区s
	 */
	private Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();

	/**
	 * 当前省的名称
	 */
	private String mCurrentProviceName;
	/**
	 * 当前市的名称
	 */
	private String mCurrentCityName;
	/**
	 * 当前区的名称
	 */
	private String mCurrentAreaName ="";

	static PopupWindow mpopupWindowcity ;

	/**
	 * 省的WheelView控件
	 */
	private WheelView mProvince;
	/**
	 * 市的WheelView控件
	 */
	private WheelView mCity;
	/**
	 * 区的WheelView控件
	 */
	private WheelView mArea;

	public  void showPopMenu(final TextView myuser_site,LinearLayout site_text) {
		View view = View.inflate(context, R.layout.select_city, null);
		mProvince = (WheelView) view.findViewById(R.id.id_province);
		mCity = (WheelView) view.findViewById(R.id.id_city);
		mArea = (WheelView) view.findViewById(R.id.id_area);
		Button city_btn_select = (Button) view.findViewById(R.id.city_btn_select);
		RelativeLayout activity_delete_dialogs_RelativeLayout = (RelativeLayout) view.findViewById(R.id.city_select_layout);
		initJsonData();
		initDatas();
		view.setFocusable(true); // 这个很重要  
		view.setFocusableInTouchMode(true);  
		mProvince.setViewAdapter(new ArrayWheelAdapter<String>(context, mProvinceDatas));
		// 添加change事件
		mProvince.addChangingListener(this);
		// 添加change事件
		mCity.addChangingListener(this);
		// 添加change事件
		mArea.addChangingListener(this);

		mProvince.setVisibleItems(5);
		mCity.setVisibleItems(5);
		mArea.setVisibleItems(5);
		updateCities();
		updateAreas();
		view.setOnKeyListener(new OnKeyListener()
		{
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if (event.getAction() == KeyEvent.ACTION_DOWN ||keyCode == KeyEvent.KEYCODE_BACK)
//					backgroundAlpha(1f);
				mpopupWindowcity.dismiss();
				return false;
			}
		});
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mpopupWindowcity.dismiss();
//				backgroundAlpha(1f);
			}
		});
		city_btn_select.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				Toast.makeText(context, mCurrentProviceName + mCurrentCityName + mCurrentAreaName, 1).show();
				myuser_site.setText(mCurrentProviceName + mCurrentCityName + mCurrentAreaName);
				mpopupWindowcity.dismiss();
//				backgroundAlpha(1f);
			}
		});
		view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
		activity_delete_dialogs_RelativeLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.push_bottom_in));
		if(mpopupWindowcity==null){
			mpopupWindowcity = new PopupWindow(view);
			mpopupWindowcity.setWidth(LayoutParams.MATCH_PARENT);
			mpopupWindowcity.setHeight(LayoutParams.MATCH_PARENT);
			mpopupWindowcity.setBackgroundDrawable(new BitmapDrawable());
			mpopupWindowcity.setFocusable(true);
			mpopupWindowcity.setOutsideTouchable(true);
		}
		mpopupWindowcity.setContentView(view);
		mpopupWindowcity.showAtLocation(site_text, Gravity.BOTTOM, 0, 0);
		mpopupWindowcity.update();
//		backgroundAlpha(0.5f);
	}
	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas()
	{
		int pCurrent = mCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mAreaDatasMap.get(mCurrentCityName);

		if (areas == null)
		{
			areas = new String[] { "" };
		}
		mArea.setViewAdapter(new ArrayWheelAdapter<String>(context, areas));
		mArea.setCurrentItem(0);
	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities()
	{
		int pCurrent = mProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null)
		{
			cities = new String[] { "" };
		}
		mCity.setViewAdapter(new ArrayWheelAdapter<String>(context, cities));
		mCity.setCurrentItem(0);
		updateAreas();
	}

	/**
	 * 解析整个Json对象，完成后释放Json对象的内存
	 */
	private void initDatas()
	{
		try
		{
			JSONArray jsonArray = mJsonObj.getJSONArray("citylist");
			mProvinceDatas = new String[jsonArray.length()];
			for (int i = 0; i < jsonArray.length(); i++)
			{
				JSONObject jsonP = jsonArray.getJSONObject(i);// 每个省的json对象
				String province = jsonP.getString("p");// 省名字

				mProvinceDatas[i] = province;

				JSONArray jsonCs = null;
				try
				{
					/**
					 * Throws JSONException if the mapping doesn't exist or is
					 * not a JSONArray.
					 */
					jsonCs = jsonP.getJSONArray("c");
				} catch (Exception e1)
				{
					continue;
				}
				String[] mCitiesDatas = new String[jsonCs.length()];
				for (int j = 0; j < jsonCs.length(); j++)
				{
					JSONObject jsonCity = jsonCs.getJSONObject(j);
					String city = jsonCity.getString("n");// 市名字
					mCitiesDatas[j] = city;
					JSONArray jsonAreas = null;
					try
					{
						/**
						 * Throws JSONException if the mapping doesn't exist or
						 * is not a JSONArray.
						 */
						jsonAreas = jsonCity.getJSONArray("a");
					} catch (Exception e)
					{
						continue;
					}

					String[] mAreasDatas = new String[jsonAreas.length()];// 当前市的所有区
					for (int k = 0; k < jsonAreas.length(); k++)
					{
						String area = jsonAreas.getJSONObject(k).getString("s");// 区域的名称
						mAreasDatas[k] = area;
					}
					mAreaDatasMap.put(city, mAreasDatas);
				}

				mCitisDatasMap.put(province, mCitiesDatas);
			}

		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		mJsonObj = null;
	}

	/**
	 * 从assert文件夹中读取省市区的json文件，然后转化为json对象
	 */
	private void initJsonData()
	{
		try
		{
			StringBuffer sb = new StringBuffer();
			InputStream is = context.getAssets().open("city.json");
			int len = -1;
			byte[] buf = new byte[1024];
			while ((len = is.read(buf)) != -1)
			{
				sb.append(new String(buf, 0, len, "gbk"));
			}
			is.close();
			mJsonObj = new JSONObject(sb.toString());
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * change事件的处理
	 */
	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue)
	{
		if (wheel == mProvince)
		{
			updateCities();
		} else if (wheel == mCity)
		{
			updateAreas();
		} else if (wheel == mArea)
		{
			mCurrentAreaName = mAreaDatasMap.get(mCurrentCityName)[newValue];
		}
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
