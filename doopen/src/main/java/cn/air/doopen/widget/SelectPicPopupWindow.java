package cn.air.doopen.widget;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import cn.air.doopen.hy.R;
import cn.air.doopen.hy.device_control.PurlingActivity;
import cn.air.doopen.hy.device_control.WaterActivity;

public class SelectPicPopupWindow extends Activity implements OnClickListener{

	private Button btn_take_photo;
	private LinearLayout layout;
	private LinearLayout faclity_select_cb_line4;
	private LinearLayout faclity_select_cb_line5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_dialog);
		btn_take_photo = (Button) this.findViewById(R.id.btn_cancel);
		faclity_select_cb_line4 = (LinearLayout) this.findViewById(R.id.faclity_select_cb_line4);
		faclity_select_cb_line5 = (LinearLayout) this.findViewById(R.id.faclity_select_cb_line5);

		layout=(LinearLayout)findViewById(R.id.pop_layout);

		layout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		btn_take_photo.setOnClickListener(this);
		faclity_select_cb_line4.setOnClickListener(this);
		faclity_select_cb_line5.setOnClickListener(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.faclity_select_cb_line4:
			Intent intent=new Intent(getApplicationContext(), WaterActivity.class);
			startActivity(intent);
			break;
		case R.id.faclity_select_cb_line5:
			Intent Purling=new Intent(getApplicationContext(), PurlingActivity.class);
			startActivity(Purling);
			break;
		case R.id.btn_cancel:
			finish();
			break;
		}
	}

}
