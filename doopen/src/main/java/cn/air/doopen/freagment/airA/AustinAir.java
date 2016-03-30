package cn.air.doopen.freagment.airA;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import cn.air.doopen.hy.R;
import android.webkit.WebStorage;
import android.webkit.WebView;
/**海一的商城；*/
import android.webkit.WebViewClient;
public class AustinAir extends Fragment {

	private WebView mWebView;

	private WebViewClient mWebViewClient = new WebViewClient() {
		// 处理页面导航
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			mWebView.loadUrl(url);
			// 记得消耗掉这个事件。给不知道的朋友再解释一下，
			// Android中返回True的意思就是到此为止吧,事件就会不会冒泡传递了，我们称之为消耗掉
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}
	};


	private WebChromeClient mChromeClient = new WebChromeClient() {

		private View myView = null;
		private CustomViewCallback myCallback = null;

		// 配置权限 （在WebChromeClinet中实现）
		@Override
		public void onGeolocationPermissionsShowPrompt(String origin,
				GeolocationPermissions.Callback callback) {
			callback.invoke(origin, true, false);
			super.onGeolocationPermissionsShowPrompt(origin, callback);
		}

		// 扩充数据库的容量（在WebChromeClinet中实现）
		@Override
		public void onExceededDatabaseQuota(String url,
				String databaseIdentifier, long currentQuota,
				long estimatedSize, long totalUsedQuota,
				WebStorage.QuotaUpdater quotaUpdater) {

			quotaUpdater.updateQuota(estimatedSize * 2);
		}

		// 扩充缓存的容量
		@Override
		public void onReachedMaxAppCacheSize(long spaceNeeded,
				long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {

			quotaUpdater.updateQuota(spaceNeeded * 2);
		}

		// Android 使WebView支持HTML5 Video（全屏）播放的方法
		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			if (myCallback != null) {
				myCallback.onCustomViewHidden();
				myCallback = null;
				return;
			}

			ViewGroup parent = (ViewGroup) mWebView.getParent();
			parent.removeView(mWebView);
			parent.addView(view);
			myView = view;
			myCallback = callback;
			mChromeClient = this;
		}

		@Override
		public void onHideCustomView() {
			if (myView != null) {
				if (myCallback != null) {
					myCallback.onCustomViewHidden();
					myCallback = null;
				}

				ViewGroup parent = (ViewGroup) myView.getParent();
				parent.removeView(myView);
				parent.addView(mWebView);
				myView = null;
			}
		}
	};

	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("deprecation")
	private void initSettings() {

//		getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE); //设置标题栏样式
//		getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //全屏

		WebSettings webSettings = mWebView.getSettings();
		// 开启Javascript脚本
		webSettings.setJavaScriptEnabled(true);

		// 启用localStorage 和 essionStorage
		webSettings.setDomStorageEnabled(true);

		// 开启应用程序缓存
		webSettings.setAppCacheEnabled(true);
		String appCacheDir = getActivity()
				.getDir("cache", Context.MODE_PRIVATE).getPath();
		webSettings.setAppCachePath(appCacheDir);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSettings.setAppCacheMaxSize(1024 * 1024 * 10);// 设置缓冲大小，我设的是10M
		webSettings.setAllowFileAccess(true);

		// 启用Webdatabase数据库
		webSettings.setDatabaseEnabled(true);
		String databaseDir = getActivity().getDir("database", Context.MODE_PRIVATE).getPath();
		webSettings.setDatabasePath(databaseDir);// 设置数据库路径

		// 启用地理定位
		webSettings.setGeolocationEnabled(true);
		// 设置定位的数据库路径
		webSettings.setGeolocationDatabasePath(databaseDir);

		// 开启插件（对flash的支持）
		webSettings.setRenderPriority(RenderPriority.HIGH);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

		mWebView.setWebChromeClient(mChromeClient);
		mWebView.setWebViewClient(mWebViewClient);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.air_store_layout, null);
		mWebView=(WebView)v.findViewById(R.id.air_store);
		this.initSettings();
		mWebView.loadUrl("http://www.1314788.com/");
