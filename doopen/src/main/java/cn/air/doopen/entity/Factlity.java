package cn.air.doopen.entity;
/**初始化参数属性；*/
public class Factlity {
	private String factlityName;//设备名称；
	private String imgurl;//图标；
	private String isstate;
	public Factlity() {
	}
	public String getFactlityName() {
		return factlityName;
	}
	public void setFactlityName(String factlityName) {
		this.factlityName = factlityName;
	}
	public String getImgurl() {
		return imgurl;
	}
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
	public String getIsstate() {
		return isstate;
	}
	public void setIsstate(String isstate) {
		this.isstate = isstate;
	}
	public Factlity(String factlityName, String imgurl, String isstate) {
		super();
		this.factlityName = factlityName;
		this.imgurl = imgurl;
		this.isstate = isstate;
	}

	@Override
	public String toString() {
		return "Factlity [factlityName=" + factlityName + ", imgurl=" + imgurl + ", isstate=" + isstate + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Factlity other = (Factlity) obj;
		if (factlityName == null) {
			if (other.factlityName != null)
				return false;
		} else if (!factlityName.equals(other.factlityName))
			return false;
		if (imgurl == null) {
			if (other.imgurl != null)
				return false;
		} else if (!imgurl.equals(other.imgurl))
			return false;
		if (isstate != other.isstate)
			return false;
		return true;
	}

}
