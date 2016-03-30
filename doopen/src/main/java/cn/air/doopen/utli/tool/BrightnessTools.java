package cn.air.doopen.utli.tool;

import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import cn.air.doopen.utli.MyLog;  
/**工具类*/
public class BrightnessTools {  
	/* Android的屏幕亮度好像在2.1+的时候提供了自动调节的功能，  
	 * 所以，如果当开启自动调节功能的时候， 我们进行调节好像是没有一点作用的，  
	 * 这点让我很是无语，结果只有进行判断，看是否开启了屏幕亮度的自动调节功能。  
	 */  

	/** * 判断是否开启了自动亮度调节 */  

	public static boolean isAutoBrightness(ContentResolver aContentResolver) {      
		boolean automicBrightness = false;      
		try{          
			automicBrightness = Settings.System.getInt(aContentResolver,                  
					Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;     
		}   
		catch(SettingNotFoundException e)   
		{         
			e.printStackTrace();    
		}      

		return automicBrightness;  
	}  
	//然后就是要觉得当前的亮度了，这个就比较纠结了：  

	/** * 获取屏幕的亮度 */  

	public static int getScreenBrightness(Activity activity) {     
		int nowBrightnessValue = 0;      
		ContentResolver resolver = activity.getContentResolver();      
		try{          
			nowBrightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);    
		}  
		catch(Exception e) {         
			e.printStackTrace();    
		}      
		return nowBrightnessValue;  
	}  

	//那如何修改屏幕的亮度呢？  



	/** * 设置亮度 */  

	public static void setBrightness(Activity activity, int brightness) {     

		// Settings.System.putInt(activity.getContentResolver(),      

		// Settings.System.SCREEN_BRIGHTNESS_MODE,      

		// Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);      

		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();     

		lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);    
		MyLog.d("lxy", "set  lp.screenBrightness == " + lp.screenBrightness);  

		activity.getWindow().setAttributes(lp);   
	}  
	//那么，能设置了，但是为什么还是会出现，设置了，没反映呢？  
	//嘿嘿，那是因为，开启了自动调节功能了，那如何关闭呢？这才是最重要的：  
	/** * 停止自动亮度调节 */  
	public static void stopAutoBrightness(Activity activity) {     

		Settings.System.putInt(activity.getContentResolver(),            

				Settings.System.SCREEN_BRIGHTNESS_MODE,             

				Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);  
	}  
	//能开启，那自然应该能关闭了哟哟，那怎么关闭呢？很简单的：  

	/** * 开启亮度自动调节 *    

	 * @param activity */  

	public static void startAutoBrightness(Activity activity) {     

		Settings.System.putInt(activity.getContentResolver(),             

				Settings.System.SCREEN_BRIGHTNESS_MODE,              

				Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);  

	}  

	//至此，应该说操作亮度的差不多都有了，结束！  
	//哎，本来认为是应该结束了，但是悲剧得是，既然像刚才那样设置的话，只能在当前的activity中有作用，一段退出的时候，会发现毫无作用，悲剧，原来是忘记了保存了。汗！  

	/** * 保存亮度设置状态 */  
	public static void saveBrightness(ContentResolver resolver, int brightness) {      


		Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");     

		android.provider.Settings.System.putInt(resolver, "screen_brightness", brightness);      

		// resolver.registerContentObserver(uri, true, myContentObserver);     

		resolver.notifyChange(uri, null);
	}  
	// 生成QR图
	public static Bitmap createImage(String url) {
		int QR_WIDTH = 400, QR_HEIGHT = 400;
		Bitmap bitmap = null;
		try {
			// 需要引入core包
			QRCodeWriter writer = new QRCodeWriter();

			String text = url;
			if (text == null || "".equals(text) || text.length() < 1) {
				return null;
			}
			// 把输入的文本转为二维码
			BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE,
					QR_WIDTH, QR_HEIGHT);

			System.out.println("w:" + martix.getWidth() + "h:"
					+ martix.getHeight());

			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(text,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}

				}
			}
			bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	//指定edittext打开软键盘；
	public static void showSoftInput(EditText et) {
		MyLog.i("showSoftInput");
		et.requestFocus();
		InputMethodManager imm = (InputMethodManager) et.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(et, InputMethodManager.RESULT_UNCHANGED_SHOWN);
	}
	/** 
	 * 获取当前的网络状态 -1：没有网络 1：WIFI网络2：wap网络3：net网络 
	 */ 
	public static int getAPNType(Context context){ 
		int netType = -1; 
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); 
		if(networkInfo==null){ 
			return netType; 
		} 
		int nType = networkInfo.getType(); 
		if(nType==ConnectivityManager.TYPE_MOBILE){ 
			MyLog.e("networkInfo.getExtraInfo()", "networkInfo.getExtraInfo() is "+networkInfo.getExtraInfo()); 
			if(networkInfo.getExtraInfo().toLowerCase().equals("cmnet")){ 
				netType = 3; 
			} 
			else{ 
				netType = 2; 
			} 
		} 
		else if(nType==ConnectivityManager.TYPE_WIFI){ 
			netType = 1; 
		} 
		return netType; 
	}
	/**
	 * 检测网络是否可用
	 * @return
	 */
	public boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}
	
}  
