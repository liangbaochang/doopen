package cn.air.doopen.hy.personage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.air.doopen.hy.R;
import cn.air.doopen.logic.ControlToOfficialOrTestServer;
import cn.air.doopen.socket.IotUser;
import cn.air.doopen.utli.MyLog;
import cn.air.doopen.utli.T;
import cn.air.doopen.view.CircleImageView;
import cn.air.doopen.view.CustomProgressDialog;
import cn.air.doopen.view.propupwindowview.Choosebirthday;
import cn.air.doopen.view.propupwindowview.City_select_class;
import cn.air.doopen.view.propupwindowview.SexWindow;
import net.tsz.afinal.FinalBitmap;
/**用户中心界面；；*/
@ContentView(value = R.layout.activity_user_center)
public class UserCenterActivity extends Activity {
	@ViewInject(R.id.myuser_result)     /////
	private Button myuser_result;                 /////完成；
	@ViewInject(R.id.myuser_finsh)     /////
	private Button myuser_finsh;                 /////确定；

	@ViewInject(R.id.myuser_img)     /////
	private CircleImageView  myuser_img;                 /////头像；

	@ViewInject(R.id.myuser_sc)     /////
	private LinearLayout  myuser_sc;                 /////

	@ViewInject(R.id.site_text)     /////地址；
	private LinearLayout  site_text;                 ////

	@ViewInject(R.id.iblbry)     /////生日；
	private LinearLayout  iblbry;                 ////

	@ViewInject(R.id.sex_linelayout)     /////性别；
	private LinearLayout  sex_linelayout;                 ////