//		mWebView.loadUrl("file:///android_asset/index.html");
		return v;
	}
}



//requestWindowFeature(Window.FEATURE_NO_TITLE); //设置标题栏样式
//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //全屏
//
//setContentView(R.layout.activity_main);
//mWebView = (WebView) findViewById(R.id.webview);
//
//WebSettings webSettings = mWebView.getSettings();
//// 开启Javascript脚本
//webSettings.setJavaScriptEnabled(true);
//
//// 启用localStorage 和 essionStorage
//webSettings.setDomStorageEnabled(true);
//
//// 开启应用程序缓存
//webSettings.setAppCacheEnabled(true);
//String appCacheDir = this.getApplicationContext()
//        .getDir("cache", Context.MODE_PRIVATE).getPath();
//webSettings.setAppCachePath(appCacheDir);
//webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//webSettings.setAppCacheMaxSize(1024 * 1024 * 10);// 设置缓冲大小，我设的是10M
//webSettings.setAllowFileAccess(true);
//
//// 启用Webdatabase数据库
//webSettings.setDatabaseEnabled(true);
//String databaseDir = this.getApplicationContext()
//        .getDir("database", Context.MODE_PRIVATE).getPath();
//webSettings.setDatabasePath(databaseDir);// 设置数据库路径
//
//// 启用地理定位
//webSettings.setGeolocationEnabled(true);
//// 设置定位的数据库路径
//webSettings.setGeolocationDatabasePath(databaseDir);
//
//// 开启插件（对flash的支持）
//webSettings.setPluginsEnabled(true);
//webSettings.setRenderPriority(RenderPriority.HIGH);
//webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//
//mWebView.setWebChromeClient(mChromeClient);
//mWebView.setWebViewClient(mWebViewClient);
//}
//
//@Override
//protected void onCreate(Bundle savedInstanceState) {
//super.onCreate(savedInstanceState);
//
//this.initSettings();
//
//mWebView.loadUrl("http://192.168.1.14/Heaven");
//}
//}





//1.跳转到浏览器直接访问页面，这段代码是在Activity中拷贝来的，所以有startActivity()方法
//Uri uri = Uri.parse("http://www.XXXX.com"); //要链接的地址
//Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//startActivity(intent);
//2.使用TextView显示HTML方法
//TextView text1 = (TextView)findViewById(R.id.TextView02);
//text1.setText(Html.fromHtml(“<font size='20'>网页内容</font>”));
//3.直接使用android中自带的显示网页组件WebView
//webview = (WebView) findViewById(R.id.WebView01);
//webview.getSettings().setJavaScriptEnabled(true);
//webview.loadUrl("http://www.xxxx.com");
//4 显示本地html
//@1
//webview = (WebView) findViewById(R.id.webview); 
//webview.getSettings().setJavaScriptEnabled(true); 
//webview.loadUrl("content://com.android.htmlfileprovider/sdcard/index.html");
//@2
//Uri uri = Uri.parse("content://com.android.htmlfileprovider/sdcard/01.htm");
//Intent intent = new Intent();
//intent.setData(uri);
//intent.setClassName("com.android.htmlviewer", "com.android.htmlviewer.HTMLViewerActivity");
//startActivity(intent);
//
//@3
//String encoding = "UTF-8";
//String mimeType = "text/html";
//final String html = 
//"<p><a href=\"file:///sdcard/web/acdf2705\">链接google</a></p>"+ 
//"<p><a href=\"file:///sdcard/ebook/user_defined/browser/localweb/\532fa8dc\"& gt;链接google</a></p>"; 
//mWebView.loadDataWithBaseURL("file://", html,mimeType, encoding, "about:blank");