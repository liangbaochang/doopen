package cn.air.doopen.freagment.airA;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import cn.air.doopen.hy.R;
import cn.air.doopen.utli.MyLog;
//http://blog.csdn.net/chenguang79/article/details/9222817
public class CustomClipprogress extends FrameLayout {

	private ClipDrawable mClipDrawable;
	private int mProgress = 0;
	private OnVolumeChangedListener mOnVolumeChangedListener;
	public final static int MY_WIDTH = 255;
	public interface OnVolumeChangedListener{
		public void setYourVolume(int index);
	}
	public void setOnVolumeChangeListener(OnVolumeChangedListener l){
		mOnVolumeChangedListener = l;
	}
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 濡傛灉娑堟伅鏄湰绋嬪簭鍙戦�佺殑
			if (msg.what == 0x123) {
				mClipDrawable.setLevel(mProgress*1000);
			}
		}
	};


	public CustomClipprogress(Context context) {
		this(context, null, 0);
	}

	public CustomClipprogress(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomClipprogress(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	private void init(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.custom_loading, null);
		addView(view);
		ImageView imageView = (ImageView) findViewById(R.id.iv_progress);
		mClipDrawable = (ClipDrawable) imageView.getDrawable();
		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
		int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
		imageView.measure(w, h);  
		int height =imageView.getMeasuredHeight();  
		int width =imageView.getMeasuredWidth();  
		MyLog.i("width="+width+"height="+height);
	}
	void setProgress(int mProgress){
		this.mProgress=mProgress;
		handler.sendEmptyMessage(0x123);
	}
	public void stop() {
		mProgress = 0;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) { 
		int X = (int) event.getX();
		int n = X * 10 / MY_WIDTH;
		MyLog.i("x="+X+"         n="+n);
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			setIndex(n);
			MyLog.i("用户手指抬起后获取当前的坐标，给监听器赋值n="+n);
			break;
		}
		mProgress=n;
		handler.sendEmptyMessage(0x123);
		return true;
	}
	private void setIndex(int n){
		if(mOnVolumeChangedListener!=null){
			mOnVolumeChangedListener.setYourVolume(n);
		}
		invalidate();
	}
}