	@ViewInject(R.id.myuser_name)     /////
	private EditText myuser_name;                 /////    呢称
	@ViewInject(R.id.myuser_sex)     /////
	private TextView myuser_sex;                 /////性别
	@ViewInject(R.id.myuser_phone)     /////
	private EditText myuser_phone;                 /////手机号
	@ViewInject(R.id.myuser_birthday)     /////
	private TextView myuser_birthday;                 /////生日
	@ViewInject(R.id.myuser_toname)     /////
	private EditText myuser_toname;                 /////真实姓名；
	@ViewInject(R.id.myuser_email)     /////
	private EditText myuser_email;                 /////邮箱；
	@ViewInject(R.id.myuser_site)     /////
	private TextView myuser_site;                 /////地址；
	@ViewInject(R.id.Address_text)     /////
	private TextView Address_text;                 /////详细地址；
	private List<Map<String, Object>> muserlist = new ArrayList<Map<String, Object>>();
	static PopupWindow mpopupWindow ;
	public final static int CAMERA_RESULT=8888;  
	public final static int CAMERA_XUANZETUPIAN=8080;  
	protected static final int IMAGE_HANDLER = 77;
	protected static final int ADD_HANDLER = 88;
	int flagsp = -1;
	private String fileuri;
	private Bitmap bitmap;
	private int sexcode=-1;
	private uploadingTask task;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		getdata();
	}
	@OnClick({ R.id.myuser_result,R.id.myuser_finsh,R.id.myuser_img,R.id.site_text,R.id.iblbry,R.id.sex_linelayout})
	public void onclickview(View v){
		switch (v.getId()) {
		case  R.id.sex_linelayout:                         //性别；
			SexWindow sex=new SexWindow(UserCenterActivity.this); 
			sex.showPopMenu( sex_linelayout,myuser_sex);           
			break;
		case  R.id.myuser_img:                         //图片
			showPopMenu(myuser_img);
			break;
		case  R.id.iblbry:                                 //生日
			Choosebirthday bi=new Choosebirthday(UserCenterActivity.this); 
			bi.showPopMenu( iblbry,myuser_birthday);              
			break;
		case  R.id.site_text:                              //地址
			City_select_class id=new City_select_class(UserCenterActivity.this);
			id.showPopMenu(myuser_site, site_text);
			break;
		case  R.id.myuser_result:                     //返回
			finish();
			break;
		case  R.id.myuser_finsh:             //确定修改
			new  AsyncTask<Integer, Void, Integer>(){
				@Override
				protected Integer doInBackground(Integer... result) {
					SharedPreferences a= getSharedPreferences("sex_msg",
							Activity.MODE_PRIVATE);
					sexcode =a.getInt("sex_code", -1);
					//修改用户信息；
					IotUser user=new IotUser(getApplicationContext());
					int code = user.adduserMsg(myuser_name.getText().toString(), sexcode, myuser_birthday.getText().toString(), myuser_email.getText().toString(), myuser_toname.getText().toString(), myuser_site.getText().toString());
					return code;
				}
				@Override
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					if (result==0) {
						Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
						finish();
					}else{
						Toast.makeText(getApplicationContext(), "修改失败", Toast.LENGTH_SHORT).show();
					}
				};
			}.execute();
			break;
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		if (task!=null) {
			//关闭；
			MyLog.i("已关闭");
			task.cancel(true);
		}
	}
	//showPopMenu
	private void getdata() {
		if(!this.isTaskRoot()) { //判断该Activity是不是任务空间的源Activity，“非”也就是说是被系统重新实例化出来
			//如果你就放在launcher Activity中话，这里可以直接return了
			Intent mainIntent=getIntent(); 
			String action=mainIntent.getAction();
			if(mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
				finish();
				return;//finish()之后该活动会继续执行后面的代码，你可以logCat验证，加return避免可能的exception
			}
		}
		//下载显示头像；
		FinalBitmap fb = FinalBitmap.create(UserCenterActivity.this);//
		String imgurl=new ControlToOfficialOrTestServer().Avatar_Downloader_url(new IotUser(UserCenterActivity.this).getToken());
		MyLog.i(imgurl);
		fb.display(myuser_img,imgurl);
		//获取用户信息显示；
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... arg0) {
				IotUser user=new IotUser(getApplicationContext());
				muserlist=user.getuserMsg();
				return null;
			}
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if (muserlist!=null) {
					myuser_sc.setVisibility(View.VISIBLE);
					myuser_name.setText(""+muserlist.get(0).get("nick"));
					sexcode=(Integer) muserlist.get(0).get("sex");
					if (sexcode==0) {
						myuser_sex.setText("保密");
					}
					if (sexcode==1) {
						myuser_sex.setText("男");
					}
					if (sexcode==2) {
						myuser_sex.setText("女");
					}
					myuser_phone.setText(""+muserlist.get(0).get("phone"));
					myuser_birthday.setText(""+muserlist.get(0).get("birthday"));
					myuser_toname.setText(""+muserlist.get(0).get("realname"));
					myuser_email.setText(""+muserlist.get(0).get("email"));
					myuser_site.setText(""+muserlist.get(0).get("addr"));
				}else{
					myuser_sc.setVisibility(View.VISIBLE);
				}
			}
		}.execute();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	//选择相册还是拍照的PopupWindow
	@SuppressWarnings("deprecation")
	public  void showPopMenu(CircleImageView btn) {
		View view = View.inflate(getApplicationContext(), R.layout.select_image, null);
		Button myuser_delete_select = (Button) view.findViewById(R.id.myuser_delete_select);
		RelativeLayout myuser_album = (RelativeLayout) view.findViewById(R.id.myuser_album);
		RelativeLayout myuser_photograph = (RelativeLayout) view.findViewById(R.id.myuser_photograph);
		RelativeLayout myuser_relativelayotu = (RelativeLayout) view.findViewById(R.id.myuser_relativelayotu);
		myuser_delete_select.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mpopupWindow.dismiss();
				backgroundAlpha(1f);
			}
		});
		myuser_album.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent,CAMERA_XUANZETUPIAN);
				mpopupWindow.dismiss();
				backgroundAlpha(1f);
			}
		});

		myuser_photograph.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
				startActivityForResult(intent, CAMERA_RESULT);  
				mpopupWindow.dismiss();
				backgroundAlpha(1f);
			}
		});

		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mpopupWindow.dismiss();
				backgroundAlpha(1f);
			}
		});

		view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
		myuser_relativelayotu.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_bottom_in));
		if(mpopupWindow==null){
			mpopupWindow = new PopupWindow(getApplicationContext());
			mpopupWindow.setWidth(LayoutParams.MATCH_PARENT);
			mpopupWindow.setHeight(LayoutParams.MATCH_PARENT);
			mpopupWindow.setBackgroundDrawable(new BitmapDrawable());
			mpopupWindow.setFocusable(true);
			mpopupWindow.setOutsideTouchable(true);
		}
		mpopupWindow.setContentView(view);
		mpopupWindow.showAtLocation(btn, Gravity.BOTTOM, 0, 0);
		mpopupWindow.update();
		backgroundAlpha(0.5f);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			backgroundAlpha(1f);
			if (resultCode==RESULT_OK) {
				if (requestCode==CAMERA_RESULT) {  
					String sdStatus = Environment.getExternalStorageState();  
					if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用  
						return;  
					}  
					new DateFormat();
					String name = DateFormat.format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA)) + ".jpg";     
					if (data!=null) {
						Bundle bundle = data.getExtras();  
						bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式  
						FileOutputStream b = null;  
						File file = new File("/sdcard/myImage/");  
						file.mkdirs();// 创建文件夹  
						String fileName = "/sdcard/myImage/"+name;  
						fileuri=fileName;
						try {  
							b = new FileOutputStream(fileName);  
							bitmap.compress(Bitmap.CompressFormat.JPEG, 10, b);// 把数据写入文件  
							// 把bitmap变成byte[];
							ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
							// 判断图的高度，宽度特别大，要缩小，压缩，否则，
							bitmap.compress(CompressFormat.JPEG, 10, outputStream);
							myuser_img.setImageBitmap(bitmap);
							task=new uploadingTask();
							task.execute();
							MyLog.i("上传中");
						} catch (FileNotFoundException e) {  
							e.printStackTrace();  
						} finally {  
							try {  
								b.flush();  
								b.close();  
							} catch (IOException e) {  
								e.printStackTrace();  
							}  
						}  
					}
				}else{
					ContentResolver resolver = this.getContentResolver();
					String[] pojo = {MediaStore.Images.Media.DATA};
					Uri uri = data.getData();
					CursorLoader cursorLoader = new CursorLoader(this, uri, pojo, null,null, null);
					Cursor cursor = cursorLoader.loadInBackground();
					cursor.moveToFirst(); 
					String path = cursor.getString(cursor.getColumnIndex(pojo[0]));
					if (path != null && path.length() > 0) {
						String picPath = path;
						fileuri=picPath;
					}
					bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);
					// 把bitmap变成byte[]
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					// 判断图的高度，宽度特别大，要缩小，压缩，否则，oom
					bitmap.compress(CompressFormat.JPEG, 10, outputStream);
					myuser_img.setImageBitmap(bitmap);
					task=new uploadingTask();
					task.execute();
					MyLog.i("上传中");
					super.onActivityResult(requestCode, resultCode, data);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/** 
	 * 设置添加屏幕的背景透明度 
	 * @param bgAlpha 
	 */  
	public void backgroundAlpha(float bgAlpha)  
	{  
		WindowManager.LayoutParams lp = getWindow().getAttributes();  
		lp.alpha=bgAlpha;
		getWindow().setAttributes(lp);  
	}  
	//头像上传；
	class uploadingTask extends AsyncTask<Integer, Integer, Integer>{
		private CustomProgressDialog dialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// 进度条对话框；
			dialog = new CustomProgressDialog(UserCenterActivity.this);
			dialog.show();
		}
		@Override
		protected Integer doInBackground(Integer... arg0) {
			IotUser user=new IotUser(getApplicationContext());
			int imgcode = 0;
			if (fileuri!=null) {
				imgcode=user.uploadingimg(fileuri);
			}
			return imgcode;
		}
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);          
			dialog.dismiss();
			if (result==0) {
				T.setTost(UserCenterActivity.this, "修改头像成功");
				//下载显示头像；
				FinalBitmap fb = FinalBitmap.create(UserCenterActivity.this);//
				String imgurl=new ControlToOfficialOrTestServer().Avatar_Downloader_url(new IotUser(UserCenterActivity.this).getToken());
				MyLog.i(imgurl);
				fb.display(myuser_img,imgurl);
			}else{
				T.setTost(UserCenterActivity.this, "修改头像失败");
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		backgroundAlpha(1f);
		return super.onKeyDown(keyCode, event);
	}

}    
