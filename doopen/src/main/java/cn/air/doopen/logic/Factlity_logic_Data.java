package cn.air.doopen.logic;

import java.util.ArrayList;
import java.util.List;

import cn.air.doopen.entity.Factlity;
import cn.air.doopen.modle.Factliity_Data;

public class Factlity_logic_Data {
	//设备赋值；项目中没有用到；
	public List<Factlity> getfactlogic(){
		Factliity_Data factliity_Data=new Factliity_Data();
		List<Factlity> factlities=new ArrayList<Factlity>();
		Factlity acy=factliity_Data.getfactlitydata();
		for (int i = 0; i <50; i++) {
			Factlity msg = null;
			if(i%3==0){
				//				msg = new Factlity("", "http://thumbs.dreamstime.com/z/-%D0%DE%B7%FE%CE%F1%BA%CD%B7%FE%CE%F1%CD%BC%B1%EA-28214652.jpg", "");
			}else if(i%3==1){
				//				msg = new Factlity("", "http://www.xiazaizhijia.com/uploads/allimg/140320/87-140320154539443.jpg","");
			}else{
				//				msg = new Factlity("", "http://ico.ooopic.com/ajax/iconpng/?id=134581.png","");
			}
			factlities.add(msg);
		}
		return factlities;
	}
}
