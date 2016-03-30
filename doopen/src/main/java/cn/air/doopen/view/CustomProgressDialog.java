package cn.air.doopen.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import cn.air.doopen.hy.R;

public class CustomProgressDialog extends ProgressDialog {  
	private AnimationDrawable mAnimation;  
	private Activity mContext;  
	private ImageView mImageView;  
	private String mLoadingTip;  
	private TextView mLoadingTv;  
	private int count = 0;  
	private String oldLoadingTip;  
	private int mResid;  
	public CustomProgressDialog(Activity activity) {  
		super(activity);  
		this.mContext = activity;  
		setCanceledOnTouchOutside(false);  
	}  
	@Override
	public void setOnCancelListener(OnCancelListener listener) {
		super.setOnCancelListener(listener);
		listener.onCancel(this);
		mContext.finish();
	}
	@Override  
	protected void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);  
		//		backgroundAlpha(1f);
		initView();  
		initData();  
	}  
	private void initData() {  
		mImageView.setBackgroundResource(R.drawable.pagpro_img_list);  
		// 通过ImageView对象拿到背景显示的AnimationDrawable  
		mAnimation = (AnimationDrawable) mImageView.getBackground();  
		// 为了防止在onCreate方法中只显示第一帧的解决方案之一  
		mImageView.post(new Runnable() {  
			@Override  
			public void run() {  
				mAnimation.start();  
			}  
		});  
		mLoadingTv.setText("正在加载中...");  
	}  

	public void setContent(String str) {  
		mLoadingTv.setText(str);  
	}  

	private void initView() {  
		setContentView(R.layout.loading_layout);  
		mLoadingTv = (TextView) findViewById(R.id.loadingTv);  
		mImageView = (ImageView) findViewById(R.id.loadingIv);  
	}  
	/** 
	 * 设置添加屏幕的背景透明度 
	 * @param bgAlpha 
	 */  
	public void backgroundAlpha(float bgAlpha)  
	{  
		WindowManager.LayoutParams lp =mContext.getWindow().getAttributes();  
		lp.alpha=bgAlpha;
		mContext.getWindow().setAttributes(lp);  
	}  
}  
