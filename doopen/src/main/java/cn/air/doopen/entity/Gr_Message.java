package cn.air.doopen.entity;
/**初始化参数属性；*/
public class Gr_Message {
	public Gr_Message() {
	}
	private String factlityName_msg;
	private String imgurl_msg;
	private String isstate_msg;
	private String context_msg;
	public String getFactlityName_msg() {
		return factlityName_msg;
	}
	public void setFactlityName_msg(String factlityName_msg) {
		this.factlityName_msg = factlityName_msg;
	}
	public String getImgurl_msg() {
		return imgurl_msg;
	}
	public void setImgurl_msg(String imgurl_msg) {
		this.imgurl_msg = imgurl_msg;
	}
	public String getIsstate_msg() {
		return isstate_msg;
	}
	public void setIsstate_msg(String isstate_msg) {
		this.isstate_msg = isstate_msg;
	}
	public String getContext_msg() {
		return context_msg;
	}
	public void setContext_msg(String context_msg) {
		this.context_msg = context_msg;
	}
	public Gr_Message(String factlityName_msg, String imgurl_msg, String isstate_msg, String context_msg) {
		super();
		this.factlityName_msg = factlityName_msg;
		this.imgurl_msg = imgurl_msg;
		this.isstate_msg = isstate_msg;
		this.context_msg = context_msg;
	}
	@Override
	public String toString() {
		return "Gr_Message [factlityName_msg=" + factlityName_msg + ", imgurl_msg=" + imgurl_msg + ", isstate_msg="
				+ isstate_msg + ", context_msg=" + context_msg + "]";
	}

}
