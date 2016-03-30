package cn.air.doopen.freagment.airA;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.air.doopen.hy.R;
import cn.air.doopen.hy.share.QuickmarkActivity;
import cn.air.doopen.socket.IotUser;

/**海一的分享界面；*/
public class ShareAir extends Fragment{
	private ImageView sweepIV;
	private EditText et1;
	private Button bt1;
	private Button quick_btn;
	private Button dev_share_result;
	private RelativeLayout shate_dev_fen;
	@SuppressLint("ResourceAsColor")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_share_dev, null);
		sweepIV = (ImageView)v.findViewById(R.id.Scan_image);
		shate_dev_fen = (RelativeLayout)v.findViewById(R.id.shate_dev_fen);
		//		shate_dev_fen.setBackgroundResource(R.color.air_bj);
		et1 = (EditText)v.findViewById(R.id.edt_operator_name);
		bt1 = (Button)v.findViewById(R.id.share_true_btn);
		quick_btn = (Button)v.findViewById(R.id.quick_btn);
		dev_share_result = (Button)v.findViewById(R.id.dev_share_result);
		dev_share_result.setVisibility(View.GONE);
		bt1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AsyncTask<Void, Void, Integer>(){
					@Override
					protected void onPostExecute(Integer result) {
						super.onPostExecute(result);
						if(result==0){
							Toast.makeText(getActivity(), "分享成功,去消息中心同意吧！", Toast.LENGTH_LONG).show();
						}else if (result==204||result==205) {
							Toast.makeText(getActivity(), "您是被分享用户,无法分享哦！", Toast.LENGTH_LONG).show();
						}else{
							Toast.makeText(getActivity(), "分享失败，", Toast.LENGTH_LONG).show();
						}
					}
					IotUser user = new IotUser(getActivity());
					@Override
					protected Integer doInBackground(Void... params) {
						if(et1.getText().toString().length() > 0){
							int code=user.shareDev(et1.getText().toString(), getActivity().getIntent().getIntExtra("id_devid", 0));
							return code;
						}
						return -1;
					}

				}.execute();
			}
		});
		dev_share_result.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getActivity().finish();
			}
		});
		quick_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent pet = getActivity().getIntent();
				int devid = pet.getIntExtra("id_devid", 0);
				String username = pet.getStringExtra("owner");
				Intent intent=new Intent(getActivity(), QuickmarkActivity.class);
				intent.putExtra("devid_puick", devid);
				intent.putExtra("owner_quick", username);
				startActivity(intent);
			}
		});
		return v;
	}
}
