package cn.air.doopen.hy.share;
import cn.air.doopen.hy.R;
import cn.air.doopen.hyapp.MyApp;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class Social_share {
	public static void showShare() {
		ShareSDK.initSDK(MyApp.getContext());
		OnekeyShare oks = new OnekeyShare();
		//关闭sso授权
		oks.disableSSOWhenAuthorize(); 
		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		//oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(MyApp.getContext().getString(R.string.share));
		oks.setTitleUrl("http://app.vikduo.com/szdoopen/");
		oks.setText("我在用APP控制智能多朋空气净化器,快来加入我们吧！");
		oks.setImageUrl("http://img5.imgtn.bdimg.com/it/u=2531989999,1630875868&fm=21&gp=0.jpg");
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://app.vikduo.com/szdoopen/");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我在用APP控制智能多朋空气净化器,快来加入我们吧！");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(MyApp.getContext().getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://app.vikduo.com/szdoopen/");
		// 启动分享GUI
		oks.show(MyApp.getContext());
	}
}
