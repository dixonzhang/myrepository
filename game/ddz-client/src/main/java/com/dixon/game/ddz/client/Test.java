package com.dixon.game.ddz.client;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.ObjectMapper;

import com.dixon.game.ddz.common.bean.DeskListView;
import com.dixon.game.ddz.common.resp.DeskListRes;

public class Test {

	public static void main(String[] args) {
		
		String str = "{\"desks\":[{\"num\":1,\"pulyerCount\":0},{\"num\":2,\"pulyerCount\":0},{\"num\":3,\"pulyerCount\":0},{\"num\":4,\"pulyerCount\":0},{\"num\":5,\"pulyerCount\":0},{\"num\":6,\"pulyerCount\":0},{\"num\":7,\"pulyerCount\":0},{\"num\":8,\"pulyerCount\":0},{\"num\":9,\"pulyerCount\":0},{\"num\":10,\"pulyerCount\":0}],\"respDesc\":\"获取桌列表成功\",\"respType\":\"deskList\"}";
		
		Pattern p = Pattern.compile("\"respType\":\"(\\w+)\"");
		Matcher m = p.matcher(str);
		
		if(m.find()){
			System.out.println(m.group(1));
		}
		
		
		
//		ObjectMapper om = new ObjectMapper();
//		try {
////			BaseRes baseRes = om.readValue(str, BaseRes.class);
////			System.out.println(baseRes.getRespType());
//			
//			
//			final DeskListRes viewList = (DeskListRes) om.readValue(str, DeskListRes.class);
//			
//			String[] items = new String[viewList.getDesks().size()];
//			for(int i = 0; i < viewList.getDesks().size(); i++){
//				DeskListView view = viewList.getDesks().get(i);
//				items[i] = view.getNum() + " (" + view.getPulyerCount() + ")";
//				
//				System.out.println(items[i]);
//			}
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}

}
