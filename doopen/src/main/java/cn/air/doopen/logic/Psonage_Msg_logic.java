package cn.air.doopen.logic;

import java.util.ArrayList;
import java.util.List;

import cn.air.doopen.entity.Gr_Message;
import cn.air.doopen.modle.Psonage_Msg_modle;

public class Psonage_Msg_logic {
//项目中么有用到；测试专用；
	public List<Gr_Message> getfactlogic(){
		Psonage_Msg_modle factliity_Data=new Psonage_Msg_modle();
		List<Gr_Message> factlities=new ArrayList<Gr_Message>();
		Gr_Message acy=factliity_Data.getgrMsgdata();
		for (int i = 0; i <50; i++) {
			Gr_Message msg = null;
			if(i%3==0){
				msg = new Gr_Message("英联智能净水器", "http://thumbs.dreamstime.com/z/-%D0%DE%B7%FE%CE%F1%BA%CD%B7%FE%CE%F1%CD%BC%B1%EA-28214652.jpg", "已拒绝", "请求分享设备给您");
			}else if(i%3==1){
				msg = new Gr_Message("智能洒水器", "http://www.xiazaizhijia.com/uploads/allimg/140320/87-140320154539443.jpg","已同意", "请求分享设备给您");
			}else{
				msg = new Gr_Message("智能空气净化器", "http://ico.ooopic.com/ajax/iconpng/?id=134581.png","等待同意", "请求分享设备给您");
			}
			factlities.add(msg);
		}
		return factlities;
	}
}
